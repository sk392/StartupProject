package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar tbUserinfo;
    JSONObject jsonResult;
    String userType;
    ImageButton ibToolbar;
    RelativeLayout rlUserinfoReviews, rlUserinfoIntro;
    ImageView ivProfile;
    float userReviewScore =0;
    int userReviewCnt=0;
    TextView tvToolbar,tvUserProfileMileageTitle, tvUserinfoName, tvUserInfoResNum, tvUserinfoMileage, tvUserinfoReviewNum, tvUserinfoAccident, tvUserinfoIntro;
    RatingBar rbUserinfoReview, rbUserinfoReviews;
    private static final String TAG = "UserinfoFragment";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        bindingXml();
        dataSetup();
    }

    private void bindingXml() {

        rlUserinfoReviews = (RelativeLayout) findViewById(R.id.rl_user_profile_reviews);
        tbUserinfo = (Toolbar) findViewById(R.id.introtoolbar);
        tbUserinfo.setNavigationIcon(R.drawable.arrow_big_navy);
        tbUserinfo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tvToolbar = (TextView) tbUserinfo.findViewById(R.id.tv_toolbar);
        ibToolbar = (ImageButton) tbUserinfo.findViewById(R.id.ib_toolbar_user);
        ivProfile = (ImageView) findViewById(R.id.iv_user_profile_profile);
        tvUserinfoIntro = (TextView) findViewById(R.id.tv_user_profile_intro);
        tvUserinfoAccident = (TextView) findViewById(R.id.tv_user_profile_accident);
        tvUserinfoMileage = (TextView) findViewById(R.id.tv_user_profile_mileage);
        tvUserinfoName = (TextView) findViewById(R.id.tv_user_profile_name);
        tvUserInfoResNum = (TextView) findViewById(R.id.tv_user_profile_res_num);
        tvUserinfoReviewNum = (TextView) findViewById(R.id.tv_user_profile_review_num);
        tvUserProfileMileageTitle = (TextView) findViewById(R.id.tv_user_profile_mileage_title);
        rbUserinfoReview = (RatingBar) findViewById(R.id.rb_user_profile_review);
        rbUserinfoReviews = (RatingBar) findViewById(R.id.rb_user_profile_reviews);
        rlUserinfoIntro = (RelativeLayout) findViewById(R.id.rl_user_profile_intro);
        //tvUserinfoReviewScore = (TextView) userinfoView.findViewById(R.id.tv_user_profile_review_score);
        rlUserinfoIntro.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        rlUserinfoReviews.setOnClickListener(this);
        ibToolbar.setOnClickListener(this);
    }

    private void dataSetup() {
        userType = getIntent().getStringExtra("user_type");
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        SendPost sendPost = new SendPost(UserProfileActivity.this);
        sendPost.setUrl(getString(R.string.url_get_target_user_info));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;
                    jsonResult = new JSONObject(result);

                    err = jsonResult.getString("err");
                    JSONObject jsonObject = jsonResult.getJSONObject("ret");
                    if (err.equals("0")) {
                        Glide.with(UserProfileActivity.this).load(jsonObject.getString("userinfo_img_url"))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .fitCenter().into(ivProfile);
                        if (userType.equals("0")) {
                            // 유저드라이버 화면
                            userReviewCnt = jsonObject.getInt("userinfo_review_cnt");
                            tvUserInfoResNum.setText(jsonObject.getString("res_cnt"));
                            tvUserinfoMileage.setText(jsonObject.getString("mileage") +"km");
                            tvUserProfileMileageTitle.setText(getString(R.string.car_miters));
                            tvUserinfoIntro.setText(jsonObject.getString("content"));
                            userReviewScore = (float)jsonObject.getDouble("userinfo_review_score");
                        } else if (userType.equals("1")) {
                            // 유저 차주 화면
                            tvUserProfileMileageTitle.setText(getString(R.string.score));
                            userReviewCnt = jsonObject.getInt("ow_review_cnt");
                            tvUserInfoResNum.setText(jsonObject.getString("res_cnt"));
                            tvUserinfoMileage.setText((float)jsonObject.getDouble("ow_review_score") + "");
                            tvUserinfoIntro.setText(jsonObject.getString("owner_content"));
                            userReviewScore = (float)jsonObject.getDouble("ow_review_score");
                        } else {
                            MainActivity.showToast(getApplicationContext(), getString(R.string.error_network));
                        }
                        tvUserinfoReviewNum.setText(userReviewCnt +"");
                        tvUserinfoAccident.setText(jsonObject.getString("accident"));
                        tvUserinfoName.setText(getIntent().getStringExtra("userinfo_name"));
                        rbUserinfoReview.setRating(userReviewScore);
                        rbUserinfoReviews.setRating(userReviewScore);
                    } else {
                        MainActivity.showToast(getApplicationContext(), getString(R.string.error_network));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);
                }

            }
        });
        tvToolbar.setText(getIntent().getStringExtra("userinfo_name"));
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&target_id=" + getIntent().getStringExtra("userinfo_id")
                + "&target_type=" + userType);//userType =0이면 드라이버, 1이면 차주
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_user_profile_reviews:
                intent.setClass(getApplicationContext(), UserReviewsActivity.class);
                intent.putExtra("title", getIntent().getStringExtra("userinfo_name") + " 리뷰");
                intent.putExtra("resultType", "3");
                intent.putExtra("target_id", getIntent().getStringExtra("userinfo_id"));
                intent.putExtra("type", userType);
                intent.putExtra("userReviewCnt", userReviewCnt);
                intent.putExtra("userReviewScore", userReviewScore);
                startActivity(intent);
                break;

            case R.id.rl_user_profile_intro:
                break;
        }
    }

}
