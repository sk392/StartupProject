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

/**
 * Created by sk392 on 2016-10-28.
 */

public class SendPost extends AsyncTask<String, Void, String> {
    //
    private static final String TAG = "SendPost";
    int sleepTime = 0;

    private String url;
    //결과 값
    private String res;
    private Context mContext;
    private boolean isLoadingImg = true;
    private CallbackEvent callbackEvent;
    private LoadingProgressDialog loadingProgressDialog;




    public interface CallbackEvent {
        void getResult(String result);
    }

    public SendPost(Context context) {
        this.mContext = context;
        callbackEvent = null;
        if (isLoadingImg) {
            loadingProgressDialog = new LoadingProgressDialog(context);
            loadingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            loadingProgressDialog.setCancelable(false);
        }

    }

    public void setCallbackEvent(CallbackEvent callbackEvent) {
        this.callbackEvent = callbackEvent;
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

        callbackEvent.getResult(s);
        Log.d("SendPost", "Result =" + s);
        if (isLoadingImg)
            if (loadingProgressDialog != null)
                loadingProgressDialog.dismiss();
    }

    public String getRes() {
        return res;
    }


    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String doInBackground(String... params) {
        Log.d("SendPost", "Context = "+mContext.getClass().getName()+" / Request =" + params[0].toString());
        if (NetworkUtil.getConnectivityStatus(mContext) != NetworkUtil.TYPE_NOT_CONNECTED) {
            //네트워크가 연결되었을 경우만.

            try {
                if(sleepTime!=0)
                    Thread.sleep(sleepTime);

                URL obj = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
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

                Thread.sleep(200);

            } catch (ConnectException e) {
                e.printStackTrace();

                FirebaseCrash.logcat(Log.ERROR, TAG, "네트워크 Fail");
                FirebaseCrash.report(e);

            } catch
                    (Exception e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "아몰랑 Fail");
                FirebaseCrash.report(e);
            }
            return res;
        } else {
            //네트워크가 연결되어 있지 않을 때.
            res = "{'err':'100', 'err_result':'네트워크 연결을 확인해주세요.'}";
            return res;
        }


    }

    public boolean isLoadingImg() {
        return isLoadingImg;
    }

    public void setLoadingImg(boolean loadingImg) {
        isLoadingImg = loadingImg;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isLoadingImg)
            if (loadingProgressDialog != null)
                loadingProgressDialog.show();
    }
}