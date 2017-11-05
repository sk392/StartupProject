package com.sk392.kr.carmony.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ReservationUserReviewActivity extends AppCompatActivity implements SendPost.CallbackEvent {
    Toolbar toolbar;
    TextView tvToolbar, tvUserReviewCnt, tvCarReviewCnt;
    //    TextView  tvCarReview, tvUserReview;
    RatingBar rbCarReview, rbUserReview;
    SharedPreferences sharedPreferences;
    EditText etUserReview, etCarReview;
    private InputMethodManager imm;
    private static final String TAG = "ReservationUserReviewActivity";

    Button btReview;
    RelativeLayout rlUserReview;
    Boolean isOver = false, isCarReview = false, isUserReview = false;
    Intent getIntent;
    JSONObject jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_user_review);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        getIntent = getIntent();
        bindingXml();

    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tvToolbar.setText("리뷰 작성");
    }

    private void bindingXml() {
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        rbCarReview = (RatingBar) findViewById(R.id.rb_reservation_car_review);
        rbUserReview = (RatingBar) findViewById(R.id.rb_reservation_user_review);
        //tvCarReview = (TextView) findViewById(R.id.tv_reservation_car_review);
        //tvUserReview = (TextView) findViewById(R.id.tv_reservation_user_review);
        tvUserReviewCnt = (TextView) findViewById(R.id.tv_reservation_user_review_cnt);
        tvCarReviewCnt = (TextView) findViewById(R.id.tv_reservation_car_review_cnt);
        btReview = (Button) findViewById(R.id.bt_reservation_user_review);
        btReview.setClickable(false);
        etUserReview = (EditText) findViewById(R.id.et_reservation_user_review);
        etCarReview = (EditText) findViewById(R.id.et_reservation_car_review);
        rlUserReview = (RelativeLayout) findViewById(R.id.rl_reservation_user_review);
        rlUserReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etCarReview.getWindowToken(), 0);
            }
        });
        etUserReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvUserReviewCnt.setText("(" + charSequence.length() + "/140)");
                isUserReview = charSequence.length() >=10 && charSequence.length()<=140;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etCarReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvCarReviewCnt.setText("(" + charSequence.length() + "/140)");
                isCarReview = charSequence.length() >=10&& charSequence.length()<=140;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


     /*   rbCarReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tvCarReview.setText(v + "점");
            }
        });
        rbUserReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tvUserReview.setText(v + "점");

            }
        });*/
        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCarReview && isUserReview) {
                    getCarResult();
                } else {
                    MainActivity.showToast(ReservationUserReviewActivity.this, "리뷰를 10자 이상 입력해주세요!");

                }
            }
        });
    }

    private void getCarResult() {
        SendPost sendPost = new SendPost(ReservationUserReviewActivity.this);
        sendPost.setUrl(getString(R.string.url_set_car_review));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&carinfo_id=" + getIntent.getStringExtra("carinfoId")
                + "&score=" + rbCarReview.getRating()
                + "&content=" + etCarReview.getText().toString()
                + "&writer_name=" + sharedPreferences.getString(getString(R.string.shared_name), ""));
    }

    @Override
    public void getResult(String result) {
        try {
            String err;


            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            if (err.equals("0")) {
                if (isOver) {
                    //getUserResult까지 모두 거친경우
                    MainActivity.showToast(getApplicationContext(), "리뷰가 작성되었습니다!");
                    finish();
                    setResult(RESULT_OK);
                } else {
                    //isOver가 false 인경우 , getUserResult를 거치지 않은 경우
                    getUserResult();

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }

    private void getUserResult() {
        SendPost sendPost = new SendPost(ReservationUserReviewActivity.this);
        sendPost.setUrl(getString(R.string.url_set_user_review));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&target_id=" + getIntent.getStringExtra("userinfoId")
                + "&score=" + rbUserReview.getRating()
                + "&content=" + etUserReview.getText().toString()
                + "&writer_name=" + sharedPreferences.getString(getString(R.string.shared_name), "")
                + "&type=1");
        //타입은 드라이버 ->차주,차주->드라이버 인지 구분한다. 0이면 차주 -> 대여자, 1이면 대여자 -> 차주 (리뷰 대상의 타입을 기준sf으로한다.)
        isOver = true;
    }
}
