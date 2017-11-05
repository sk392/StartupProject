package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.URIImageViewPagerAdapter;
import com.sk392.kr.carmony.Dialog.OptionDialogFragment;
import com.sk392.kr.carmony.Item.OptionItem;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.sk392.kr.carmony.Fragment.SearchFragment.CLICKABLE_NO;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionDrawable;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringEn;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringKo;


public class SearchResultDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar tbResult;
    TextView tvResult, tvDetailOwnerName, tvDetailCarModel, tvDetailCarYears, tvDetailCost, tvDetailStartDate, tvDetailEndDate, tvDetailTotalCost, tvDetailRentalCost, tvDetailDeliCost, tvDetailOnedayCost, tvDetailOnedayWarning, tvDetailCarDescription, tvDetailCarReviewNum, tvDetailInfoCarYears, tvDetailInfoCarMiters, tvDetailInfoCarFuel, tvDetailInfoCarGear, tvDetailInfoCarSeat, tvDetailIsDelivery, tvDetailIsOneday, tvDetailProfileOwnerName, tvDetailProfileResNum, tvDetailProfileOwnerScoreNum, tvDetailOnlineCost, tvDetailOfflineCost, tvDetailLateCost;
    RatingBar rbDetailCarReview, rbDetailProfile;
    List<String> imageList;
    ViewPager detailViewpager;
    private static final String TAG = "SearchResultDetailActivity";

    SharedPreferences sharedPreferences;
    CircleImageView ivDetailOwnerProfile, ivDetailOwnerTopProfile;
    CirclePageIndicator circlePageIndicator;
    LinearLayout llReviews, llOneday, llCarmonyRule, llDilivery, llPanalty, llOption;
    RelativeLayout rlOnedayCost, rlResultDetailOwnerProfile;
    List<OptionItem> optionItems;
    JSONObject jsonResult;
    Button btResultDetail;
    //  TextView tvDetailCarReviewSCore, tvDetailProfileOwnerScore;
    RecyclerView.LayoutManager layoutManager;
    AlertDialog.Builder abCarmonyRule, abDilivery, abOneDay, abPanalty;
    AlertDialog adCarmonyRule, adDilivery, adOneDay, adPanalty;
    OptionDialogFragment optionDialog;
    Intent getIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        optionItems = new ArrayList<>();
        imageList = new ArrayList<>();
        getIntent = getIntent();


        layoutManager = new LinearLayoutManager(SearchResultDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        // for xml
        bindingXml();
        getJsonResult();
        dialogBuildUp();


    }

    @Override
    public void onResume() {
        super.onResume();
        tbResult.setNavigationIcon(R.drawable.arrow_big_navy);
        tbResult.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

    }

    //
    public class AutoScrollImage extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            detailViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int postItemPosition = position;
                    try {
                        Thread.sleep(1000);
                        postItemPosition = postItemPosition + 1;

                    } catch (InterruptedException e) {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "InterruptedException Fail");
                        FirebaseCrash.report(e);

                    }

                    detailViewpager.setCurrentItem(postItemPosition);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }

    }

    private void dialogBuildUp() {
        //CarmonyRule
        abCarmonyRule = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_carmony_rule, null, false);
        Button btClose = (Button) mView.findViewById(R.id.bt_alert_rule_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adCarmonyRule.dismiss();
            }
        });
        abCarmonyRule.setView(mView);
        adCarmonyRule = abCarmonyRule.create();

        //Dilivery

        abDilivery = new AlertDialog.Builder(this);
        mView = getLayoutInflater().inflate(R.layout.dialog_delivery, null, false);
        btClose = (Button) mView.findViewById(R.id.bt_alert_delivery_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adDilivery.dismiss();
            }
        });
        abDilivery.setView(mView);
        adDilivery = abDilivery.create();
        //Oneday

        abOneDay = new AlertDialog.Builder(this);
        mView = getLayoutInflater().inflate(R.layout.dialog_oneday, null, false);
        btClose = (Button) mView.findViewById(R.id.bt_alert_oneday_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adOneDay.dismiss();
            }
        });
        abOneDay.setView(mView);
        adOneDay = abOneDay.create();
        //Penalty

        abPanalty = new AlertDialog.Builder(this);
        mView = getLayoutInflater().inflate(R.layout.dialog_panalty, null, false);
        btClose = (Button) mView.findViewById(R.id.bt_alert_panaly_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adPanalty.dismiss();
            }
        });
        abPanalty.setView(mView);
        adPanalty = abPanalty.create();

    }

    private void bindingXml() {
        tbResult = (Toolbar) findViewById(R.id.introtoolbar);
        tbResult.setTitle("");
        rlResultDetailOwnerProfile = (RelativeLayout) findViewById(R.id.rl_result_detail_owner_profile);
        tvResult = (TextView) tbResult.findViewById(R.id.tv_toolbar);
        llReviews = (LinearLayout) findViewById(R.id.ll_detail_reviews);
        llPanalty = (LinearLayout) findViewById(R.id.ll_detail_panalty);
        llCarmonyRule = (LinearLayout) findViewById(R.id.ll_detail_carmony_rule);
        llDilivery = (LinearLayout) findViewById(R.id.ll_detail_delivery);
        llOneday = (LinearLayout) findViewById(R.id.ll_detail_oneday);
        llOption = (LinearLayout) findViewById(R.id.ll_detail_option);
        rlOnedayCost = (RelativeLayout) findViewById(R.id.rl_detail_oneday_cost);
        detailViewpager = (ViewPager) findViewById(R.id.vp_detail_car);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_detail);
        ivDetailOwnerProfile = (CircleImageView) findViewById(R.id.iv_detail_profile);
        ivDetailOwnerTopProfile = (CircleImageView) findViewById(R.id.iv_detail_top_profile);
        tvDetailOwnerName = (TextView) findViewById(R.id.tv_detail_owner_name);
        tvDetailCarModel = (TextView) findViewById(R.id.tv_detail_car_model);
        tvDetailCarYears = (TextView) findViewById(R.id.tv_detail_car_years);
        tvDetailCost = (TextView) findViewById(R.id.tv_detail_cost);
        tvDetailStartDate = (TextView) findViewById(R.id.tv_detail_start_date);
        tvDetailEndDate = (TextView) findViewById(R.id.tv_detail_end_date);
        tvDetailTotalCost = (TextView) findViewById(R.id.tv_detail_total_cost);
        tvDetailRentalCost = (TextView) findViewById(R.id.tv_detail_rental_cost);
        tvDetailDeliCost = (TextView) findViewById(R.id.tv_detail_delivery_cost);
        tvDetailOnedayCost = (TextView) findViewById(R.id.tv_detail_oneday_cost);
        tvDetailOnedayWarning = (TextView) findViewById(R.id.tv_detail_oneday_warning);
        tvDetailCarDescription = (TextView) findViewById(R.id.tv_detail_car_description);
        tvDetailCarReviewNum = (TextView) findViewById(R.id.tv_detail_car_review_num);
//        tvDetailCarReviewSCore = (TextView) findViewById(R.id.tv_detail_car_reivew_score);
        tvDetailInfoCarYears = (TextView) findViewById(R.id.tv_detail_info_car_years);
        tvDetailInfoCarMiters = (TextView) findViewById(R.id.tv_detail_info_car_miters);
        tvDetailLateCost = (TextView) findViewById(R.id.tv_detail_late_cost);
        tvDetailOnlineCost = (TextView) findViewById(R.id.tv_detail_online_cost);
        tvDetailOfflineCost = (TextView) findViewById(R.id.tv_detail_offline_cost);
        tvDetailInfoCarFuel = (TextView) findViewById(R.id.tv_detail_info_car_fuel);
        tvDetailInfoCarGear = (TextView) findViewById(R.id.tv_detail_info_car_gear);
        tvDetailInfoCarSeat = (TextView) findViewById(R.id.tv_detail_info_car_seat);
        tvDetailIsDelivery = (TextView) findViewById(R.id.tv_detail_is_delivery);
        tvDetailIsOneday = (TextView) findViewById(R.id.tv_detail_is_oneday);
        tvDetailProfileOwnerName = (TextView) findViewById(R.id.tv_detail_profile_owner_name);
        tvDetailProfileResNum = (TextView) findViewById(R.id.tv_detail_profile_res_num);
        //  tvDetailProfileOwnerScore = (TextView) findViewById(R.id.tv_detail_profile_owner_score);
        tvDetailProfileOwnerScoreNum = (TextView) findViewById(R.id.tv_detail_profile_score_num);
        rbDetailProfile = (RatingBar) findViewById(R.id.rb_detail_profile);
        rbDetailCarReview = (RatingBar) findViewById(R.id.rb_detail_car_review);
        btResultDetail = (Button) findViewById(R.id.bt_result_detail_res);

        btResultDetail.setOnClickListener(this);
        llReviews.setOnClickListener(this);
        llOption.setOnClickListener(this);
        llCarmonyRule.setOnClickListener(this);
        llDilivery.setOnClickListener(this);
        llOneday.setOnClickListener(this);
        rlResultDetailOwnerProfile.setOnClickListener(this);
        llPanalty.setOnClickListener(this);

    }

    private void getJsonResult() {
        SendPost sendPost = new SendPost(SearchResultDetailActivity.this);
        sendPost.setUrl(getString(R.string.url_get_car_detail_info));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;


                    jsonResult = new JSONObject(result);

                    err = jsonResult.getString("err");
                    jsonResultDetailResult(err);
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);
                }
            }
        });
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&carinfo_id=" + getIntent.getStringExtra("carinfoId"));
    }

    private void jsonResultDetailResult(String err) {
        JSONObject jsonObject;
        if (err.equals("0")) {
            try {
                jsonObject = jsonResult.getJSONObject("ret");


                //이미지가 1개 이상일 경우
                JSONArray jsonImgArray;
                jsonImgArray = jsonObject.getJSONArray("img");

                for (int i = 0; i < jsonObject.getInt("img_cnt"); i++) {
                    //실제 이미지 서버에 주소값과 같은 경우에 사용.
                    JSONObject jsonObject1 = jsonImgArray.getJSONObject(i);
                    imageList.add(jsonObject1.getString("carimage_img_t"));
                    //지금은 썸내일로, 추후에 확대 가능한 기능을 넣을 때 오리진 사진으로
                }


                URIImageViewPagerAdapter adapter = new URIImageViewPagerAdapter(getApplicationContext(), getLayoutInflater(), imageList);
                //ViewPager에 Adapter 설정
                detailViewpager.setAdapter(adapter);
                circlePageIndicator.setViewPager(detailViewpager);

                Glide.with(getApplicationContext()).load(jsonObject.getString("userinfo_img_url"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .fitCenter().into(ivDetailOwnerProfile);
                Glide.with(getApplicationContext()).load(jsonObject.getString("userinfo_img_url"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .fitCenter().into(ivDetailOwnerTopProfile);
                tvDetailOwnerName.setText(jsonObject.getString("name"));
                tvDetailCarModel.setText(jsonObject.getString("model"));
                tvDetailCarYears.setText(jsonObject.getString("model_year"));
                tvResult.setText(jsonObject.getString("model") + " " + jsonObject.getString("model_year"));
                tvDetailCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("cost"))) + "원");
                tvDetailStartDate.setText(getIntent.getStringExtra("startDate"));
                tvDetailEndDate.setText(getIntent.getStringExtra("endDate"));
                tvDetailTotalCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("cost"))) + "원");
                //렌트 비용은 무조건 0원이다.
                tvDetailRentalCost.setText("0원");
                tvDetailOnlineCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("cost")) - Integer.parseInt(getIntent.getStringExtra("onedayCost"))) + "원");
                tvDetailDeliCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("deliveryCost"))) + "원");
                tvDetailLateCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("lateCost"))) + "원");
                tvDetailOnedayCost.setText("약  " + String.format("%,d", Integer.parseInt(getIntent.getStringExtra("onedayCost"))) + "원");
                tvDetailOfflineCost.setText("약  " + String.format("%,d", Integer.parseInt(getIntent.getStringExtra("onedayCost"))) + "원");
                tvDetailCarDescription.setText(jsonObject.getString("car_content"));
                String strScore = String.format("%.1f", (float) (jsonObject.getDouble("carinfo_review_score")));
                rbDetailCarReview.setRating(Float.valueOf(strScore));
                //  tvDetailCarReviewSCore.setText(strScore);
                tvDetailCarReviewNum.setText(jsonObject.getString("carinfo_review_cnt"));
                tvDetailInfoCarYears.setText(jsonObject.getString("model_year"));
                tvDetailInfoCarMiters.setText(jsonObject.getInt("mileage") + "만~" + (jsonObject.getInt("mileage") + 1) + "만km");
                tvDetailInfoCarFuel.setText(jsonObject.getString("fuel"));
                tvDetailInfoCarGear.setText(jsonObject.getString("gear"));
                tvDetailInfoCarSeat.setText(jsonObject.getString("seater") + "인승");
                tvDetailProfileOwnerName.setText(jsonObject.getString("name"));
                tvDetailProfileResNum.setText(jsonObject.getString("res_cnt"));
                //   tvDetailProfileOwnerScore.setText(strScore);
                strScore = String.format("%.1f", (float) (jsonObject.getDouble("ow_review_score")));
                rbDetailProfile.setRating(Float.valueOf(strScore));
                tvDetailProfileOwnerScoreNum.setText(jsonObject.getString("ow_review_cnt"));

                if (jsonObject.getString("isdelivery").equals("y"))
                    tvDetailIsDelivery.setText("필수");
                    //현재는 딜리버리 비용으로 받고 있기 때문에 무조건 필수다.
                else
                    tvDetailIsDelivery.setText("필수");

                if (jsonObject.getString("isoneday").equals("y")) {
                    tvDetailIsOneday.setText("필수");
                    rlOnedayCost.setVisibility(View.VISIBLE);
                }
                else {
                    tvDetailIsOneday.setText("필수 아님");
                    rlOnedayCost.setVisibility(View.GONE);
                }


                List<OptionItem> list = checkOption(jsonObject);
                optionDialog = new OptionDialogFragment();
                optionDialog.setItems(list);


            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);
            }
            //이미지 추가
            //텍스트뷰 세팅

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_detail_reviews:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UserReviewsActivity.class);
                try {
                    intent.putExtra("resultType", "1");//차량 리뷰는 1
                    intent.putExtra("carinfoId", jsonResult.getJSONObject("ret").getString("carinfo_id"));
                    intent.putExtra("carinfoReviewCnt", jsonResult.getJSONObject("ret").getString("carinfo_review_cnt"));
                    intent.putExtra("carinfoReviewScore", (float) jsonResult.getJSONObject("ret").getDouble("carinfo_review_score"));
                    intent.putExtra("title", "차량 리뷰");
                } catch (JSONException e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);
                    e.printStackTrace();
                }
                startActivity(intent);
                break;

            case R.id.ll_detail_option:
                Bundle arguments = new Bundle();
                arguments.putInt("type", CLICKABLE_NO);
                optionDialog.setArguments(arguments);
                optionDialog.show(getFragmentManager(), "OptionDialog");
                break;

            case R.id.ll_detail_carmony_rule:
                adCarmonyRule.show();
                break;

            case R.id.ll_detail_delivery:
                adDilivery.show();
                break;

            case R.id.ll_detail_oneday:
                adOneDay.show();
                break;

            case R.id.ll_detail_panalty:
                adPanalty.show();
                break;
            case R.id.bt_result_detail_res:
                if (sharedPreferences.getString(getString(R.string.shared_islicen), "").equals("y")) {
                    getReservationJsonResult();
                } else {
                    MainActivity.showLongToast(SearchResultDetailActivity.this, "면허증 심사가 마무리 되어야 진행하실 수 있어요!");
                }

                break;
            case R.id.rl_result_detail_owner_profile:
                try {
                    Intent intent1 = new Intent(getApplicationContext(), UserProfileActivity.class);
                    intent1.putExtra("userinfo_name", jsonResult.getJSONObject("ret").getString("name"));
                    intent1.putExtra("userinfo_id", jsonResult.getJSONObject("ret").getString("userinfo_id"));
                    intent1.putExtra("user_type", "1");//차주 정보를 가져오는지(1), 드라이버 정보를 가져오는지 (0)
                    startActivity(intent1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    //옵션이 y로 표시되어 있는지 체크한다.
    private List<OptionItem> checkOption(JSONObject jsonObject) {
        JSONObject jsonObject1 = jsonObject;
        List<OptionItem> list = new ArrayList<>();

        try {
            for (int i = 0; i < optionStringEn.length; i++) {
                if (jsonObject1.getString("navigation").equals("y"))
                    if (optionStringEn[i].equals("navigation"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (jsonObject1.getString("rearsensor").equals("y"))
                    if (optionStringEn[i].equals("rearsensor"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (jsonObject1.getString("rearcarmera").equals("y"))
                    if (optionStringEn[i].equals("rearcarmera"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (jsonObject1.getString("smartkey").equals("y"))
                    if (optionStringEn[i].equals("smartkey"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (jsonObject1.getString("blackbox").equals("y"))
                    if (optionStringEn[i].equals("blackbox"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (jsonObject1.getString("smoking").equals("y"))
                    if (optionStringEn[i].equals("smoking"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (jsonObject1.getString("bluetooth").equals("y"))
                    if (optionStringEn[i].equals("bluetooth"))
                        list.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);
        }
        return list;
    }

    private void getReservationJsonResult() {
        SendPost sendPost = new SendPost(SearchResultDetailActivity.this);
        sendPost.setUrl(getString(R.string.url_set_reservation));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;

                    JSONObject jsonObject;

                    Log.d("ppap", result);
                    jsonObject = new JSONObject(result);

                    err = jsonObject.getString("err");
                    //예약이 정상적으로 요청된 경우.
                    if (err.equals("0")) {

                    /*스택에 기존에 사용하던 Activity가 있다면 그 위의 스택을 전부 제거해 주고 호출한다.
                    0->1->2->3 일때 Activity 3에서 Activity 1을 호출할때 이 플래그를 설정하면
                    0->1 만 남게 된다. (2, 3은 제거)  이때 Activity 1은 onDestory() -> onCreate()가 호출된다. 삭제하고 다시 만들게 된다.
                    * */
                        //| Intent.FLAG_ACTIVITY_SINGLE_TOP
                        Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                        mIntent.putExtra("is_massage", true);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mIntent.putExtra("result", "SearchResultDetailActivity");
                        mIntent.putExtra("owner_id", jsonResult.getJSONObject("ret").getString("userinfo_id"));//차주  아이디
                        //데이트 년도 짜르기
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat format2 = new SimpleDateFormat("MM. dd. HH:mm");
                        String startDate = "", endDate = "";
                        try {
                            startDate = format2.format(format.parse(getIntent.getStringExtra("startDate")));
                            endDate = format2.format(format.parse(getIntent.getStringExtra("endDate")));

                        } catch (ParseException e) {
                            FirebaseCrash.logcat(Log.ERROR, TAG, "ParseException Fail");
                            FirebaseCrash.report(e);
                            e.printStackTrace();
                        }
                        mIntent.putExtra("reservation_date", startDate + " ~ " + endDate);
                        mIntent.putExtra("reservation_id", jsonObject.getString("reservation_id"));
                        mIntent.putExtra("car_name", jsonResult.getJSONObject("ret").getString("model"));
                        mIntent.putExtra("location", getIntent.getStringExtra("location"));

                        startActivity(mIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);
                }
            }
        });

        try {
            sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&owner_id=" + jsonResult.getJSONObject("ret").getString("userinfo_id")
                    + "&carinfo_id=" + jsonResult.getJSONObject("ret").getString("carinfo_id")
                    + "&start_date=" + getIntent.getStringExtra("startDate")
                    + "&end_date=" + getIntent.getStringExtra("endDate")
                    + "&cost=" + Integer.parseInt(getIntent.getStringExtra("cost"))
                    + "&late_cost=" + Integer.parseInt(getIntent.getStringExtra("lateCost"))
                    + "&delivery_cost=" + Integer.parseInt(getIntent.getStringExtra("deliveryCost"))
                    + "&oneday_cost=" + Integer.parseInt(getIntent.getStringExtra("onedayCost"))
                    + "&location=" + getIntent.getStringExtra("location"));

            Log.d("aBD", "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&owner_id=" + jsonResult.getJSONObject("ret").getString("userinfo_id")
                    + "&carinfo_id=" + jsonResult.getJSONObject("ret").getString("carinfo_id")
                    + "&start_date=" + getIntent.getStringExtra("startDate")
                    + "&end_date=" + getIntent.getStringExtra("endDate")
                    + "&cost=" + Integer.parseInt(getIntent.getStringExtra("cost"))
                    + "&late_cost=" + Integer.parseInt(getIntent.getStringExtra("lateCost"))
                    + "&delivery_cost=" + Integer.parseInt(getIntent.getStringExtra("deliveryCost"))
                    + "&oneday_cost=" + Integer.parseInt(getIntent.getStringExtra("onedayCost"))
                    + "&location=" + getIntent.getStringExtra("location"));
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);
        }

    }
}
