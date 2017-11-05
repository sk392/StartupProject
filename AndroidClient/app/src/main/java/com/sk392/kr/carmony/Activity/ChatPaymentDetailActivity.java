package com.sk392.kr.carmony.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Dialog.LoadingProgressDialog;
import com.sk392.kr.carmony.R;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class ChatPaymentDetailActivity extends AppCompatActivity {

    private static final String TAG = "ChatPaymentDetailAct";

    private WebView wvChatPaymentDetail;
    SharedPreferences sharedPreferences;
    boolean isDone= false;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_payment_detail);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        wvChatPaymentDetail = (WebView) findViewById(R.id.wv_chat_payment_detail);
        wvChatPaymentDetail.setWebViewClient(new TPayWebViewClient());

        new SleepTask(ChatPaymentDetailActivity.this).execute("");

        WebSettings webSettings = wvChatPaymentDetail.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        try {
            String str = "amt=" + URLEncoder.encode(getIntent().getStringExtra("amt"), "UTF-8")
                    + "&reservation_id=" + URLEncoder.encode(getIntent().getStringExtra("reservation_id"), "UTF-8")
                    + "&goodsName=" + URLEncoder.encode(getIntent().getStringExtra("goodsName"), "UTF-8")
                    + "&tid=" + URLEncoder.encode(getIntent().getStringExtra("reservation_id"), "UTF-8")
                    + "&buyerEmail=" + URLEncoder.encode(sharedPreferences.getString(getString(R.string.shared_email), ""), "UTF-8")
                    + "&buyerName=" + URLEncoder.encode(sharedPreferences.getString(getString(R.string.shared_name), ""), "UTF-8")
                    + "&tel=" + URLEncoder.encode(sharedPreferences.getString(getString(R.string.shared_userphone), ""), "UTF-8");
            wvChatPaymentDetail.postUrl("http://14.63.225.26:2780/api/tpay/mainPay.php", str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private class TPayWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
        }
/*

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            final SslErrorHandler mHandler = handler;

            new BottomDialog.Builder(getApplicationContext())
                    .setTitle("인증서 여부")
                    .setContent("인증되지 않은 보안을 사용하고 있습니다. 계속 진행하시겠습니까??")
                    .setCancelable(true)
                    .setNegativeText("취소")
                    .setPositiveText("예")
                    .setPositiveTextColorResource(R.color.white)
                    .setPositiveBackgroundColorResource(R.color.reddish_orange)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            mHandler.proceed();

                        }
                    })
                    .onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            mHandler.cancel();

                        }
                    })
                    .show();
        }
*/

        @Override
        public void onPageFinished(WebView view, String url) {
            //웹페이지 호출이 끝났을 때  ( 매 요청 시 1번만실행)
            if (url != null && url.equals("http://14.63.225.26:2780/api/tpay/returnPay.php")) {
                isDone =true;
                onBackPressed();
            }
        }

        /**
         * 새로운 URL이 현재 WebView에 로드되려고 할 때 호스트 응용 프로그램에게 컨트롤을
         * 대신할 기회를 줍니다. WebViewClient가 제공되지 않으면, 기본적으로 WebView는 URL에
         * 대한 적절한 핸들러를 선택하려고 Activity Manager에게 물어봅니다. WebViewClient가
         * 제공되면, 호스트 응용 프로그램이 URL을 처리한다는 의미인 true를 반환거나 현재
         * WebView가 URL을 처리한다는 의미인 false를 반환합니다.
         **/


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null && !url.equals("ansimclick.hyundiacard.com") &&
                    (url.contains("http://market.android.com")
                            || url.contains("intent")
                            || url.contains("ispmobile://")
                            || url.contains("kftc-bankpay")
                            || url.contains("com.kftc.bankpay.android")
                            || url.contains("com.lotte.lottesmartpay")
                            || url.contains("com.ahnlab.v3mobileplus")
                            || url.contains("lottesmartpay")
                            || url.contains("hanaansim")
                            || url.contains("cloudpay")
                            || url.contains("vguard")
                            || url.contains("droidxantivirus")
                            || url.contains("smhyundaiansimclick://")
                            || url.contains("smshinhanansimclick://")
                            || url.contains("smshinhancardusim://")
                            || url.contains("smartwall://")
                            || url.contains("appfree://")
                            || url.contains("market://")
                            || url.contains("v3mobile")
                            || url.contains("ansimclick")
                            || url.contains("http://m.ahnlab.com/kr/site/download")
                            || url.endsWith(".apk")
                    )) {
                try {
                    Intent intent = null;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException ex) {
                        Log.e("BBBrowser", "Bad URI " + url + ":" + ex.getMessage());
                        FirebaseCrash.logcat(Log.ERROR, TAG, "URISyntaxException Fail");
                        FirebaseCrash.report(ex);
                        return false;
                    }
                    //chrome 버젼 방식
                    if (url.startsWith("intent") || url.startsWith("lottesmartpay://") || url.startsWith("kb-acp://")) {
                        // 앱설치 체크를 합니다.
                        if (getPackageManager().resolveActivity(intent, 0) == null) {

                            String packagename = intent.getPackage();
                            if (packagename != null) {

                                Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                return true;
                            }
                        }
                        //구동방식은 PG:쇼핑몰에서 결정하세요.
                        int runType = 1;
                        if (runType == 1) {

                            Uri uri = Uri.parse(intent.getDataString());
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } else {
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setComponent(null);
                            try {
                                if (startActivityIfNeeded(intent, -1)) {
                                    return true;

                                }
                            } catch (ActivityNotFoundException ex) {
                                Log.e("BBerror ===>", ex.getMessage());
                                FirebaseCrash.logcat(Log.ERROR, TAG, "ActivityNotFoundException Fail");
                                FirebaseCrash.report(ex);

                                ex.printStackTrace();
                                return false;
                            }
                        }
                    } else { // 구 방식
                        try {
                            Uri uri = Uri.parse(url);
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            MainActivity.showLongToast(getApplicationContext(), "앱이 설치되어있는지 확인해주세요.");

                            FirebaseCrash.logcat(Log.ERROR, TAG, "ActivityNotFoundException Fail");
                            FirebaseCrash.report(e);

                        }
                    }
                    return true;
                } catch (ActivityNotFoundException e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "ActivityNotFoundException Fail");
                    FirebaseCrash.report(e);

                    e.printStackTrace();
                    return false;
                }
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript(url, null);
                } else {
                    view.loadUrl(url);
                }
            }
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("BBerror ===>", errorCode + " = " + description);
        }
    }

    //서버 처리가 되는 시간 동안 대기해준다.
    public class SleepTask extends AsyncTask<String, Void, String> {
        //처리 대기 시간.
        int sleepTime = 2000;
        String url;
        //결과 값
        String res;
        boolean isFinish = false;
        Context mContext;
        boolean isLoadingImg = true;
        LoadingProgressDialog loadingProgressDialog;


        public SleepTask(Context context) {
            this.mContext = context;
            if (isLoadingImg) {
                loadingProgressDialog = new LoadingProgressDialog(mContext);
                loadingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                loadingProgressDialog.setCancelable(false);
            }

        }

        public void setCallbackEvent(com.sk392.kr.carmony.Library.SendPost.CallbackEvent callbackEvent) {
        }

        public String getUrl() {
            return url;
        }

        public void setRes(String res) {
            this.res = res;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        protected void onPostExecute(String s) {

            if (isLoadingImg)
                if (loadingProgressDialog != null)
                    loadingProgressDialog.dismiss();
            if(isFinish)
                finish();


        }

        public String getRes() {
            return res;
        }


        @Override
        public String doInBackground(String... params) {
            try {
                Thread.sleep(sleepTime);

            } catch (InterruptedException e) {
                e.printStackTrace();

                FirebaseCrash.logcat(Log.ERROR, TAG, "InterruptedException Fail");
                FirebaseCrash.report(e);
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isLoadingImg)
                if (loadingProgressDialog != null)
                    loadingProgressDialog.show();
        }

        private void setIsFinish(boolean isFinish) {
            this.isFinish = isFinish;
        }


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,isDone+"");
        if (isDone) {
            //2초간 대기 후 데이터를 처리하는 부분.
            Intent intent = new Intent();
            intent.putExtra("is_done", isDone);
            intent.putExtra("reservation_id", getIntent().getStringExtra("reservation_id"));
            intent.putExtra("finally_cost", getIntent().getStringExtra("amt"));
            intent.putExtra("origin_cost", getIntent().getStringExtra("origin_cost"));
            intent.putExtra("usercoupon_id", getIntent().getStringExtra("usercoupon_id"));
            intent.putExtra("owner_id", getIntent().getStringExtra("owner_id"));
            intent.putExtra("coupon_id", getIntent().getStringExtra("coupon_id"));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("is_done", isDone);
            intent.putExtra("reservation_id", getIntent().getStringExtra("reservation_id"));
            intent.putExtra("finally_cost", getIntent().getStringExtra("amt"));
            intent.putExtra("origin_cost", getIntent().getStringExtra("origin_cost"));
            intent.putExtra("owner_id", getIntent().getStringExtra("owner_id"));
            intent.putExtra("usercoupon_id", getIntent().getStringExtra("usercoupon_id"));
            intent.putExtra("coupon_id", getIntent().getStringExtra("coupon_id"));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
