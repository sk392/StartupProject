package com.sk392.kr.carmony.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.UserReviewsAdapter;
import com.sk392.kr.carmony.Item.ReviewsItem;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserReviewsActivity extends AppCompatActivity implements SendPost.CallbackEvent{
    RecyclerView rvReviews;
    UserReviewsAdapter userReviewsAdapter;
    List<ReviewsItem> list;
    private static final String TAG = "UserReviewsActivity";

    Toolbar toolbar;
    TextView tvToolbar, tvReviewNum,tvReviewScore;
    String result,err;
    String resultType;
    RatingBar rbReview,rbReviewItem;
    JSONObject jsonResult;
    SharedPreferences sharedPreferences;
    LinearLayout llNonReviews;
    RelativeLayout rlReviews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);
        toolbar = (Toolbar)findViewById(R.id.introtoolbar);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);
        tvToolbar = (TextView)toolbar.findViewById(R.id.tv_toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bindingXml();

        setTitle(getIntent().getStringExtra("title"));
        resultType = getIntent().getStringExtra("resultType");
        //resultType = 1이면 차량 리뷰, 2면 유저 리뷰이다.
        rvReviews = (RecyclerView)findViewById(R.id.rv_reviews);
        list = new ArrayList<>();
        userReviewsAdapter = new UserReviewsAdapter(getApplicationContext(), list, R.layout.item_user_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvReviews.setAdapter(userReviewsAdapter);
        setupList();
    }
    public void setTitle(String title){
        tvToolbar.setText(title);
    }

    private void setupList(){
        SendPost sendPost = new SendPost(UserReviewsActivity.this);

        if(resultType.equals("1")){
            //차량 리뷰
            sendPost.setUrl(getString(R.string.url_get_car_review_list));
            sendPost.setCallbackEvent(this);
            sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&carinfo_id=" + getIntent().getStringExtra("carinfoId")
                    + "&offset=0"
                    + "&row_cnt=50"
            );
            Log.d("차량리뷰","sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&carinfo_id=" + getIntent().getStringExtra("carinfoId")
                    + "&offset=0"
                    + "&row_cnt=50");
        }else if(resultType.equals("3")){
            //타겟 유저 리뷰
            sendPost.setUrl(getString(R.string.url_get_target_user_review_list));
            sendPost.setCallbackEvent(this);
            sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&target_id=" + getIntent().getStringExtra("target_id")
                    + "&type=" + getIntent().getStringExtra("type")
                    + "&offset=0"
                    + "&row_cnt=50"
            );
            Log.d("차량리뷰","sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&target_id=" + getIntent().getStringExtra("target_id")
                    + "&type=" + getIntent().getStringExtra("type")
                    + "&offset=0"
                    + "&row_cnt=50");
        }else {
            // 유저 리뷰

            sendPost.setUrl(getString(R.string.url_get_user_review_list));
            sendPost.setCallbackEvent(this);
            sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&type=" + getIntent().getStringExtra("type")
                    + "&offset=0"
                    + "&row_cnt=50"
            );
            Log.d("유저리뷰","sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&type=" + getIntent().getStringExtra("type")
                    + "&offset=0"
                    + "&row_cnt=50");
        }

    }
    private List<ReviewsItem> jsonRievewResult(String err){
        List<ReviewsItem> mlist = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject jsonObject;
        Log.d("reviewActivity",jsonResult.toString());
        if(err.equals("0")){
            try {
                jsonArray = jsonResult.getJSONArray("ret");

                for(int i=0; i<jsonResult.getInt("cnt"); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    mlist.add(new ReviewsItem(jsonObject.getString("content"),jsonObject.getString("date"),getString(R.string.url_image_server)+"profile.png",jsonObject.getString("writer_name"),(float)jsonObject.getDouble("score")));

                }


            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);
            }
        }
        return mlist;
    }
    private void bindingXml(){
        llNonReviews = (LinearLayout)findViewById(R.id.ll_non_reviews);
        rlReviews = (RelativeLayout) findViewById(R.id.rl_reviews);
        tvReviewNum = (TextView)findViewById(R.id.tv_reviews_review_num);
        rbReview = (RatingBar)findViewById(R.id.rb_reviews_review);
        tvReviewScore = (TextView)findViewById(R.id.tv_reviews_review_score);

    }

    @Override
    public void getResult(String result) {
        try {
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            list =jsonRievewResult(err);

            if(list.size()>0) {

                userReviewsAdapter = new UserReviewsAdapter(getApplicationContext(), list, R.layout.item_user_reviews);
                rvReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                rvReviews.setAdapter(userReviewsAdapter);
                userReviewsAdapter.notifyDataSetChanged();
                if(resultType.equals("1")){
                    // 차량 리뷰

                    rbReview.setRating((getIntent().getFloatExtra("carinfoReviewScore",0)));
                    tvReviewScore.setText(getIntent().getFloatExtra("carinfoReviewScore",0)+"");
                    tvReviewNum.setText(getIntent().getStringExtra("carinfoReviewCnt"));

                }else{
                    //유저 리뷰
                    rbReview.setRating((getIntent().getFloatExtra("userReviewScore",0)));
                    tvReviewScore.setText(getIntent().getFloatExtra("userReviewScore",0)+"");
                    tvReviewNum.setText(getIntent().getStringExtra("userReviewCnt"));
                }


            }else{
                llNonReviews.setVisibility(View.VISIBLE);
                rlReviews.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);
        }
    }
}
