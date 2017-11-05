package com.sk392.kr.carmony.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.crash.FirebaseCrash;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sk392.kr.carmony.Activity.MainActivity;
import com.sk392.kr.carmony.Activity.UserCouponActivity;
import com.sk392.kr.carmony.Activity.UserEditingContentActivity;
import com.sk392.kr.carmony.Activity.UserReviewsActivity;
import com.sk392.kr.carmony.Activity.UserSetupActivity;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class UserinfoFragment extends Fragment implements View.OnClickListener {
    public static final int REQ_CODE_SELECT_IMAGE = 100;
    public static final int REQ_CODE_CHANGE_INTRO = 101;
    Toolbar tbUserinfo;

    View userinfoView;
    ImageButton ibToolbar;
    RelativeLayout rlUserinfoReviews, rlUserinfoCoupon, rlUserinfoIntro;
    ImageView ivProfile;
    TextView tvUserinfoName, tvUserInfoResNum, tvUserinfoMileage, tvUserinfoReviewNum, tvUserinfoAccident, tvUserinfoIntro;
    //TextView tvUserinfoReviewScore;
    RatingBar rbUserinfoReview, rbUserinfoReviews;
    private static final String TAG = "UserinfoFragment";

    Cursor cursor;
    File profileFile;
    String  uploadFileName, userId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userinfoView = inflater.inflate(R.layout.fragment_userinfo, container, false);
        bindingXml();
        dataSetup();
        return userinfoView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ib_toolbar_user:
                intent.setClass(getActivity().getApplicationContext(), UserSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_userinfo_reviews:
                intent.setClass(getActivity().getApplicationContext(), UserReviewsActivity.class);
                intent.putExtra("title", "유저 리뷰");
                intent.putExtra("resultType", "2");
                intent.putExtra("type", "0");
                intent.putExtra("userReviewCnt", sharedPreferences.getString(getString(R.string.shared_user_reviewcnt), ""));
                intent.putExtra("userReviewScore", sharedPreferences.getFloat(getString(R.string.shared_user_reviewscore), 0));

                startActivity(intent);
                break;
            case R.id.iv_userinfo_profile:

                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Intent imageIntent = new Intent(Intent.ACTION_PICK);
                        imageIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                        imageIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(imageIntent, REQ_CODE_SELECT_IMAGE);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    }


                };


                new TedPermission(getActivity())
                        .setPermissionListener(permissionlistener)
                        .setRationaleMessage("사진을 업데이트하기 위해서는 갤러리 접근 권한이 필요해요..!")
                        .setDeniedMessage("나중에 하고 싶으시면 \n [설정] > [권한]에서 권한을 설정할 수 있어요")
                        .setGotoSettingButtonText("설정")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
                break;
            case R.id.rl_userinfo_intro:
                intent.setClass(getActivity().getApplicationContext(), UserEditingContentActivity.class);
                intent.putExtra("type", "1");
                intent.putExtra("content", tvUserinfoIntro.getText().toString());
                startActivityForResult(intent, REQ_CODE_CHANGE_INTRO);
                break;
            case R.id.rl_userinfo_coupons:
                Intent intent1 = new Intent(getActivity(), UserCouponActivity.class);
                intent1.putExtra("readonly", true);
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String filePath;


                //이미지 회전 방지
                //String filePath = data.getData().getPath();
                //imageProfile = (filePath);

                try {//이미지 데이터를 비트맵으로 받아온다.
                    Uri uriProfile = data.getData();
                    if (sharedPreferences.getString(getString(R.string.shared_type), "").equals("1"))
                        OwnerRegisteredFragment.imageSetup(getActivity().getContentResolver(), data.getData());
                    filePath = getPath(uriProfile);
                    String[] pathArr = filePath.split("/");
                    uploadFileName = pathArr[pathArr.length - 1];
                    File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "Carmony");

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize=8;
                    Bitmap image = BitmapFactory.decodeFile(filePath,options);
                    // 이미지를 상황에 맞게 회전시킨다
                    ExifInterface exif = new ExifInterface(filePath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    image = rotate(image, exifDegree);
                    boolean doSave = true;
                    if (!dir.exists()) {
                        doSave = dir.mkdirs();
                    }
                    ivProfile.setImageBitmap(image);
                    if (doSave)
                        profileFile = saveBitmapToFile(dir, uploadFileName, image);

                } catch (IOException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "IOException Fail");
                    FirebaseCrash.report(e);
                }
                try {
                    SendBird.connect(userId, new SendBird.ConnectHandler() {
                        @Override
                        public void onConnected(User user, SendBirdException e) {
                            if (e != null) {
                                return;
                            }
                            mUser = user;
                            if (profileFile != null) {
                                SendBird.updateCurrentUserInfoWithProfileImage(sharedPreferences.getString(getString(R.string.shared_name), "")
                                        , profileFile
                                        , new SendBird.UserInfoUpdateHandler() {
                                            @Override
                                            public void onUpdated(SendBirdException e) {
                                                editor.putString(getString(R.string.shared_userprofileurl), mUser.getProfileUrl());
                                                editor.commit();
                                                if (e != null)
                                                    e.printStackTrace();

                                                SendPost sendPost = new SendPost(getActivity());
                                                sendPost.setUrl(getString(R.string.url_set_user_profile_url));
                                                sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                                                    @Override
                                                    public void getResult(String result) {
                                                        try {
                                                            JSONObject jsonResult;
                                                            String err;
                                                            jsonResult = new JSONObject(result);
                                                            err = jsonResult.getString("err");
                                                            if (err.equals("0")) {
                                                                MainActivity.showToast(getActivity().getApplicationContext(), "변경되었습니다.");
                                                            } else {
                                                                MainActivity.showToast(getActivity().getApplicationContext(), "일시적으로 서버에 접근할 수 없습니다.");
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                                            FirebaseCrash.report(e);
                                                        }
                                                    }
                                                });

                                                sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                                                        + "&profile_url=" + mUser.getProfileUrl());
                                            }
                                        });
                            } else {
                                MainActivity.showToast(getActivity().getApplicationContext(), "일시적으로 서버에 접근할 수 없습니다.");
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "아멀랑 Fail");
                    FirebaseCrash.report(e);
                }


            }
        } else if (requestCode == REQ_CODE_CHANGE_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                String intro = data.getStringExtra("intro");
                tvUserinfoIntro.setText(intro);
            }
        }
    }

    //경로 획득.
    public String getPath(Uri uri) {
        String path;
        String[] projection = {MediaStore.Images.Media.DATA};

        cursor = getActivity().managedQuery(uri, projection, null, null, null);
        getActivity().startManagingCursor(cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        path = cursor.getString(columnIndex);
        return path;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }

    private void bindingXml() {

        rlUserinfoReviews = (RelativeLayout) userinfoView.findViewById(R.id.rl_userinfo_reviews);
        tbUserinfo = (Toolbar) getActivity().findViewById(R.id.introtoolbar);
        ibToolbar = (ImageButton) tbUserinfo.findViewById(R.id.ib_toolbar_user);
        ivProfile = (ImageView) userinfoView.findViewById(R.id.iv_userinfo_profile);
        tvUserinfoIntro = (TextView) userinfoView.findViewById(R.id.tv_userinfo_intro);
        tvUserinfoAccident = (TextView) userinfoView.findViewById(R.id.tv_userinfo_accident);
        tvUserinfoMileage = (TextView) userinfoView.findViewById(R.id.tv_userinfo_mileage);
        tvUserinfoName = (TextView) userinfoView.findViewById(R.id.tv_userinfo_name);
        tvUserInfoResNum = (TextView) userinfoView.findViewById(R.id.tv_userinfo_res_num);
        tvUserinfoReviewNum = (TextView) userinfoView.findViewById(R.id.tv_userinfo_review_num);
        rbUserinfoReview = (RatingBar) userinfoView.findViewById(R.id.rb_userinfo_review);
        rbUserinfoReviews = (RatingBar) userinfoView.findViewById(R.id.rb_userinfo_reviews);
        rlUserinfoCoupon = (RelativeLayout) userinfoView.findViewById(R.id.rl_userinfo_coupons);
        rlUserinfoIntro = (RelativeLayout) userinfoView.findViewById(R.id.rl_userinfo_intro);
        //tvUserinfoReviewScore = (TextView) userinfoView.findViewById(R.id.tv_userinfo_review_score);
        rlUserinfoCoupon.setOnClickListener(this);
        rlUserinfoIntro.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        rlUserinfoReviews.setOnClickListener(this);
        ibToolbar.setOnClickListener(this);
    }

    private void dataSetup() {
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getString(getString(R.string.shared_userid), "");
//        Log.d("TTTest", sharedPreferences.getString(getString(R.string.shared_userprofileurl), ""));
        Glide.with(getActivity()).load(sharedPreferences.getString(getString(R.string.shared_userprofileurl), ""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(ivProfile);
        tvUserinfoReviewNum.setText(sharedPreferences.getString(getString(R.string.shared_user_reviewcnt), ""));
        tvUserInfoResNum.setText(sharedPreferences.getString(getString(R.string.shared_userrescnt), ""));
        tvUserinfoName.setText(sharedPreferences.getString(getString(R.string.shared_name), ""));
        tvUserinfoMileage.setText(sharedPreferences.getString(getString(R.string.shared_mileage), ""));
        tvUserinfoAccident.setText(sharedPreferences.getString(getString(R.string.shared_accident), ""));
        tvUserinfoIntro.setText(sharedPreferences.getString(getString(R.string.shared_content), ""));
        float fnum = sharedPreferences.getFloat(getString(R.string.shared_user_reviewscore), 0);
        //tvUserinfoReviewScore.setText("" + fnum);
        rbUserinfoReview.setRating(fnum);
        rbUserinfoReviews.setRating(fnum);
    }

    public File saveBitmapToFile(File dir, String fileName, Bitmap bm) {

        File imageFile = new File(dir, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);

            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.close();

            return imageFile;
        } catch (IOException e) {
            Log.e("app", e.getMessage());
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }
    /**
     * 이미지를 회전시킵니다.
     *
     * @param bitmap 비트맵 이미지
     * @param degrees 회전 각도
     * @return 회전된 이미지
     */
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

}
