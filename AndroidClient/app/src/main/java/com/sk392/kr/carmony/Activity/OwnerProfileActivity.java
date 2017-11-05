package com.sk392.kr.carmony.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.sk392.kr.carmony.Fragment.UserinfoFragment;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;


public class OwnerProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    TextView tvToolbar;
    private static final String TAG = "OwnerProfileActivity";

    Toolbar tbOwnerProfile;
    ImageButton ibToolbar;
    ImageView ivOwnerProfile;
    RelativeLayout rlOwnerProfileReviews, rlOwnerProfileIntro;
    TextView tvOwnerProfileName, tvOwnerProfileResNum, tvOwnerProfileScore, tvOwnerProfileReviewNum, tvOwnerProfileAccident, tvOwnerProfileIntro;
    //TextView tvOwnerProfileReviewScore;
    RatingBar rbOwnerProfileReview, rbOwnerProfileReviews;
    String userId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);
        bindingXml();
        getDataUpdate();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ib_toolbar_user:
                intent.setClass(getApplicationContext(), UserSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_owner_profile_reviews:
                intent.setClass(getApplicationContext(), UserReviewsActivity.class);
                intent.putExtra("title", "유저 리뷰");
                intent.putExtra("resultType", "2");
                intent.putExtra("type", "1");
                intent.putExtra("userReviewCnt", sharedPreferences.getString(getString(R.string.shared_owner_reviewcnt), ""));
                intent.putExtra("userReviewScore", sharedPreferences.getFloat(getString(R.string.shared_owner_reviewscore), 0));

                startActivity(intent);
                break;

            case R.id.rl_owner_profile_intro:
                intent.setClass(getApplicationContext(), UserEditingContentActivity.class);
                intent.putExtra("type", "2");
                intent.putExtra("content", tvOwnerProfileIntro.getText().toString());
                startActivityForResult(intent, UserinfoFragment.REQ_CODE_CHANGE_INTRO);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UserinfoFragment.REQ_CODE_CHANGE_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                String intro = data.getStringExtra("intro");
                tvOwnerProfileIntro.setText(intro);
            }
        }
    }


    private void bindingXml() {

        rlOwnerProfileReviews = (RelativeLayout) findViewById(R.id.rl_owner_profile_reviews);
        tbOwnerProfile = (Toolbar) findViewById(R.id.introtoolbar);
        ibToolbar = (ImageButton) tbOwnerProfile.findViewById(R.id.ib_toolbar_user);
        tvOwnerProfileIntro = (TextView) findViewById(R.id.tv_owner_profile_intro);
        tvOwnerProfileAccident = (TextView) findViewById(R.id.tv_owner_profile_accident);
        tvOwnerProfileScore = (TextView) findViewById(R.id.tv_owner_profile_score);
        tvOwnerProfileName = (TextView) findViewById(R.id.tv_owner_profile_name);
        tvOwnerProfileResNum = (TextView) findViewById(R.id.tv_owner_profile_res_num);
        tvOwnerProfileReviewNum = (TextView) findViewById(R.id.tv_owner_profile_review_num);
        rbOwnerProfileReview = (RatingBar) findViewById(R.id.rb_owner_profile_review);
        rbOwnerProfileReviews = (RatingBar) findViewById(R.id.rb_owner_profile_reviews);
        rlOwnerProfileIntro = (RelativeLayout) findViewById(R.id.rl_owner_profile_intro);
        //tvOwnerProfileReviewScore = (TextView) findViewById(R.id.tv_owner_profile_review_score);
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        ivOwnerProfile = (ImageView) findViewById(R.id.iv_owner_profile_profile);
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);

        rlOwnerProfileIntro.setOnClickListener(this);
        rlOwnerProfileReviews.setOnClickListener(this);
        ibToolbar.setOnClickListener(this);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private void getDataUpdate() {
        SendPost spGetOwnerInfo = new SendPost(getApplicationContext());
        spGetOwnerInfo.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;
                    JSONObject jsonResult;
                    jsonResult = new JSONObject(result);
                    err = jsonResult.getString("err");
                    if (err.equals("0")) {
//                                    Log.d(TAG, "LoginProcess -11 getuserinfo err =0");
                        editor.putString(getString(R.string.shared_owner_content), jsonResult.getJSONObject("ret").getString("owner_content"));
                        editor.putString(getString(R.string.shared_accident), jsonResult.getJSONObject("ret").getString("accident"));
                        editor.putString(getString(R.string.shared_owner_reviewcnt), jsonResult.getJSONObject("ret").getString("ow_review_cnt"));
                        editor.putFloat(getString(R.string.shared_owner_reviewscore), jsonResult.getJSONObject("ret").getLong("ow_review_score"));
                        editor.commit();

                        dataSetup();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        spGetOwnerInfo.setLoadingImg(false);
        spGetOwnerInfo.setUrl(getString(R.string.url_get_owner_info));
        spGetOwnerInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), ""));
        SendPost spGetPaymentList = new SendPost(getApplicationContext());
        spGetPaymentList.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;
                    JSONObject jsonResult;
                    jsonResult = new JSONObject(result);
                    err = jsonResult.getString("err");
                    if (err.equals("0")) {

                        editor.putString(getString(R.string.shared_ownerincome),jsonResult.getString("total_income"));
                        editor.putString(getString(R.string.shared_ownerrescnt),jsonResult.getString("cnt"));
                        editor.putString(getString(R.string.shared_ownermonthincome),jsonResult.getString("this_month_income"));
                        editor.putString(getString(R.string.shared_ownermonthrescnt),jsonResult.getString("this_month_res"));
                        editor.commit();

                        dataSetup();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        spGetPaymentList.setLoadingImg(false);
        spGetPaymentList.setUrl(getString(R.string.url_get_user_payment_list));
        spGetPaymentList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), ""));
    }

    private void dataSetup() {
        userId = sharedPreferences.getString(getString(R.string.shared_userid), "");
        Glide.with(getApplicationContext()).load(sharedPreferences.getString(getString(R.string.shared_userprofileurl), ""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(ivOwnerProfile);
        tvOwnerProfileReviewNum.setText(sharedPreferences.getString(getString(R.string.shared_owner_reviewcnt), "0"));
        tvOwnerProfileResNum.setText(sharedPreferences.getString(getString(R.string.shared_ownerrescnt), "0"));
        tvOwnerProfileName.setText(sharedPreferences.getString(getString(R.string.shared_name), ""));
        tvOwnerProfileScore.setText(sharedPreferences.getFloat(getString(R.string.shared_owner_reviewscore), 0) + "");
        tvOwnerProfileAccident.setText(sharedPreferences.getString(getString(R.string.shared_accident), ""));
        tvOwnerProfileIntro.setText(sharedPreferences.getString(getString(R.string.shared_owner_content), ""));
        float fnum = sharedPreferences.getFloat(getString(R.string.shared_owner_reviewscore), 0);
        //tvOwnerProfileReviewScore.setText("" + fnum);
        rbOwnerProfileReview.setRating(fnum);
        rbOwnerProfileReviews.setRating(fnum);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tvToolbar.setText("차주 프로필");
    }

}
