package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.crash.FirebaseCrash;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat
 * <p>
 * <pre>
 * <b>History:</b>
 *    Latte, 1.0, 2017.03.10 초기작성
 * </pre>
 *
 * @author Latte
 */

public class ChatPaymentActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int CHATPAYMENT_CODE = 1010;
    private static final String TAG = "ChatPaymentActivity";

    public static final int CHATPAYMENT_COUPON_CODE = 1012;
    String couponId = "0", couponType = "0", couponDisCredit = "0", usercouponeId = "0";
    Toolbar tbResult;
    TextView tvResult, tvChatPaymentCouponPayment, tvChatPaymentLocation, tvChatPaymentFinalCost, tvChatPaymentCarModel, tvChatPaymentCarYears, tvChatPaymentEndDate, tvChatPaymentTotalCost, tvChatPaymentRentalCost, tvChatPaymentDeliCost, tvChatPaymentOnedayCost, tvChatPaymentOnedayWarning, tvChatPaymentIsDelivery, tvChatPaymentIsOneday, tvChatPaymentOnlineCost, tvChatPaymentOfflineCost, tvChatPaymentLateCost, tvChatPaymentStartDate;
    SharedPreferences sharedPreferences;
    LinearLayout llOneday, llCarmonyRule, llDilivery, llPanalty;
    RelativeLayout rlOnedayCost,rlChatPaymentCoupon, rlAgree;
    String carShape, isWend, renthour, strOwnerPhone, strFinalCost, strTotalCost, strRentalCost, strDeliCost, strOnedayCost, strOnlineCost, strOfflineCost, strLateCost;
    JSONObject joSetPayment, joGetReservation, joGetPayment;
    Button btChatPaymentRes, btChatPaymentCoupon;
    RecyclerView.LayoutManager layoutManager;
    AlertDialog.Builder abCarmonyRule, abDilivery, abOneDay, abPanalty;
    AlertDialog adCarmonyRule, adDilivery, adOneDay, adPanalty;
    CheckBox cbChatPaymentRes, cbChatPaymentRule;
    GroupChannel mGroupChannel;
    ImageView ivChatPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_payment);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        layoutManager = new LinearLayoutManager(ChatPaymentActivity.this, LinearLayoutManager.HORIZONTAL, false);

        // for xml
        bindingXml();
        getJsonResult();
        dialogBuildUp();

        if (getIntent().getStringExtra("is_pay").equals("1")) {
            btChatPaymentRes.setEnabled(false);
            rlAgree.setVisibility(View.GONE);
            btChatPaymentCoupon.setEnabled(false);
            btChatPaymentRes.setText("결제가 완료되었습니다.");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHATPAYMENT_CODE) {
                if (data.getBooleanExtra("is_done", false)) {
                    SendPost sendPost = new SendPost(ChatPaymentActivity.this);
                    sendPost.setUrl(getString(R.string.url_get_reservation_state));
                    sendPost.setSleepTime(1000);
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            String err;
                            JSONObject joGetState;
                            try {
                                joGetState = new JSONObject(result);

                                err = joGetState.getString("err");
                                if (err.equals("0") && !joGetState.getJSONObject("ret").getString("state").equals("2")) {
                                    //결제진행이 완료되었다면 (취소(state=0) 혹은 결제완료(state=1))
                                    if (joGetState.getJSONObject("ret").getString("state").equals("1")) {
                                        //결제 완료 처리
                                        //정상처리 루틴.
                                        SendPost sendPostMain = new SendPost(ChatPaymentActivity.this);
                                        sendPostMain.setUrl(getString(R.string.url_set_payment));
                                        sendPostMain.setCallbackEvent(new SendPost.CallbackEvent() {
                                            @Override
                                            public void getResult(String result) {
                                                try {
                                                    String err;

                                                    joSetPayment = new JSONObject(result);

                                                    err = joSetPayment.getString("err");
                                                    if (err.equals("0")) {
                                                        //채널 값에 결제가 완료되었다고 알려줌. is_pay를 통해
                                                        GroupChannel.getChannel(data.getStringExtra("channel_url"), new GroupChannel.GroupChannelGetHandler() {
                                                            @Override
                                                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                                                if (e != null) {
                                                                    return;
                                                                }

                                                                mGroupChannel = groupChannel;
                                                                mGroupChannel.markAsRead();
                                                                List<String> keys = new ArrayList<>();
                                                                keys.add("owner_id");
                                                                keys.add("is_check");
                                                                keys.add("is_pay");
                                                                keys.add("reservation_id");

                                                                HashMap<String, String> mMap = new HashMap<>();
                                                                mMap.put("owner_id", getIntent().getStringExtra("owner_id"));
                                                                mMap.put("is_check", getIntent().getStringExtra("is_check"));
                                                                mMap.put("reservation_id", getIntent().getStringExtra("reservation_id"));
                                                                mMap.put("is_pay", "1");


                                                                // Not Error
                                                                mGroupChannel.updateMetaData(mMap, new BaseChannel.MetaDataHandler() {
                                                                    @Override
                                                                    public void onResult(Map<String, String> map, SendBirdException e) {
                                                                        Log.d(TAG, "TESTT - 업데이트완료");
                                                                    }
                                                                });
                                                            }
                                                        });
                                                        btChatPaymentRes.setEnabled(false);
                                                        btChatPaymentRes.setText("결제가 완료되었습니다.");
                                                        MainActivity.showLongToast(getApplicationContext(), "결제가 완료되었습니다.");

                                                        //날짜 형식 변경
                                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        SimpleDateFormat format2 = new SimpleDateFormat("MM월 dd일 a hh시mm분");
                                                        String startDate = "", endDate = "", rentDate = "";
                                                        try {
                                                            startDate = format2.format(format.parse(tvChatPaymentStartDate.getText().toString()));
                                                            endDate = format2.format(format.parse(tvChatPaymentEndDate.getText().toString()));

                                                        } catch (ParseException e) {
                                                            FirebaseCrash.logcat(Log.ERROR, TAG, "ParseException Fail");
                                                            FirebaseCrash.report(e);
                                                            e.printStackTrace();
                                                        }
                                                        rentDate = startDate + " ~ " + endDate;

                                                        //드라이버 예약 문자 발송
                                                        SendPost sendPostDriver = new SendPost(ChatPaymentActivity.this);
                                                        sendPostDriver.setCallbackEvent(new SendPost.CallbackEvent() {
                                                            @Override
                                                            public void getResult(String result) {
                                                                String err = "";
                                                                try {
                                                                    JSONObject jsonResult;
                                                                    jsonResult = new JSONObject(result);

                                                                    err = jsonResult.getString("err");
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                                                    FirebaseCrash.report(e);

                                                                }
                                                            }
                                                        });
                                                        sendPostDriver.setUrl(getString(R.string.url_set_reservation_sending_message));
                                                        sendPostDriver.setLoadingImg(false);
                                                        sendPostDriver.execute("user_type=" + "0" +
                                                                "&car_model=" + tvChatPaymentCarModel.getText().toString() +
                                                                "&rent_date=" + rentDate +
                                                                "&rent_location=" + tvChatPaymentLocation.getText().toString() +
                                                                "&cost_online=" + String.format("%,d", Integer.parseInt(strOnlineCost)) +
                                                                "&cost_oneday=" + String.format("%,d", Integer.parseInt(strOnedayCost)) +
                                                                "&user_phone=" + sharedPreferences.getString(getString(R.string.shared_userphone), ""));
                                                        //차주 예약 문자 발송
                                                        SendPost sendPostOwner = new SendPost(ChatPaymentActivity.this);
                                                        sendPostOwner.setCallbackEvent(new SendPost.CallbackEvent() {
                                                            @Override
                                                            public void getResult(String result) {
                                                                String err = "";
                                                                try {
                                                                    JSONObject jsonResult;
                                                                    jsonResult = new JSONObject(result);

                                                                    err = jsonResult.getString("err");
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                                                    FirebaseCrash.report(e);

                                                                }
                                                            }
                                                        });
                                                        sendPostOwner.setUrl(getString(R.string.url_set_reservation_sending_message));
                                                        sendPostOwner.setLoadingImg(false);
                                                        sendPostOwner.execute("user_type=" + "1" +
                                                                "&car_model=" + tvChatPaymentCarModel.getText().toString() +
                                                                "&rent_date=" + rentDate +
                                                                "&rent_location=" + tvChatPaymentLocation.getText().toString() +
                                                                "&cost_online=" + String.format("%,d", Integer.parseInt(strOnlineCost)) +
                                                                "&cost_oneday=" + String.format("%,d", Integer.parseInt(strOnedayCost)) +
                                                                "&user_phone=" + strOwnerPhone);
                                                        finish();

                                                    } else {
                                                        MainActivity.showLongToast(getApplicationContext(), "일시적 오류 입니다. 잠시 후 다시 시도해주세요.");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                                    FirebaseCrash.report(e);
                                                }
                                            }
                                        });

                                        sendPostMain.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                                                + "&reservation_id=" + data.getStringExtra("reservation_id")
                                                + "&finally_cost=" + data.getStringExtra("finally_cost")
                                                + "&origin_cost=" + data.getStringExtra("origin_cost")
                                                + "&usercoupon_id=" + data.getStringExtra("usercoupon_id")
                                                + "&coupon_id=" + data.getStringExtra("coupon_id")
                                                + "&owner_id=" + data.getStringExtra("owner_id"));

                                    } else {
                                        //결제가 되지 않았을 때,
                                        MainActivity.showToast(ChatPaymentActivity.this, "결제가 실패하였습니다. 다시 결제를 진행해주세요.");
                                    }
                                    //결제진행이 완료되기 전이라면 아무것도 안한당.
                                } else {
                                    // 결제 도중에 마무리 되지 않은 경우,
                                    // 결제 도중(state=2를 0으로 돌리고, 결제가 취소 되었음을 알린다.
                                    SendPost sendPost = new SendPost(ChatPaymentActivity.this);
                                    sendPost.setUrl(getString(R.string.url_set_reservation_state));
                                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                                        @Override
                                        public void getResult(String result) {
                                            MainActivity.showLongToast(ChatPaymentActivity.this, "결제 오류 입니다. 다시 결제를 진행해주세요.");
                                        }
                                    });
                                    sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                                            + "&reservation_id=" + data.getStringExtra("reservation_id")
                                            + "&state=" + 0);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                            + "&reservation_id=" + data.getStringExtra("reservation_id"));


                } else {

                    Log.d(TAG, "여기 들어오니");
                    //결제 완료되지 않은체 취소한 경우
                    //결제 도중(state=2를 0으로 돌리고, 결제가 취소 되었음을 알린다.
                    SendPost sendPost = new SendPost(ChatPaymentActivity.this);
                    sendPost.setUrl(getString(R.string.url_set_reservation_state));
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {

                            MainActivity.showToast(ChatPaymentActivity.this, "결제 진행이 취소 되었습니다.");
                        }
                    });
                    sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                            + "&reservation_id=" + data.getStringExtra("reservation_id")
                            + "&state=" + 0);
                }

            } else if (requestCode == CHATPAYMENT_COUPON_CODE) {
                //쿠폰을 선택했다면.
                couponId = data.getStringExtra("coupon_id");
                couponType = data.getStringExtra("coupon_type");// 1이면 금액, 2면 퍼센테이지
                couponDisCredit = data.getStringExtra("coupon_dis_credit");
                usercouponeId = data.getStringExtra("usercoupon_id");
                if (couponType.equals("1")) {
                    //금액 할인인 경우
                    tvChatPaymentFinalCost.setText(String.format("%,d", Integer.parseInt(strFinalCost) - Integer.parseInt(couponDisCredit)) + "원");
                    tvChatPaymentCouponPayment.setText(String.format("%,d", Integer.parseInt(couponDisCredit)) + "원");
                } else if (couponType.equals("2")) {
                    // 퍼센테이지 할인인 경우우
                    tvChatPaymentFinalCost.setText(String.format("%,d", Integer.parseInt(strFinalCost) - Integer.parseInt(strFinalCost) * Integer.parseInt(couponDisCredit) / 100) + "원");
                    tvChatPaymentCouponPayment.setText(String.format("%,d", Integer.parseInt(strFinalCost) * Integer.parseInt(couponDisCredit) / 100) + "원");
                }
            }
        }
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
        tvResult = (TextView) tbResult.findViewById(R.id.tv_toolbar);
        llPanalty = (LinearLayout) findViewById(R.id.ll_chat_payment_panalty);
        llCarmonyRule = (LinearLayout) findViewById(R.id.ll_chat_payment_carmony_rule);
        llDilivery = (LinearLayout) findViewById(R.id.ll_chat_payment_delivery);
        btChatPaymentCoupon = (Button) findViewById(R.id.bt_chat_payment_coupon);
        tvChatPaymentFinalCost = (TextView) findViewById(R.id.tv_chat_payment_final_payment);
        llOneday = (LinearLayout) findViewById(R.id.ll_chat_payment_oneday);
        rlOnedayCost = (RelativeLayout) findViewById(R.id.rl_chat_payment_oneday_cost);
        rlAgree = (RelativeLayout) findViewById(R.id.rl_chat_payment_agree);
        ivChatPayment = (ImageView) findViewById(R.id.iv_chat_payment_car);
        tvChatPaymentCarModel = (TextView) findViewById(R.id.tv_chat_payment_car_model);
        tvChatPaymentCarYears = (TextView) findViewById(R.id.tv_chat_payment_car_years);
        tvChatPaymentEndDate = (TextView) findViewById(R.id.tv_chat_payment_end_date);
        tvChatPaymentLocation = (TextView) findViewById(R.id.tv_chat_payment_location);
        tvChatPaymentStartDate = (TextView) findViewById(R.id.tv_chat_payment_start_date);
        tvChatPaymentTotalCost = (TextView) findViewById(R.id.tv_chat_payment_total_cost);
        tvChatPaymentRentalCost = (TextView) findViewById(R.id.tv_chat_payment_rental_cost);
        tvChatPaymentDeliCost = (TextView) findViewById(R.id.tv_chat_payment_delivery_cost);
        tvChatPaymentOnedayCost = (TextView) findViewById(R.id.tv_chat_payment_oneday_cost);
        tvChatPaymentOnedayWarning = (TextView) findViewById(R.id.tv_chat_payment_oneday_warning);
        tvChatPaymentLateCost = (TextView) findViewById(R.id.tv_chat_payment_late_cost);
        tvChatPaymentOnlineCost = (TextView) findViewById(R.id.tv_chat_payment_online_cost);
        tvChatPaymentOfflineCost = (TextView) findViewById(R.id.tv_chat_payment_offline_cost);
        tvChatPaymentIsDelivery = (TextView) findViewById(R.id.tv_chat_payment_is_delivery);
        tvChatPaymentIsOneday = (TextView) findViewById(R.id.tv_chat_payment_is_oneday);
        btChatPaymentRes = (Button) findViewById(R.id.bt_chat_payment_res);
        tvChatPaymentCouponPayment = (TextView) findViewById(R.id.tv_chat_payment_coupon_payment);
        cbChatPaymentRes = (CheckBox) findViewById(R.id.cb_chat_payment_res_payment);
        rlChatPaymentCoupon = (RelativeLayout)findViewById(R.id.rl_chat_payment_coupon);
        cbChatPaymentRule = (CheckBox) findViewById(R.id.cb_chat_payment_carmony_rule);

        btChatPaymentCoupon.setOnClickListener(this);
        btChatPaymentRes.setOnClickListener(this);
        llCarmonyRule.setOnClickListener(this);
        llDilivery.setOnClickListener(this);
        llOneday.setOnClickListener(this);
        llPanalty.setOnClickListener(this);

    }

    private void getJsonResult() {
        SendPost sendPost = new SendPost(ChatPaymentActivity.this);
        sendPost.setUrl(getString(R.string.url_get_reservation));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;


                    joGetReservation = new JSONObject(result);

                    err = joGetReservation.getString("err");
                    jsonResultChatPaymentResult(err);
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);


                }
            }
        });
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&reservation_id=" + getIntent().getStringExtra("reservation_id"));
    }

    private void jsonResultChatPaymentResult(String err) {
        JSONObject jsonObject;
        if (err.equals("0")) {
            try {
                jsonObject = joGetReservation.getJSONObject("ret");
                carShape = jsonObject.getString("car_shape");
                strOwnerPhone = jsonObject.getString("phone");
                strTotalCost = jsonObject.getString("cost");
                strRentalCost = "0";
                strDeliCost = jsonObject.getString("delivery_cost");
                strOnedayCost = jsonObject.getString("oneday_cost");
                strOnlineCost = Integer.parseInt(strTotalCost) - Integer.parseInt(strOnedayCost) + "";
                strFinalCost = strOnlineCost;
                strOfflineCost = jsonObject.getString("oneday_cost");

                strLateCost = jsonObject.getString("late_cost");
                Glide.with(getApplicationContext()).load(jsonObject.getString("carinfo_img_t"))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .fitCenter().into(ivChatPayment);
                tvChatPaymentCarModel.setText(jsonObject.getString("model"));
                tvChatPaymentCarYears.setText(jsonObject.getString("model_year"));
                tvResult.setText(getString(R.string.payment_info));
                tvChatPaymentStartDate.setText(jsonObject.getString("start_date"));
                tvChatPaymentEndDate.setText(jsonObject.getString("end_date"));
                tvChatPaymentTotalCost.setText(String.format("%,d", Integer.parseInt(strTotalCost)) + "원");
                //렌트 비용은 무조건 0원이다.
                tvChatPaymentLocation.setText(jsonObject.getString("location"));
                tvChatPaymentFinalCost.setText(String.format("%,d", Integer.parseInt(strFinalCost)) + "원");
                tvChatPaymentRentalCost.setText(String.format("%,d", Integer.parseInt(strRentalCost))+ "원");
                tvChatPaymentOnlineCost.setText(String.format("%,d", Integer.parseInt(strTotalCost) - Integer.parseInt(strOnedayCost)) + "원");
                tvChatPaymentDeliCost.setText(String.format("%,d", Integer.parseInt(strDeliCost)) + "원");
                tvChatPaymentLateCost.setText(String.format("%,d", Integer.parseInt(strLateCost)) + "원");
                tvChatPaymentOnedayCost.setText(String.format("%,d", Integer.parseInt(strOnedayCost)) + "원");
                tvChatPaymentOfflineCost.setText(String.format("%,d", Integer.parseInt(strOfflineCost)) + "원");
                if (jsonObject.getString("isoneday").equals("y")) {
                    rlOnedayCost.setVisibility(View.VISIBLE);
                    tvChatPaymentIsOneday.setText("필수");
                } else {
                    tvChatPaymentIsOneday.setText("필수 아님");
                    rlOnedayCost.setVisibility(View.GONE);
                }
                if (jsonObject.getString("state").equals("1")) {
                    getPayment();
                    //결제가 완료된 건이라면
                    btChatPaymentRes.setEnabled(false);
                    btChatPaymentRes.setText("결제가 완료되었습니다.");
                    rlAgree.setVisibility(View.GONE);
                    btChatPaymentCoupon.setEnabled(false);
                } else if (jsonObject.getString("state").equals("3")) {
                    btChatPaymentRes.setEnabled(false);
                    rlAgree.setVisibility(View.GONE);
                    btChatPaymentCoupon.setEnabled(false);
                    btChatPaymentRes.setText("예약이 취소되었습니다.");
                } else {
                    if (getIntent().getStringExtra("owner_id").equals(sharedPreferences.getString(getString(R.string.shared_userid), ""))) {
                        btChatPaymentRes.setEnabled(false);
                        rlAgree.setVisibility(View.GONE);
                        btChatPaymentCoupon.setEnabled(false);
                        rlChatPaymentCoupon.setVisibility(View.GONE);
                        btChatPaymentRes.setText("결제가 진행중입니다.");
                    } else {
                        rlChatPaymentCoupon.setVisibility(View.VISIBLE);
                        rlAgree.setVisibility(View.VISIBLE);
                        btChatPaymentCoupon.setEnabled(true);
                        btChatPaymentRes.setEnabled(true);
                        btChatPaymentRes.setText("결제 및 예약하기");
                    }
                }

                if (jsonObject.getString("isdelivery").equals("y"))
                    tvChatPaymentIsDelivery.setText("필수");
                    //현재는 딜리버리 비용으로 받고 있기 때문에 무조건 필수다.
                else
                    tvChatPaymentIsDelivery.setText("필수 아님");
                //몇 시간 이용 했는지 체크한다.
                renthour = checkHour(jsonObject.getString("start_date"), jsonObject.getString("end_date"));

                //주말이 포함되어 있는지 체크
                if (checkWend(jsonObject.getString("start_date"), jsonObject.getString("end_date")))
                    isWend = "y";
                else
                    isWend = "n";

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);

            }
            //이미지 추가
            //텍스트뷰 세팅

        } else {
            try {
                MainActivity.showToast(getApplicationContext(), joGetReservation.getString("err_result"));
            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);

            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_chat_payment_carmony_rule:
                adCarmonyRule.show();
                break;

            case R.id.ll_chat_payment_delivery:
                adDilivery.show();
                break;

            case R.id.ll_chat_payment_oneday:
                adOneDay.show();
                break;

            case R.id.ll_chat_payment_panalty:
                adPanalty.show();
                break;
            case R.id.bt_chat_payment_coupon:
                Intent intentCoupon = new Intent(ChatPaymentActivity.this, UserCouponActivity.class);
                intentCoupon.putExtra("readonly", false);
                intentCoupon.putExtra("is_wend", isWend);
                intentCoupon.putExtra("renthour", renthour);
                intentCoupon.putExtra("car_shape", carShape);
                startActivityForResult(intentCoupon, CHATPAYMENT_COUPON_CODE);
                break;
            case R.id.bt_chat_payment_res:
                if (cbChatPaymentRule.isChecked()) {
                    if (cbChatPaymentRes.isChecked()) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), ChatPaymentDetailActivity.class);

                            intent.putExtra("is_check", getIntent().getStringExtra("is_check"));
                            intent.putExtra("channel_url", getIntent().getStringExtra("channel_url"));
                            intent.putExtra("reservation_id", getIntent().getStringExtra("reservation_id"));
                            intent.putExtra("is_pay", getIntent().getStringExtra("is_pay"));
                            intent.putExtra("owner_id", getIntent().getStringExtra("owner_id"));
                            intent.putExtra("userinfo_id", joGetReservation.getJSONObject("ret").getString("userinfo_id"));
                            intent.putExtra("goodsName", getIntent().getStringExtra("user_name") + " " + tvChatPaymentCarModel.getText().toString() + tvChatPaymentCarYears.getText().toString());
                            intent.putExtra("usercoupon_id", usercouponeId);
                            intent.putExtra("coupon_id", couponId);
                            intent.putExtra("origin_cost", strOnlineCost);
                            intent.putExtra("amt", strFinalCost);
                            startActivityForResult(intent, CHATPAYMENT_CODE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MainActivity.showToast(ChatPaymentActivity.this, "결제내역에 동의하지 않았습니다.");
                    }
                } else {
                    MainActivity.showToast(ChatPaymentActivity.this, "약관에 동의하지 않았습니다.");

                }
                break;

            default:
                break;

        }

    }

    //시작 날짜와 끝나는 날짜가 모두 주중이면서  중간에 주말이 끼지 않았을 경우엔 주중.
    private boolean checkWend(String startDate, String endDate) {
        //주말 포함이면 true 아니면 false
        int startWeek, endWeek, startDay, endDay;
        //시작 일자와 종료 일자가 주중인지 체크.
        boolean startWday, endWday;
        if (Integer.parseInt(renthour) > 144) {
            //일주일 이상 이용한다면
            return true;
        } else {

            //일주일 이하로 이용 하는 경우
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date dStart = transFormat.parse(startDate);
                Date dEnd = transFormat.parse(endDate);
                Calendar cStart = new GregorianCalendar();
                cStart.setTime(dStart);
                startWeek = cStart.get(Calendar.WEEK_OF_YEAR);
                Calendar cEnd = new GregorianCalendar();
                cEnd.setTime(dEnd);
                endWeek = cEnd.get(Calendar.WEEK_OF_YEAR);
                if (startWeek == endWeek) {
                    //같은 주 인지 체크
                    startDay = cStart.get(Calendar.DAY_OF_WEEK);
                    endDay = cEnd.get(Calendar.DAY_OF_WEEK);
                    if (checkDay(startDay, cStart) && checkDay(endDay, cEnd))
                        return false;
                    else
                        return true;
                } else {
                    //주차가 넘어가면 무조건 주말 포함.
                    return true;
                }

            } catch (ParseException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "string to date parse Fail");
                FirebaseCrash.report(e);


            }
            return false;
        }


    }

    //주중이면 트루 주말이면 false;
    private boolean checkDay(int day, Calendar calendar) {
        if (day == 1) {
            //일요일
            if (calendar.get(Calendar.HOUR_OF_DAY) > 18)
                return true;
            else
                return false;
        } else if (day <= 5 && day >= 2) {
            //월,화,수,목
            return true;
        } else if (day == 6) {
            //금요일
            if (calendar.get(Calendar.HOUR_OF_DAY) < 18)
                return true;
            else
                return false;
        } else {
            return false;
            //토요일
        }
    }

    private String checkHour(String startDate, String endDate) {
        //몇 시간 이용하는지.
        long diffHour = 0;
        try {
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dStart = transFormat.parse(startDate);
            Date dEnd = transFormat.parse(endDate);

            long diff = dEnd.getTime() - dStart.getTime();
            diffHour = diff / (60 * 60 * 1000);

        } catch (ParseException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "string to date parse Fail");
            FirebaseCrash.report(e);
        }
        //시간 값 반환.
        return diffHour + "";

    }

    private void getPayment() {
        SendPost sendPost = new SendPost(ChatPaymentActivity.this);
        sendPost.setUrl(getString(R.string.url_get_payment));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    String err;

                    joGetPayment = new JSONObject(result);
                    err = joGetPayment.getString("err");
                    if (err.equals("0")) {
                        JSONObject jsonObject = joGetPayment.getJSONObject("ret");
                        tvChatPaymentFinalCost.setText(String.format("%,d", Integer.parseInt(jsonObject.getString("finally_cost"))) + "원");
                        tvChatPaymentCouponPayment.setText(String.format("%,d", Integer.parseInt(jsonObject.getString("origin_cost")) - Integer.parseInt(jsonObject.getString("finally_cost"))) + "원");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);


                }
            }
        });
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&reservation_id=" + getIntent().getStringExtra("reservation_id"));
    }
}