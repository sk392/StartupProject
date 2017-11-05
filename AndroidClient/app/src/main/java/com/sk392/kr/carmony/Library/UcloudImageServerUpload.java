package com.sk392.kr.carmony.Library;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by SK392 on 2016-11-15.
 */

public class UcloudImageServerUpload extends AsyncTask<String, Void, String> {
    //
    String url;
    private static final String TAG = "UcloudImageServerUpload";
    //결과 값
    String res;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }

    public String getRes() {
        return res;
    }

    @Override
    public String doInBackground(String... params) {
        DefaultHttpClient hClient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("https://ssproxy.ucloudbiz.olleh.com/auth/v1.0");
        //계정 이메일
        httpget.setHeader("X-Storage-User", "carmony.kr@gmail.com");
        //계정 API
        httpget.setHeader("X-Storage-Pass", "MTQ3MDEyNDIxNzE0NzAxMTk1MDc3Njgw");
        //새로운 토큰 발행
        httpget.setHeader("X-Auth-New", "true");
        HttpResponse response = null;
        StatusLine res_sl = null;
        Header[] res_header = null;
        BufferedReader in = null;
        int returnCode = 0;
        boolean t = true;
        try {
            response = hClient.execute(httpget);
            res_sl = response.getStatusLine();
            returnCode = res_sl.getStatusCode();
            res_header = response.getAllHeaders();
            if (returnCode == 200) {
                for (int i = 0; i < res_header.length; ++i) {
                    if (res_header[i].getName().equals("X-Auth-Token"))
                        System.out.println("token =" +res_header[i].getValue());
                    if (res_header[i].getName().equals("X-Storage-Url"))
                        System.out.println(res_header[i].getValue());
                    if (res_header[i].getName().equals("X-Auth-Token-Expires"))
                        System.out.println("TokenTimer = "+res_header[i].getValue());
                }
            } else {
                t = false;
            }
        } catch (IOException e) {
            e.printStackTrace();

            FirebaseCrash.logcat(Log.ERROR, TAG, "IOException Fail");
            FirebaseCrash.report(e);
        }
        hClient.getConnectionManager().shutdown();
        if(t) return "1";
        else return "2";
    }


}

