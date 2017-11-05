package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;


public class IntroSplashActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    private static final String TAG = "IntroSplashActivity";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    JSONObject jsonResult;
    String userId, userName;
    ImageView ivFlash;
    int flashTime = 2000;// 로고가 보여지는 시간
    boolean isAutoLogin;
    int userPosition = 0;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_splash);
        ivFlash = (ImageView) findViewById(R.id.iv_flash);
        Glide.with(IntroSplashActivity.this).load(R.drawable.flash_img_2)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().into(ivFlash);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getString(getString(R.string.shared_userid), "");
        userName = sharedPreferences.getString(getString(R.string.shared_name), "");
        SendBird.init(getString(R.string.sendbird_app_id), IntroSplashActivity.this);

        isAutoLogin = sharedPreferences.getBoolean(getString(R.string.shared_auto_login), false);
        if (isAutoLogin)
            flashTime = 0;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isAutoLogin) {

                    SendPost sendPost = new SendPost(IntroSplashActivity.this);
                    sendPost.setUrl(getString(R.string.url_set_token_update));
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            String err = "";
                            try {
                                jsonResult = new JSONObject(result);

                                err = jsonResult.getString("err");
                                if (err.equals("0")) {
                                    editor.putString(getString(R.string.shared_token), jsonResult.getString("jwt_sktoken"));
                                    editor.commit();
                                    sendBirdConnect();
                                } else {
                                    if (err.equals("100"))
                                        MainActivity.showToast(getApplicationContext(), "네트워크 연결을 확인해주세요.");
                                    editor.clear();
                                    editor.commit();
                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), IntroTutorialActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);

                            } catch (Exception e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "아몰랑 fail");
                                FirebaseCrash.report(e);

                            }
                        }
                    });
                    sendPost.setLoadingImg(false);
                    sendPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), ""));
                } else {
                    Intent intent = new Intent(IntroSplashActivity.this, IntroTutorialActivity.class);
                    startActivity(intent);
                    // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                    finish();

                }

            }
        }, flashTime);
    }

    private void sendBirdConnect() {

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Intent intent = new Intent(IntroSplashActivity.this, IntroTutorialActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                //
                if (getIntent() != null && getIntent().getStringExtra("is_message") != null && getIntent().getStringExtra("is_message").equals("y")) {

                    GroupChannel.getChannel(getIntent().getStringExtra("channel_url"), new GroupChannel.GroupChannelGetHandler() {
                        @Override
                        public void onResult(GroupChannel groupChannel, SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "FileNotFoundException Fail");
                                FirebaseCrash.report(e);

                                return;
                            }
                            GroupChannel channel = groupChannel;
                            for (int i = 0; i < channel.getMembers().size(); i++) {
                                if (!(channel.getMembers().get(i).getUserId().equals("4") || channel.getMembers().get(i).getUserId().equals(sharedPreferences.getString(getApplicationContext().getString(R.string.shared_userid), "")))) {
                                    //Carmony(4)가 아니고, 나도 아닌 경우.
                                    userPosition = i;
                                }

                            }

                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("is_message","y");
                            intent.putExtra("user_name", channel.getMembers().get(userPosition).getNickname());
                            intent.putExtra("channel_url", channel.getUrl());
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    SendPost sendPost = new SendPost(IntroSplashActivity.this);
                    sendPost.setUrl(getString(R.string.url_get_user_info));
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            try {

                                String err;

                                jsonResult = new JSONObject(result);
                                err = jsonResult.getString("err");
                                if (err.equals("0")) {
//                                    Log.d(TAG, "LoginProcess -11 getuserinfo err =0");

                                    editor.putString(getString(R.string.shared_content), jsonResult.getJSONObject("ret").getString("content"));
                                    editor.putString(getString(R.string.shared_owner_content), jsonResult.getJSONObject("ret").getString("owner_content"));
                                    editor.putString(getString(R.string.shared_isface), jsonResult.getJSONObject("ret").getString("is_face"));
                                    editor.putString(getString(R.string.shared_iscompany), jsonResult.getJSONObject("ret").getString("is_company"));
                                    editor.putString(getString(R.string.shared_isemail), jsonResult.getJSONObject("ret").getString("is_email"));
                                    editor.putString(getString(R.string.shared_islicen), jsonResult.getJSONObject("ret").getString("is_licen"));
                                    editor.putString(getString(R.string.shared_isphone), jsonResult.getJSONObject("ret").getString("is_phone"));
                                    editor.putString(getString(R.string.shared_accident), jsonResult.getJSONObject("ret").getString("accident"));
                                    editor.putString(getString(R.string.shared_user_reviewcnt), jsonResult.getJSONObject("ret").getString("userinfo_review_cnt"));
                                    editor.putFloat(getString(R.string.shared_user_reviewscore), jsonResult.getJSONObject("ret").getLong("userinfo_review_score"));
                                    editor.putString(getString(R.string.shared_owner_reviewcnt), jsonResult.getJSONObject("ret").getString("ow_review_cnt"));
                                    editor.putFloat(getString(R.string.shared_owner_reviewscore), jsonResult.getJSONObject("ret").getLong("ow_review_score"));
                                    editor.putString(getString(R.string.shared_mileage), jsonResult.getJSONObject("ret").getString("mileage"));
                                    editor.putString(getString(R.string.shared_userrescnt), jsonResult.getJSONObject("ret").getString("res_cnt"));
                                    editor.putString(getString(R.string.shared_userphone), jsonResult.getJSONObject("ret").getString("phone"));
                                    editor.putString(getString(R.string.shared_userprofileurl), jsonResult.getJSONObject("ret").getString("userinfo_img_url"));
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (err.equals("100"))
                                        MainActivity.showToast(getApplicationContext(), "네트워크 연결을 확인해주세요.");
                                    else if (err.equals("20"))
                                        MainActivity.showToast(getApplicationContext(), "장기간 미접속으로 로그아웃 되었습니다.");
                                    else
                                        MainActivity.showToast(getApplicationContext(), "서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
                                    editor.clear();
                                    editor.commit();
                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), IntroTutorialActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);


                            }
                        }
                    });
                    sendPost.setLoadingImg(false);
                    sendPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "") +
                                    "&type=" + sharedPreferences.getString(getString(R.string.shared_type), ""));

                }


                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            Intent intent = new Intent(IntroSplashActivity.this, IntroTutorialActivity.class);
                            startActivity(intent);
                            return;
                        }
                    }
                });


            }
        });

    }
}