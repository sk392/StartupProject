package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.UserCouponAdapter;
import com.sk392.kr.carmony.Item.CouponItem;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * shared에 저장되어있는 토큰을 이용해 서버에서 coupon 리스트를 받아오는 액티비티
 * readonly와 그렇지 않은 것으로 나누고 readonly는 모든 쿠폰을, 아닌 경우엔 사용 가능한 쿠폰을 리스트로 출력한다.
 *
 * <pre>
 *     Itnent의 입력값과 출력값
 *     input : readonly(y,n) , car_shape(car_light,car_compact,car_semi_medium,car_medium,car_large,car_suv) ,is_wend(y,n)
 *     output : coupon_id,coupon_type,coupon_dis_credit,usercoupon_id
 * </pre>
 * <pre>
 * <b>History:</b>
 *    Latte, 1.0, 2017.02.13 초기작성
 * </pre>
 *
 * @author Latte
 */
public class UserCouponActivity extends AppCompatActivity implements SendPost.CallbackEvent, View.OnClickListener {
    RecyclerView rvCoupon;
    UserCouponAdapter userCouponAdapter;
    List<CouponItem> list;
    CouponItem item;
    Toolbar toolbar;
    private static final String TAG = "UserCouponActivity";

    TextView tvToolbar, tvCouponEnable;
    String result, err;
    JSONObject jsonResult;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingXml();
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvToolbar.setText("쿠폰함");
        list = new ArrayList<>();
        setupList();

    }

    private void setupList() {
        SendPost sendPost = new SendPost(UserCouponActivity.this);
        sendPost.setUrl(getString(R.string.url_get_user_coupon_list));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), ""));
    }

    private List<CouponItem> jsonRievewResult(String err) {
        List<CouponItem> mlist = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject jsonObject;
        if (err.equals("0")) {
            try {
                jsonArray = jsonResult.getJSONArray("ret");

                for (int i = 0; i < jsonResult.getInt("cnt"); i++) {
                    String strTime;
                    CouponItem mItem = null;
                    SimpleDateFormat timeFormat;
                    jsonObject = jsonArray.getJSONObject(i);

                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date to = null;
                    try {
                        to = transFormat.parse(jsonObject.getString("expiration_date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        FirebaseCrash.logcat(Log.ERROR, TAG, "ParseException Fail");
                        FirebaseCrash.report(e);
                    }
                    timeFormat = new SimpleDateFormat("yyyy년 M월 d일");
                    strTime = timeFormat.format(to);
                    Log.d("abababab",jsonObject.toString());

                    HashMap<String, String> hmCarShape = new HashMap<>();
                    hmCarShape.put("car_light", jsonObject.getString("coupon_car_light"));
                    hmCarShape.put("car_compact", jsonObject.getString("coupon_car_compact"));
                    hmCarShape.put("car_semi_medium", jsonObject.getString("coupon_car_semi_medium"));
                    hmCarShape.put("car_medium", jsonObject.getString("coupon_car_medium"));
                    hmCarShape.put("car_large", jsonObject.getString("coupon_car_large"));
                    hmCarShape.put("car_suv", jsonObject.getString("coupon_car_suv"));



                    String type = "";
                    if (jsonObject.getString("coupon_type").equals("1")) {
                        //금액할인
                        mItem = new CouponItem(jsonObject.getString("coupon_type"), jsonObject.getString("coupon_id"), jsonObject.getString("coupon_price")
                                , jsonObject.getString("coupon_wday"), jsonObject.getString("coupon_wend"), hmCarShape, strTime, jsonObject.getString("usercoupon_id"));

                    } else if (jsonObject.getString("coupon_type").equals("2")) {
                        //퍼센트 할인
                        mItem = new CouponItem(jsonObject.getString("coupon_type"), jsonObject.getString("coupon_id"), jsonObject.getString("coupon_percent")
                                , jsonObject.getString("coupon_wday"), jsonObject.getString("coupon_wend"), hmCarShape, strTime, jsonObject.getString("usercoupon_id"));

                    }
                    if (!getIntent().getBooleanExtra("readonly", true)) {
                        //리드온리가 아닐 경우
                        //car_shape와 wend, wday값을 비교해서
                        if (hmCarShape.get(getIntent().getStringExtra("car_shape")).equals("y")) {
                            //getIntent로 전달 받은carShape의 값이 Y 인 경우만 출력.

                            if (getIntent().getStringExtra("is_wend").equals("y") && jsonObject.getString("coupon_wend").equals("y")) {
                                //전달 받은 값이 주말이 포함이고, 쿠폰도 주말이 가능하다면. 리스트 추가.
                                mlist.add(mItem);

                            }else if(jsonObject.getString("coupon_wday").equals("y") && !getIntent().getStringExtra("is_wend").equals("y")){
                                //전달 받은 값이 주말이 아니고, 쿠폰이 평일이 가능하다면 리스트 추가.
                                mlist.add(mItem);

                            }
                        }
                    } else {
                        //리드온리일 경우 가지고 있는 모든 쿠폰을 출력한다.
                        mlist.add(mItem);
                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);
            }
        }
        return mlist;
    }

    private void bindingXml() {
        setContentView(R.layout.activity_user_coupon);
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        rvCoupon = (RecyclerView) findViewById(R.id.rv_coupon);
        tvCouponEnable = (TextView) findViewById(R.id.tv_coupon_enable);


    }

    @Override
    public void getResult(String result) {
        try {
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            list = jsonRievewResult(err);

            if (list.size() > 0) {
                userCouponAdapter = new UserCouponAdapter(getApplicationContext(), list, R.layout.item_coupon, this);
                rvCoupon.setAdapter(userCouponAdapter);
                rvCoupon.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

            } else {
                tvCouponEnable.setVisibility(View.VISIBLE);
                rvCoupon.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onClick(View view) {
        if (!getIntent().getBooleanExtra("readonly", true)) {
            //readonly가 아닐 경우에 클릭.
            int itemPosition = rvCoupon.getChildLayoutPosition(view);
            item = list.get(itemPosition);
            Intent mIntent = new Intent();
            mIntent.putExtra("coupon_id", item.getCouponId());
            mIntent.putExtra("coupon_type", item.getType());
            mIntent.putExtra("coupon_dis_credit", item.getDisCredit());
            mIntent.putExtra("usercoupon_id", item.getUserCouponId());
            setResult(RESULT_OK, mIntent);
            finish();
        }
    }
}
