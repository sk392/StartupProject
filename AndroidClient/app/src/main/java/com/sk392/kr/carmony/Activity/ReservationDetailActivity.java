package com.sk392.kr.carmony.Activity;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.ReservationAdapter;
import com.sk392.kr.carmony.Adapter.URIImageViewPagerAdapter;
import com.sk392.kr.carmony.Dialog.OptionDialogFragment;
import com.sk392.kr.carmony.Item.OptionItem;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.sk392.kr.carmony.Fragment.SearchFragment.CLICKABLE_NO;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionDrawable;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringEn;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringKo;

public class ReservationDetailActivity extends AppCompatActivity implements View.OnClickListener, SendPost.CallbackEvent {
    Toolbar tbResult;
    private static final String TAG = "ReservationDetailActivity";
    private static final int REVIEW_CREATE_CODE = 10111;

    TextView tvResult, tvResDetailOwnerName, tvResDetailCarModel, tvResDetailCarYears, tvResDetailCost, tvResDetailStartDate, tvResDetailEndDate, tvResDetailTotalCost, tvResDetailRentalCost, tvResDetailDeliCost, tvResDetailOnedayCost, tvResDetailOnedayWarning, tvResDetailCarDescription, tvResDetailCarReviewNum, tvResDetailInfoCarYears, tvResDetailInfoCarMiters, tvResDetailInfoCarFuel, tvResDetailInfoCarGear, tvResDetailInfoCarSeat, tvResDetailIsDelivery, tvResDetailIsOneday, tvResDetailProfileOwnerName, tvResDetailProfileResNum, tvResDetailProfileOwnerScoreNum, tvResDetailOnlineCost, tvResDetailOfflineCost, tvResDetailLateCost;
    RatingBar rbResDetailCarReview, rbResDetailProfile;
    //TextView tvResDetailCarReviewSCore,tvResDetailProfileOwnerScore;
    List<String> imageList;
    Button btResDetail;
    ViewPager detailViewpager;
    SharedPreferences sharedPreferences;
    CirclePageIndicator circlePageIndicator;
    LinearLayout llReviews, llOneday, llCarmonyRule, llDilivery, llPanalty, llOption;
    RelativeLayout rlOnedayCost;
    RelativeLayout rlResDetailOwnerProfile;
    List<OptionItem> optionItems;
    JSONObject jsonResult;
    RecyclerView.LayoutManager layoutManager;
    AlertDialog.Builder abCarmonyRule, abDilivery, abOneDay, abPanalty;
    AlertDialog adCarmonyRule, adDilivery, adOneDay, adPanalty;
    OptionDialogFragment optionDialog;
    Intent getIntent;
    ImageView ivResDetailProfile, ivResDetailTopProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        optionItems = new ArrayList<>();
        imageList = new ArrayList<>();
        getIntent = getIntent();


        layoutManager = new LinearLayoutManager(ReservationDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
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
        tvResult = (TextView) tbResult.findViewById(R.id.tv_toolbar);
        ivResDetailProfile = (ImageView) findViewById(R.id.iv_res_detail_profile);
        ivResDetailTopProfile = (ImageView) findViewById(R.id.iv_res_detail_top_profile);
        llReviews = (LinearLayout) findViewById(R.id.ll_res_detail_reviews);
        llPanalty = (LinearLayout) findViewById(R.id.ll_res_detail_panalty);
        llCarmonyRule = (LinearLayout) findViewById(R.id.ll_res_detail_carmony_rule);
        llDilivery = (LinearLayout) findViewById(R.id.ll_res_detail_delivery);
        llOneday = (LinearLayout) findViewById(R.id.ll_res_detail_oneday);
        llOption = (LinearLayout) findViewById(R.id.ll_res_detail_option);
        rlOnedayCost = (RelativeLayout) findViewById(R.id.rl_res_detail_oneday_cost);
        rlResDetailOwnerProfile = (RelativeLayout) findViewById(R.id.rl_res_detail_owner_profile);
        detailViewpager = (ViewPager) findViewById(R.id.vp_res_detail_car);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator_res_detail);
        tvResDetailOwnerName = (TextView) findViewById(R.id.tv_res_detail_owner_name);
        tvResDetailCarModel = (TextView) findViewById(R.id.tv_res_detail_car_model);
        tvResDetailCarYears = (TextView) findViewById(R.id.tv_res_detail_car_years);
        tvResDetailCost = (TextView) findViewById(R.id.tv_res_detail_cost);
        tvResDetailStartDate = (TextView) findViewById(R.id.tv_res_detail_start_date);
        tvResDetailEndDate = (TextView) findViewById(R.id.tv_res_detail_end_date);
        tvResDetailTotalCost = (TextView) findViewById(R.id.tv_res_detail_total_cost);
        tvResDetailRentalCost = (TextView) findViewById(R.id.tv_res_detail_rental_cost);
        tvResDetailDeliCost = (TextView) findViewById(R.id.tv_res_detail_delivery_cost);
        tvResDetailOnedayCost = (TextView) findViewById(R.id.tv_res_detail_oneday_cost);
        tvResDetailOnedayWarning = (TextView) findViewById(R.id.tv_res_detail_oneday_warning);
        tvResDetailCarDescription = (TextView) findViewById(R.id.tv_res_detail_car_description);
        tvResDetailCarReviewNum = (TextView) findViewById(R.id.tv_res_detail_car_review_num);
        //tvResDetailCarReviewSCore = (TextView) findViewById(R.id.tv_res_detail_car_review_score);
        tvResDetailInfoCarYears = (TextView) findViewById(R.id.tv_res_detail_info_car_years);
        tvResDetailInfoCarMiters = (TextView) findViewById(R.id.tv_res_detail_info_car_miters);
        tvResDetailLateCost = (TextView) findViewById(R.id.tv_res_detail_late_cost);
        tvResDetailOnlineCost = (TextView) findViewById(R.id.tv_res_detail_online_cost);
        tvResDetailOfflineCost = (TextView) findViewById(R.id.tv_res_detail_offline_cost);
        tvResDetailInfoCarFuel = (TextView) findViewById(R.id.tv_res_detail_info_car_fuel);
        tvResDetailInfoCarGear = (TextView) findViewById(R.id.tv_res_detail_info_car_gear);
        tvResDetailInfoCarSeat = (TextView) findViewById(R.id.tv_res_detail_info_car_seat);
        tvResDetailIsDelivery = (TextView) findViewById(R.id.tv_res_detail_is_delivery);
        tvResDetailIsOneday = (TextView) findViewById(R.id.tv_res_detail_is_oneday);
        tvResDetailProfileOwnerName = (TextView) findViewById(R.id.tv_res_detail_profile_owner_name);
        tvResDetailProfileResNum = (TextView) findViewById(R.id.tv_res_detail_profile_res_num);
        //tvResDetailProfileOwnerScore = (TextView) findViewById(R.id.tv_res_detail_profile_owner_score);
        tvResDetailProfileOwnerScoreNum = (TextView) findViewById(R.id.tv_res_detail_profile_score_num);
        rbResDetailProfile = (RatingBar) findViewById(R.id.rb_res_detail_profile);
        rbResDetailCarReview = (RatingBar) findViewById(R.id.rb_res_detail_car_review);
        btResDetail = (Button) findViewById(R.id.bt_res_detail);

        btResDetail.setOnClickListener(this);
        llReviews.setOnClickListener(this);
        llOption.setOnClickListener(this);
        llCarmonyRule.setOnClickListener(this);
        llDilivery.setOnClickListener(this);
        llOneday.setOnClickListener(this);
        llPanalty.setOnClickListener(this);
        rlResDetailOwnerProfile.setOnClickListener(this);

    }

    private void getJsonResult() {
        SendPost sendPost = new SendPost(ReservationDetailActivity.this);
        sendPost.setUrl(getString(R.string.url_get_car_detail_info));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&carinfo_id=" + getIntent.getStringExtra("carinfoId"));
    }

    private void jsonResultDetailResult(String err) {
        final JSONObject jsonObject;
        if (err.equals("0")) {
            try {
                jsonObject = jsonResult.getJSONObject("ret");

                JSONArray jsonImgArray;
                jsonImgArray = jsonObject.getJSONArray("img");

                for (int i = 0; i < jsonObject.getInt("img_cnt"); i++) {
                    //실제 이미지 서버에 주소값과 같은 경우에 사용.
                    JSONObject jsonObject1 = jsonImgArray.getJSONObject(i);
                    imageList.add(jsonObject1.getString("carimage_img_t"));
                }


                URIImageViewPagerAdapter adapter = new URIImageViewPagerAdapter(getApplicationContext(), getLayoutInflater(), imageList);
                //ViewPager에 Adapter 설정
                detailViewpager.setAdapter(adapter);
                circlePageIndicator.setViewPager(detailViewpager);
                tvResDetailOwnerName.setText(jsonObject.getString("name"));
                tvResDetailCarModel.setText(jsonObject.getString("model"));
                tvResDetailCarYears.setText(jsonObject.getString("model_year"));
                tvResult.setText(jsonObject.getString("model") + " " + jsonObject.getString("model_year"));
                Log.d("ResDetailActvity_Cost", getIntent.getStringExtra("cost"));
                tvResDetailCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("cost"))) + "원");
                tvResDetailStartDate.setText(getIntent.getStringExtra("startDate"));
                tvResDetailEndDate.setText(getIntent.getStringExtra("endDate"));
                tvResDetailTotalCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("cost"))) + "원");
                //렌트 비용은 무조건 0원이다.
                tvResDetailRentalCost.setText("0원");
                tvResDetailOnlineCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("cost")) - Integer.parseInt(getIntent.getStringExtra("onedayCost"))) + "원");
                tvResDetailDeliCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("deliveryCost"))) + "원");
                tvResDetailLateCost.setText(String.format("%,d", Integer.parseInt(getIntent.getStringExtra("lateCost"))) + "원");
                tvResDetailOnedayCost.setText("약  " + String.format("%,d", Integer.parseInt(getIntent.getStringExtra("onedayCost"))) + "원");
                tvResDetailOfflineCost.setText("약  " + String.format("%,d", Integer.parseInt(getIntent.getStringExtra("onedayCost"))) + "원");
                Glide.with(ReservationDetailActivity.this).load(jsonObject.getString("userinfo_img_url"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .fitCenter().into(ivResDetailProfile);
                Glide.with(ReservationDetailActivity.this).load(jsonObject.getString("userinfo_img_url"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .fitCenter().into(ivResDetailTopProfile);
                if (getIntent.getIntExtra("reservationType", 0) == ReservationAdapter.RES_AFTER) {
                    //이후 예약인 경우

                    if (getIntent.getStringExtra("reservationState").equals("1")) {
                        //결제 이후.
                        btResDetail.setEnabled(false);
                        btResDetail.setText("결제가 완료되었습니다.");

                    } else {
                        //결제 이전이라면.
                        //예약을 취소하시겠습니까? 메시지
                        btResDetail.setText(R.string.res_request_cancel);
                        btResDetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SendPost sendPost = new SendPost(ReservationDetailActivity.this);
                                sendPost.setUrl(getString(R.string.url_set_reservation_state));
                                sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                                    @Override
                                    public void getResult(String result) {
                                        String err;
                                        try {
                                            JSONObject jsonObject1;
                                            jsonObject1 = new JSONObject(result);
                                            err = jsonObject1.getString("err");
                                            if (err.equals("0")) {
                                                MainActivity.showLongToast(getApplicationContext(), "예약이 취소되었습니다.");
                                                Bundle extra = new Bundle();
                                                Intent intent = new Intent();
                                                extra.putBoolean("is_cancel", true);
                                                intent.putExtras(extra);
                                                setResult(RESULT_OK, intent);
                                                finish();

                                            } else if (err.equals("100")) {
                                                MainActivity.showLongToast(getApplicationContext(), jsonObject1.getString("err_result"));
                                            } else {
                                                MainActivity.showLongToast(getApplicationContext(), "서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                            FirebaseCrash.report(e);

                                        }

                                    }
                                });
                                sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                                        + "&reservation_id=" + getIntent.getStringExtra("reservationId")
                                        + "&state=" + "3");

                            }
                        });
                    }
                } else if (getIntent.getIntExtra("reservationType", 0) == ReservationAdapter.RES_BEFORE) {
                    //예약이 지난 경우
                    if (getIntent.getIntExtra("userType", 5) == 0) {
                        //드라이버 입장인 경우
                        btResDetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), ReservationUserReviewActivity.class);
                                intent.putExtra("carinfoId", getIntent.getStringExtra("carinfoId"));
                                intent.putExtra("userinfoId", getIntent.getStringExtra("ownerId"));
                                startActivityForResult(intent, REVIEW_CREATE_CODE);


                            }
                        });

                    } else if (getIntent.getIntExtra("userType", 5) == 1) {
                        //차주인 경우
                        btResDetail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), ReservationOwnerReviewActivity.class);
                                intent.putExtra("carinfoId", getIntent.getStringExtra("carinfoId"));
                                intent.putExtra("userinfoId", getIntent.getStringExtra("userinfoId"));
                                startActivityForResult(intent, REVIEW_CREATE_CODE);
                            }
                        });
                    }

                } else if (getIntent.getIntExtra("reservationType", 0) == ReservationAdapter.RES_NOW) {
                    //리뷰작성 페이지로
                    //결제한 후에 현재로 넘어오니까 버튼을 삭제
                    btResDetail.setVisibility(View.GONE);
                    btResDetail.setText(R.string.res_request_cancel);


                }
                if (!getIntent.getStringExtra("onedayCost").equals("0")) {
                    rlOnedayCost.setVisibility(View.VISIBLE);
                } else {
                    rlOnedayCost.setVisibility(View.GONE);
                }
                tvResDetailCarDescription.setText(jsonObject.getString("car_content"));
                String strScore = String.format("%.1f", (float) (jsonObject.getDouble("carinfo_review_score")));
                rbResDetailCarReview.setRating(Float.valueOf(strScore));
                //tvResDetailCarReviewSCore.setText(strScore);
                tvResDetailCarReviewNum.setText(jsonObject.getString("carinfo_review_cnt"));
                tvResDetailInfoCarYears.setText(jsonObject.getString("model_year"));
                tvResDetailInfoCarMiters.setText(jsonObject.getInt("mileage") + "만~" + (jsonObject.getInt("mileage") + 1) + "만km");
                tvResDetailInfoCarFuel.setText(jsonObject.getString("fuel"));
                tvResDetailInfoCarGear.setText(jsonObject.getString("gear"));
                tvResDetailInfoCarSeat.setText(jsonObject.getString("seater") + "인승");
                tvResDetailProfileOwnerName.setText(jsonObject.getString("name"));
                tvResDetailProfileResNum.setText(jsonObject.getString("res_cnt"));
                //tvResDetailProfileOwnerScore.setText(strScore);
                strScore = String.format("%.1f", (float) (jsonObject.getDouble("ow_review_score")));
                rbResDetailProfile.setRating(Float.valueOf(strScore));
                tvResDetailProfileOwnerScoreNum.setText(jsonObject.getString("ow_review_cnt"));

                if (jsonObject.getString("isdelivery").equals("y"))
                    tvResDetailIsDelivery.setText("필수");
                    //현재는 딜리버리 비용으로 받고 있기 때문에 무조건 필수다.
                else
                    tvResDetailIsDelivery.setText("필수");

                if (jsonObject.getString("isoneday").equals("y"))
                    tvResDetailIsOneday.setText("필수");
                else
                    tvResDetailIsOneday.setText("필수 아님");


                List<OptionItem> list = checkOption(jsonObject);
                optionDialog = new OptionDialogFragment();
                optionDialog.setItems(list);


            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);

            }
            if (getIntent.getIntExtra("reservationType", 0) == ReservationAdapter.RES_BEFORE) {
                //지난 예약인 경우에만 리뷰를 쓸 수 있으므로
                //해당 예약 건수에 리뷰를 작성했는지 확인
                checkReviewStatus();
            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_res_detail_reviews:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UserReviewsActivity.class);
                try {
                    intent.putExtra("carinfoId", jsonResult.getJSONObject("ret").getString("carinfo_id"));
                    intent.putExtra("carinfoReviewCnt", jsonResult.getJSONObject("ret").getString("carinfo_review_cnt"));
                    intent.putExtra("resultType", "1");//1은 차량 리뷰
                    intent.putExtra("carinfoReviewScore", (float) jsonResult.getJSONObject("ret").getDouble("carinfo_review_score"));
                    intent.putExtra("title", "차량 리뷰");
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);

                }
                startActivity(intent);
                break;

            case R.id.ll_res_detail_option:
                Bundle arguments = new Bundle();
                arguments.putInt("type", CLICKABLE_NO);
                optionDialog.setArguments(arguments);
                optionDialog.show(getFragmentManager(), "OptionDialog");
                break;

            case R.id.ll_res_detail_carmony_rule:
                adCarmonyRule.show();
                break;

            case R.id.ll_res_detail_delivery:
                adDilivery.show();
                break;

            case R.id.ll_res_detail_oneday:
                adOneDay.show();
                break;

            case R.id.ll_res_detail_panalty:
                adPanalty.show();
                break;
            case R.id.rl_res_detail_owner_profile:
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
            Log.d("optiondaa", jsonObject1.getString("sunroof"));


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

    @Override
    public void getResult(String result) {
        try {
            String err;


            Log.d("ReservationDetailActivity", result);
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            jsonResultDetailResult(err);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }

    private void checkReviewStatus() {
        SendPost mSendPost = new SendPost(ReservationDetailActivity.this);
        mSendPost.setUrl(getString(R.string.url_get_user_review_state));
        mSendPost.setLoadingImg(false);
        mSendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    JSONObject joUserReviewState = new JSONObject(result);

                    if (joUserReviewState.getString("err").equals("0")) {
                        //예약이 이전이며, 리뷰가 작성된 경우
                        if (joUserReviewState.getString("state").equals("1")) {
                            btResDetail.setEnabled(false);
                            btResDetail.setText(getString(R.string.res_request_review_disabled));

                        } else {
                            //예약이 이전이며 리뷰가 작성되지 않은 경우
                            btResDetail.setText(R.string.res_request_review);
                            btResDetail.setEnabled(true);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        if (getIntent.getIntExtra("userType", 5) == 0) {
            //드라이버의 경우
            mSendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&target_id=" + getIntent.getStringExtra("ownerId"));
            Log.d(TAG, "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&target_id=" + getIntent.getStringExtra("ownerId"));

        } else if (getIntent.getIntExtra("userType", 5) == 1) {
            //차주의 경우
            mSendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&target_id=" + getIntent.getStringExtra("userinfoId"));
            Log.d(TAG, "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                    + "&target_id=" + getIntent.getStringExtra("userinfoId"));

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REVIEW_CREATE_CODE) {
            checkReviewStatus();
            btResDetail.setEnabled(false);
            btResDetail.setText(getString(R.string.res_request_review_disabled));
        }
    }
}