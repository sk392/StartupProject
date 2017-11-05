package com.sk392.kr.carmony.Fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Activity.MainActivity;
import com.sk392.kr.carmony.Activity.OwnerCarInfoActivity;
import com.sk392.kr.carmony.Activity.OwnerProfileActivity;
import com.sk392.kr.carmony.Activity.OwnerScheduleCarActivity;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OwnerRegisteredFragment extends Fragment implements View.OnClickListener {
    ImageButton ibOwnerProfile;
    Button btOwnerCarInfo, btOwnerSchedule;
    JSONObject jsonResult;
    private static final String TAG = "OwnerRegisteredFragment";

    String err;
    public static ImageView ivOwnerPageProfile;
    TextView tvOwnerPageName, tvOwnerPageResNum, tvOwnerPageReviewScore, tvOwnerPageTotalIncome, tvOwnerPageTotalRes, tvOwnerPageMonthIncome, tvOwnerPageMonthRes;
    RatingBar rbOwnerPage;
    //    TextView tvOwnerPageReviews;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        View mView = inflater.inflate(R.layout.fragment_owner_registered, container, false);
        bindingXml(mView);
        getJsonResult();

        // Inflate the layout for this fragment
        return mView;
    }

    private void getJsonResult() {
        SendPost sendPost = new SendPost(getActivity());
        sendPost.setUrl(getString(R.string.url_get_user_payment_list));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    jsonResult = new JSONObject(result);
                    Log.d("registerred", jsonResult.toString());
                    err = jsonResult.getString("err");
                    if (err.equals("0")) {
                        try {

                            if (jsonResult.getInt("cnt") > 0) {
                                //예약이 있는 경우

                                editor.putString(getString(R.string.shared_ownerincome),jsonResult.getString("total_income"));
                                editor.putString(getString(R.string.shared_ownerrescnt),jsonResult.getString("cnt"));
                                editor.putString(getString(R.string.shared_ownermonthincome),jsonResult.getString("this_month_income"));
                                editor.putString(getString(R.string.shared_ownermonthrescnt),jsonResult.getString("this_month_res"));
                                editor.commit();


                            } else {
                                editor.putString(getString(R.string.shared_ownerincome),"0");
                                editor.putString(getString(R.string.shared_ownerrescnt),"0");
                                editor.putString(getString(R.string.shared_ownermonthincome),"0");
                                editor.putString(getString(R.string.shared_ownermonthrescnt),"0");
                                editor.commit();
                            }
                            dataSetup();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                            FirebaseCrash.report(e);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);

                }

            }
        });
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), ""));
    }


    private void bindingXml(View view) {
        ibOwnerProfile = (ImageButton) getActivity().findViewById(R.id.ib_toolbar_owner);
        ivOwnerPageProfile = (ImageView) view.findViewById(R.id.iv_owner_page_profile);

        ibOwnerProfile.setOnClickListener(this);
        btOwnerCarInfo = (Button) view.findViewById(R.id.bt_owner_car_info);
        btOwnerCarInfo.setOnClickListener(this);
        btOwnerSchedule = (Button) view.findViewById(R.id.bt_owner_schedule);
        btOwnerSchedule.setOnClickListener(this);
        tvOwnerPageName = (TextView) view.findViewById(R.id.tv_owner_page_name);
        tvOwnerPageResNum = (TextView) view.findViewById(R.id.tv_owner_page_res_num);
        // tvOwnerPageReviews = (TextView) view.findViewById(R.id.tv_owner_page_reviews_num);
        tvOwnerPageReviewScore = (TextView) view.findViewById(R.id.tv_owner_pager_review_score);
        tvOwnerPageTotalIncome = (TextView) view.findViewById(R.id.tv_owner_page_total_income);
        tvOwnerPageTotalRes = (TextView) view.findViewById(R.id.tv_owner_page_total_res);
        tvOwnerPageMonthIncome = (TextView) view.findViewById(R.id.tv_owner_page_month_income);
        tvOwnerPageMonthRes = (TextView) view.findViewById(R.id.tv_owner_page_month_res);
    }

    private void dataSetup() {
        Glide.with(getActivity()).load(sharedPreferences.getString(getString(R.string.shared_userprofileurl), ""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(ivOwnerPageProfile);
        Log.d("imageCheck", sharedPreferences.getString(getString(R.string.shared_userprofileurl), ""));
        tvOwnerPageTotalIncome.setText(sharedPreferences.getString(getString(R.string.shared_ownerincome), "") + "원");
        tvOwnerPageTotalRes.setText(sharedPreferences.getString(getString(R.string.shared_ownerrescnt), "") + "번");
        tvOwnerPageMonthIncome.setText(sharedPreferences.getString(getString(R.string.shared_ownermonthincome), "") + "원");
        tvOwnerPageMonthRes.setText(sharedPreferences.getString(getString(R.string.shared_ownermonthrescnt), "") + "번");
        tvOwnerPageResNum.setText(sharedPreferences.getString(getString(R.string.shared_ownerrescnt), ""));
        tvOwnerPageName.setText(sharedPreferences.getString(getString(R.string.shared_name), ""));
        //tvOwnerPageReviews.setText("(" + sharedPreferences.getString(getString(R.string.shared_owner_reviewcnt), "") + ")");
        tvOwnerPageReviewScore.setText("" + sharedPreferences.getFloat(getString(R.string.shared_owner_reviewscore), 0));
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bt_owner_car_info:
                intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(), OwnerCarInfoActivity.class);
                startActivity(intent);
                return;
            case R.id.ib_toolbar_owner:
                intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(), OwnerProfileActivity.class);
                startActivity(intent);
                ((MainActivity) getActivity()).setIsMain(false);
                return;
            case R.id.bt_owner_schedule:
                intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(), OwnerScheduleCarActivity.class);
                startActivity(intent);
                return;
        }
    }

    public static void imageSetup(ContentResolver contentResolver, Uri uri) {
        try {
            Bitmap imageProfile = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            ivOwnerPageProfile.setImageBitmap(imageProfile);
        } catch (IOException e) {
            e.printStackTrace();

            FirebaseCrash.logcat(Log.ERROR, TAG, "IOException Fail");
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        dataSetup();
    }
}
