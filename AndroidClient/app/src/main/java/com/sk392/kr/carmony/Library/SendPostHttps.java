package com.sk392.kr.carmony.Library;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Dialog.LoadingProgressDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sk392 on 2016-10-28.
 */

public class SendPostHttps extends AsyncTask<String, Void, String> {
    private static final String TAG = "SendPostHttps";
    String url = "";
    //결과 값
    String res = "";
    Context mContext;
    private CallbackEvent callbackEvent;
    LoadingProgressDialog loadingProgressDialog;

    public SendPostHttps(Context context, CallbackEvent callbackEvent1) {
        this.mContext = context;
        loadingProgressDialog = new LoadingProgressDialog(context);
        // 위에서 테두리를 둥글게 했지만 다이얼로그 자체가 네모라 사각형 여백이 보입니다. 아래 코드로 다이얼로그 배경을 투명처리합니다.
        loadingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingProgressDialog.setCancelable(false);
        callbackEvent = callbackEvent1;
    }

    public interface CallbackEvent {
        void getResult(String result);
    }

    public void setCallbackEvent(CallbackEvent callbackEvent) {
        this.callbackEvent = callbackEvent;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getRes() {
        return res;
    }

    @Override
    public String doInBackground(String... params) {
        Log.d("SendPostHttps", "Request =" + params);
        if (NetworkUtil.getConnectivityStatus(mContext) != NetworkUtil.TYPE_NOT_CONNECTED) {
            //네트워크가 연결되었을 경우만.

            try {
                URL obj = new URL(url);


                trustAllHosts();

                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) obj.openConnection();
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });

                HttpURLConnection conn = httpsURLConnection;


                //read시 연결 시간 timeout시간
                conn.setReadTimeout(10000);
                //서버 접속 시 연결시간의 timeout
                conn.setConnectTimeout(20000);
                //요청 방식 선택(get,post) get기본
                conn.setRequestMethod("POST");
                //inputstream으로 서버로 부터 응답으로 받겠다는 옵션.
                conn.setDoInput(true);
                //outputstream으로 post 데이터를 넘겨주겠다는 옵션
                conn.setDoOutput(true);
                // 서버 Response Data를 JSON 형식의 타입으로 요청.
                conn.setRequestProperty("Accept", "application/json");
                //웹서버 형식으로 전송
                conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");

                byte[] outputInBytes = params[0].getBytes("UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(outputInBytes);
                os.flush();
                os.close();
                //서버에 성공적으로 접근했는지를 체크(기능의 성공[ex 로그인]과는 관계가 없다.
                int retCode = conn.getResponseCode();
                if (retCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    res = response.toString();

                    br.close();
                    is.close();
                } else {
                    res = "ErrorCode : " + retCode;
                }
                conn.disconnect();
                httpsURLConnection.disconnect();

                Thread.sleep(200);

            } catch (ConnectException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "네트워크 Fail");
                FirebaseCrash.report(e);
            } catch (Exception e) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "아몰랑 Fail");
                FirebaseCrash.report(e);
                e.printStackTrace();
            }
            return res;
        } else {
            //네트워크가 연결되어 있지 않을 때.
            res = "{'err':'100', 'err_result':'네트워크 연결을 확인해주세요.'}";
            return res;
        }
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "아멀랑 Fail");
            FirebaseCrash.report(e);

            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

       /* Animation anim = AnimationUtils.loadAnimation(
                context, // 현재 화면의 제어권자
                m);    // 설정한 에니메이션 파일
        anim.setDuration(1000);
        anim.setInterpolator(new Interpolator() {
            //1초에 재생되는 프레임 수 크면 클수록 자연스럽다(30까지)
            private final int frameCount = 30;

            @Override
            public float getInterpolation(float input) {
                return (float)Math.floor(input*frameCount)/frameCount;
            }
        });
        ivProgress.startAnimation(anim);

        ivProgress.setVisibility(View.VISIBLE);*/
        super.onPreExecute();
        if (loadingProgressDialog != null) {
            loadingProgressDialog.show();
        }
    }

    //doinBackground에서 나오는 리턴 값이 여기로 들어간다.
    @Override
    protected void onPostExecute(String s) {
        if (loadingProgressDialog != null) {
            loadingProgressDialog.dismiss();
        }
        Log.d("SendPostHttps", "Result =" + s);

        callbackEvent.getResult(s);
    }

}