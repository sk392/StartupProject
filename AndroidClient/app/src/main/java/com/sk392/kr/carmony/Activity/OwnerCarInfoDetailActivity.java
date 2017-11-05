package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import static com.sk392.kr.carmony.Fragment.SearchFragment.CLICKABLE_NO;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionDrawable;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringEn;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringKo;

public class OwnerCarInfoDetailActivity extends AppCompatActivity implements View.OnClickListener, SendPost.CallbackEvent {
    Toolbar toolbar;
    TextView tvToolbar;
    private static final String TAG = "OwnerCarInfoDetailActivity";
    ImageButton ibToolbar;
    ViewPager vpOwnerCarInfoDetail;
    CirclePageIndicator indicatorOwnerCarInfoDetail;
    TextView tvOwnerCarInfoDetailDescription, tvOwnerCarInfoDetailReviewNum, tvOwnerCarInfoDetailCarYears, tvOwnerCarInfoDetailCarMiters, tvOwnerCarInfoDetailCarFuel, tvOwnerCarInfoDetailCarGear, tvOwnerCarInfoDetailCarSeat, tvOwnerCarInfoDetailIsDelivery, tvOwnerCarInfoDetailIsOneday;
    SharedPreferences sharedPreferences;
    //TextView tvOwnerCarInfoDetailWeekdayCost, tvOwnerCarInfoDetailWeekendCost,tvOwnerCarinfoDetailReviewScore;
    JSONObject jsonResult;
    LinearLayout llOwnerCarInfoDetailReviews, llOwnerCarInfoDetailOption, llOwnerCarInfoDetailDelivery, llOwnerCarInfoDetailRentRule, llOwnerCarInfoDetailOneday, llOwnerCarInfoDetailRule;
    RatingBar rbOwnerCarInfoDetailCarReview;
    OptionDialogFragment optionDialog;
    AlertDialog.Builder abCarmonyRule, abDelivery, abOneDay, abPanalty;
    AlertDialog adCarmonyRule, adDelivery, adOneDay, adPanalty;
    List<String> images;
    Handler handler;
    Runnable update;
    int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_car_info_detail);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        images = new ArrayList<>();
        // for xml
        bindingXml();
        getJsonResult();
        dialogBuildUp();
        final URIImageViewPagerAdapter adapter = new URIImageViewPagerAdapter(getApplicationContext(), getLayoutInflater(), images);
        //ViewPager에 Adapter 설정
        vpOwnerCarInfoDetail.setAdapter(adapter);
        indicatorOwnerCarInfoDetail.setViewPager(vpOwnerCarInfoDetail);


    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                ibToolbar.setVisibility(View.GONE);

            }
        });
        ibToolbar.setBackgroundResource(R.drawable.ic_edit_black_24dp);
        ibToolbar.setVisibility(View.VISIBLE);
        tvToolbar.setText(getIntent().getStringExtra("tvToolbar"));
    }

    private void jsonCarinfoDetailResult(String err) {
        JSONObject jsonObject;
        if (err.equals("0")) {
            try {
                jsonObject = jsonResult.getJSONObject("ret");

                JSONArray jsonImgArray;
                jsonImgArray = jsonObject.getJSONArray("img");

                for (int i = 0; i < jsonObject.getInt("img_cnt"); i++) {
                    //실제 이미지 서버에 주소값과 같은 경우에 사용.
                    JSONObject jsonObject1 = jsonImgArray.getJSONObject(i);
                    images.add(jsonObject1.getString("carimage_img_t"));
                }


                URIImageViewPagerAdapter adapter = new URIImageViewPagerAdapter(getApplicationContext(), getLayoutInflater(), images);
                //ViewPager에 Adapter 설정
                vpOwnerCarInfoDetail.setAdapter(adapter);
                indicatorOwnerCarInfoDetail.setViewPager(vpOwnerCarInfoDetail);
                tvOwnerCarInfoDetailCarFuel.setText(jsonObject.getString("fuel"));
                tvOwnerCarInfoDetailCarGear.setText(jsonObject.getString("gear"));
                tvOwnerCarInfoDetailCarMiters.setText(jsonObject.getInt("mileage") + "만~" + (jsonObject.getInt("mileage") + 1) + "만km");
                tvOwnerCarInfoDetailCarSeat.setText(jsonObject.getString("seater") + "인승");
                tvOwnerCarInfoDetailCarYears.setText(jsonObject.getString("model_year"));
                tvOwnerCarInfoDetailDescription.setText(jsonObject.getString("car_content"));
                //tvOwnerCarInfoDetailWeekendCost.setText(jsonObject.getString("cost_deli_wend") + "원");
                //tvOwnerCarInfoDetailWeekdayCost.setText(jsonObject.getString("cost_deli_wday") + "원");
                if (jsonObject.getString("isdelivery").equals("y"))
                    tvOwnerCarInfoDetailIsDelivery.setText("필수");
                    //현재는 딜리버리 비용으로 받고 있기 때문에 무조건 필수다.
                else
                    tvOwnerCarInfoDetailIsDelivery.setText("필수");

                if (jsonObject.getString("isoneday").equals("y"))
                    tvOwnerCarInfoDetailIsOneday.setText("필수");
                else
                    tvOwnerCarInfoDetailIsOneday.setText("필수 아님");
                //    if (!jsonObject.getString("carinfo_review_score").equals("0"))
                //        tvOwnerCarinfoDetailReviewScore.setText(jsonObject.getString("carinfo_review_score"));
                if (!jsonObject.getString("carinfo_review_cnt").equals("0"))
                    tvOwnerCarInfoDetailReviewNum.setText(jsonObject.getString("carinfo_review_cnt"));

                rbOwnerCarInfoDetailCarReview.setRating((float) (jsonObject.getDouble("carinfo_review_score")));
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

    private void bindingXml() {
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        ibToolbar = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_carinfo);
        indicatorOwnerCarInfoDetail = (CirclePageIndicator) findViewById(R.id.indicator_owner_car_info_detail);
        vpOwnerCarInfoDetail = (ViewPager) findViewById(R.id.vp_owner_car_info_detail_car);

        tvOwnerCarInfoDetailCarFuel = (TextView) findViewById(R.id.tv_owner_car_info_detail_info_car_fuel);
        tvOwnerCarInfoDetailCarGear = (TextView) findViewById(R.id.tv_owner_car_info_detail_info_car_gear);
        tvOwnerCarInfoDetailCarMiters = (TextView) findViewById(R.id.tv_owner_car_info_detail_info_car_miters);
        tvOwnerCarInfoDetailCarSeat = (TextView) findViewById(R.id.tv_owner_car_info_detail_info_car_seat);
        tvOwnerCarInfoDetailCarYears = (TextView) findViewById(R.id.tv_owner_car_info_detail_info_car_years);
        tvOwnerCarInfoDetailDescription = (TextView) findViewById(R.id.tv_owner_car_info_detail_car_description);
        tvOwnerCarInfoDetailIsDelivery = (TextView) findViewById(R.id.tv_owner_car_info_detail_is_delivery);
        tvOwnerCarInfoDetailIsOneday = (TextView) findViewById(R.id.tv_owner_car_info_detail_is_oneday);
        tvOwnerCarInfoDetailReviewNum = (TextView) findViewById(R.id.tv_owner_car_info_detail_car_review_num);

        /* tvOwnerCarInfoDetailWeekdayCost = (TextView) findViewById(R.id.tv_owner_car_info_detail_weekday);
        tvOwnerCarInfoDetailWeekendCost = (TextView) findViewById(R.id.tv_owner_car_info_detail_weekend);
        tvOwnerCarinfoDetailReviewScore = (TextView) findViewById(R.id.tv_owner_car_info_detail_car_reivew_score);
*/
        rbOwnerCarInfoDetailCarReview = (RatingBar) findViewById(R.id.rb_owner_car_info_detail_car_review);

        llOwnerCarInfoDetailReviews = (LinearLayout) findViewById(R.id.ll_owner_car_info_detail_reviews);

        llOwnerCarInfoDetailDelivery = (LinearLayout) findViewById(R.id.ll_owner_car_info_detail_delivery);
        llOwnerCarInfoDetailOneday = (LinearLayout) findViewById(R.id.ll_owner_car_info_detail_oneday);
        llOwnerCarInfoDetailRentRule = (LinearLayout) findViewById(R.id.ll_owner_car_info_detail_panalty);
        llOwnerCarInfoDetailOption = (LinearLayout) findViewById(R.id.ll_owner_car_info_detail_option);
        llOwnerCarInfoDetailRule = (LinearLayout) findViewById(R.id.ll_owner_car_info_detail_carmony_rule);


        llOwnerCarInfoDetailReviews.setOnClickListener(this);
        ibToolbar.setOnClickListener(this);
        llOwnerCarInfoDetailDelivery.setOnClickListener(this);
        llOwnerCarInfoDetailOneday.setOnClickListener(this);
        llOwnerCarInfoDetailRentRule.setOnClickListener(this);
        llOwnerCarInfoDetailOption.setOnClickListener(this);
        llOwnerCarInfoDetailRule.setOnClickListener(this);


    }

    private void getJsonResult() {
        SendPost sendPost = new SendPost(OwnerCarInfoDetailActivity.this);
        sendPost.setUrl(getString(R.string.url_get_car_detail_info));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&carinfo_id=" + getIntent().getStringExtra("carinfoId"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_owner_car_info_detail_delivery:
                adDelivery.show();
                break;

            case R.id.ll_owner_car_info_detail_panalty:
                adPanalty.show();
                break;

            case R.id.ll_owner_car_info_detail_oneday:
                adOneDay.show();
                break;

            case R.id.ll_owner_car_info_detail_option:
                Bundle arguments = new Bundle();
                arguments.putInt("type", CLICKABLE_NO);
                optionDialog.setArguments(arguments);
                optionDialog.show(getFragmentManager(), "OptionDialog");
                break;

            case R.id.ll_owner_car_info_detail_carmony_rule:
                adCarmonyRule.show();
                break;

            case R.id.ll_owner_car_info_detail_reviews:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UserReviewsActivity.class);
                try {
                    intent.putExtra("resultType", "1");
                    intent.putExtra("carinfoId", jsonResult.getJSONObject("ret").getString("carinfo_id"));
                    intent.putExtra("carinfoReviewCnt", jsonResult.getJSONObject("ret").getString("carinfo_review_cnt"));
                    intent.putExtra("carinfoReviewScore", (float) jsonResult.getJSONObject("ret").getDouble("carinfo_review_score"));
                    intent.putExtra("title", "차량 리뷰");
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);

                }
                startActivity(intent);
                break;
            case R.id.ib_toolbar_carinfo:
                MainActivity.showToast(getApplicationContext(), "카모니 폴리스로 연락주세요!");
                break;

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

        //Delivery
        abDelivery = new AlertDialog.Builder(this);
        mView = getLayoutInflater().inflate(R.layout.dialog_delivery, null, false);
        btClose = (Button) mView.findViewById(R.id.bt_alert_delivery_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adDelivery.dismiss();
            }
        });
        abDelivery.setView(mView);
        adDelivery = abDelivery.create();

        //Oneday
        abOneDay = new AlertDialog.Builder(this);
        mView = getLayoutInflater().inflate(R.layout.dialog_oneday, null, false);
        btClose = (Button) mView.findViewById(R.id.bt_alert_oneday_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adOneDay.cancel();
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

            Log.d("abt", result);
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            jsonCarinfoDetailResult(err);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }
}
