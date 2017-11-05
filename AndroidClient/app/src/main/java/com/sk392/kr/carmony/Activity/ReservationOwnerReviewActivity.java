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

public class ReservationOwnerReviewActivity extends AppCompatActivity implements SendPost.CallbackEvent {
    Toolbar toolbar;
    TextView tvToolbar,tvOwnerReviewCnt;
   //TextView tvOwnerReview;
    RatingBar  rbOwnerReview;
    SharedPreferences sharedPreferences;
    private InputMethodManager imm;
    private static final String TAG = "ReservationOwnerReviewActivity";


    EditText etOwnerReview;
    RelativeLayout rlOwnerReview;
    Button btReview;
    Boolean isOver=false,isOwnerReview=false;
    Intent getIntent;
    JSONObject jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_owner_review);
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
        rbOwnerReview = (RatingBar) findViewById(R.id.rb_reservation_owner_review);
        //tvOwnerReview = (TextView) findViewById(R.id.tv_reservation_owner_review);
        tvOwnerReviewCnt = (TextView) findViewById(R.id.tv_reservation_owner_review_cnt);
        btReview = (Button) findViewById(R.id.bt_reservation_owner_review);
        etOwnerReview = (EditText)findViewById(R.id.et_reservation_owner_review);
        btReview.setClickable(false);
        rlOwnerReview = (RelativeLayout)findViewById(R.id.rl_reservation_owner_review);
        rlOwnerReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etOwnerReview.getWindowToken(), 0);
            }
        });
        etOwnerReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                tvOwnerReviewCnt.setText("("+charSequence.length()+"/140)");
                if(charSequence.length()>=10 && charSequence.length()<=140) {
                    isOwnerReview = true;
                }
                else {
                    isOwnerReview = false;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

       /* rbOwnerReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tvOwnerReview.setText(v + "점");

            }
        });*/
        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOwnerReview){
                    getCarResult();
                }else{
                    MainActivity.showToast(ReservationOwnerReviewActivity.this,"리뷰를 10자 이상 입력해주세요!");
                }
            }
        });
    }

    private void getCarResult() {
        SendPost sendPost = new SendPost(ReservationOwnerReviewActivity.this);
        sendPost.setUrl(getString(R.string.url_set_user_review));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&target_id=" + getIntent.getStringExtra("userinfoId")
                + "&score=" + rbOwnerReview.getRating()
                + "&content=" + etOwnerReview.getText().toString()
                + "&writer_name=" + sharedPreferences.getString(getString(R.string.shared_name),"")
                + "&type=0");
        //타입은 드라이버 ->차주,차주->드라이버 인지 구분한다. 0이면 차주 -> 대여자, 1이면 대여자 -> 차주 (리뷰 대상의 타입을 기준sf으로한다.)
    }

    @Override
    public void getResult(String result) {
        try {
            String err;
            jsonResult = new JSONObject(result);
            err = jsonResult.getString("err");
            if(err.equals("0")){
                //getUserResult까지 모두 거친경우
                MainActivity.showToast(getApplicationContext(),"리뷰가 작성되었습니다!");
                setResult(RESULT_OK);
                finish();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }

}
