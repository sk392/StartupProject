package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sk392.kr.carmony.Library.BackPressClosehHandler;
import com.sk392.kr.carmony.Library.NetworkUtil;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.Library.SendPostHttps;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroLoginActivity extends AppCompatActivity {
    EditText etEmail, etPasswd;
    private static final String TAG = "IntroLoginActivity";
    public static final int REQUEST_LOGIN = 327;
    CheckBox cbAutologin;
    private SessionCallback mKakaocallback;
    Button btLogin;
    TextView tvSignup, tvFindid, tvFindpasswd, tvKakaoLogin, tvFblogin;
    BackPressClosehHandler backPressClosehHandler;
    private CallbackManager callbackManager;
    JSONObject jsonResult;
    String userType;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String userId, userPasswd, userEmail;
    private String kakaoUserId, facebookUserId, userName, kakaoprofileUrl;
    boolean iskakao = false, isSingup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_login);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userType = "0";
        //xml과 클래스 내 객체와 바인딩
        etEmail = (EditText) findViewById(R.id.edit_email);
        etPasswd = (EditText) findViewById(R.id.edit_passwd);
        cbAutologin = (CheckBox) findViewById(R.id.cb_auto_login);
        cbAutologin.setChecked(true);
        btLogin = (Button) findViewById(R.id.bt_login);
        tvFblogin = (TextView) findViewById(R.id.tv_fb_login);
        tvKakaoLogin = (TextView) findViewById(R.id.tv_kakao_login);
        tvSignup = (TextView) findViewById(R.id.tv_signup);
        tvFindid = (TextView) findViewById(R.id.tv_find_id);
        tvFindpasswd = (TextView) findViewById(R.id.tv_find_passwd);
        //facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        //facebook에서 제공하는 로그인 버튼을 사용할 경우. 로그인 버튼에게, 아닌 경우  LoginManager에게 콜백 설정을 해줘야한다.
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {
                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {
                            FirebaseCrash.logcat(Log.ERROR, TAG, "new MeReqeust FacebookException ");
                            FirebaseCrash.report(response.getError().getException());

                        } else {
                            try {
                                facebookUserId = user.getString("id");
                                userName = user.getString("name");
                                if (user.has("email")) {
                                    userEmail = user.getString("email");

                                }
                                userType = "2";
                                setResult(RESULT_OK);
                                iskakao = true;
                                //페이스북 회원가입 여부
                                login(2, facebookUserId);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);

                            } catch (Exception e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "아몰랑 Fail");
                                FirebaseCrash.report(e);

                            }


                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                FirebaseCrash.logcat(Log.ERROR, TAG, "FacebookException error");
                FirebaseCrash.report(error);

                //finish();
            }

            @Override
            public void onCancel() {
                //finish();
            }
        });


        backPressClosehHandler = new BackPressClosehHandler(this);
        /////////////////////////////
        //////////////////////////////
        //sUserId = getPreferences(Context.MODE_PRIVATE).getString("user_id", "");
        //mNickname = getPreferences(Context.MODE_PRIVATE).getString("nickname", "");


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean(getString(R.string.shared_auto_login), cbAutologin.isChecked());
                editor.commit();
                userEmail = etEmail.getText().toString().trim();
                userPasswd = etPasswd.getText().toString().trim();
                login(userEmail, userPasswd);
            }
        });
        tvFblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.getConnectivityStatus(getApplicationContext()) != NetworkUtil.TYPE_NOT_CONNECTED) {
                    //네트워크 연결이 되어있을 경우만
                    editor.putBoolean(getString(R.string.shared_auto_login), cbAutologin.isChecked());
                    editor.commit();
                    //로그인 시도.
                    LoginManager.getInstance().logInWithReadPermissions(IntroLoginActivity.this,
                            Arrays.asList("public_profile", "email"));


                } else {
                    //네트워크 연결이 되어있지 않을 때
                    MainActivity.showToast(getApplicationContext(), "네트워크 연결을 확인해주세요.");

                }

            }
        });

        tvKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //네트워크 연결 체크
                if (NetworkUtil.getConnectivityStatus(getApplicationContext()) != NetworkUtil.TYPE_NOT_CONNECTED) {

                    editor.putBoolean(getString(R.string.shared_auto_login), cbAutologin.isChecked());
                    editor.commit();
                    kakaoSignup();
//                    Log.d(TAG, "LoginProcess-1");

                    //}


                } else {
                    MainActivity.showToast(getApplicationContext(), "네트워크 연결을 확인해주세요.");

                }
            }
        });
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), IntroSignupActivity.class);
                intent.putExtra("login_type", "회원가입");
                intent.putExtra("user_type", userType);
                startActivity(intent);

                editor.putBoolean(getString(R.string.shared_auto_login), cbAutologin.isChecked());
                editor.commit();

            }
        });
        tvFindpasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), IntroFindPasswdActivity.class);
                startActivity(intent);


            }
        });
        tvFindid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IntroFindIdActivity.class);
                startActivity(intent);
            }
        });
        //회원가입 후 첫 로그인 시
        if (getIntent() != null && getIntent().getBooleanExtra("is_signup", false)) {
            isSingup = true;//회원가입 후
            login(getIntent().getStringExtra("user_email"), getIntent().getStringExtra("user_password"));
        }

    }

    private void kakaoSignup() {
        //카카오톡 로그인 일 경우
        // 카카오 세션을 오픈한다
//        Log.d(TAG, "LoginProcess -2");

        mKakaocallback = new IntroLoginActivity.SessionCallback();
        //mKakaocallback.onSessionOpened();
//        Log.d(TAG, "LoginProcess -3");
        Session.getCurrentSession().addCallback(mKakaocallback);
        Session.getCurrentSession().checkAndImplicitOpen();
        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, IntroLoginActivity.this);
    }

    private void sendBirdConnect() {
        //Log.d(TAG,"LoginProcess -12 sendbird connect ");


        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    return;
                }
                //Log.d(TAG,"LoginProcess -14 sendbird connect  nonerr");
                if (FirebaseInstanceId.getInstance().getToken() == null) {
                    FirebaseInstanceId.getInstance();
//                    Log.d(TAG, "LoginProcess -15 firebase token null");
                    return;
                }

                if (isSingup) {
                    SendBird.updateCurrentUserInfo(sharedPreferences.getString(getString(R.string.shared_name), "")
                            , getString(R.string.url_base_profile_url), new SendBird.UserInfoUpdateHandler() {
                                @Override
                                public void onUpdated(SendBirdException e) {
                                    if (e != null) {
                                        // Error.
                                        return;
                                    }
                                }
                            });
                }
                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            return;
                        }
//                        Log.d(TAG, "LoginProcess -18 sendbird token ok");
                    }
                });
                GroupChannelListQuery mQuery;


                mQuery = GroupChannel.createMyGroupChannelListQuery();
                if (mQuery == null || mQuery.isLoading()) {
                    return;
                }

                if (!mQuery.hasNext()) {
                    return;
                }
                mQuery.setIncludeEmpty(true);
                mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
                    @Override
                    public void onResult(final List<GroupChannel> list, SendBirdException e) {
                        if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (list.size() == 0) {
                            //여기서 user_id와 user_name은 차주의 (요청 받는 입장의) 데이터이다.
                            String[] USER_IDS = {"4"};
                            String reservationId = "-";
                            String reservationDate = "-";//reservationDate 값이 숫자가 아니라 - 인경우 카모니와의 대화인 것을 인지하고 채팅방 나가기가 불가능하게 한다.
                            //오너 아이디 | 확정버튼 여부 (1이면 true 0이면 false) | 예약번호
                            String ChannelData = "4" + "|" + "0" + "|" + reservationId;

                            //첫 로그인이라면 카모니와 채팅방 만들기
                            GroupChannel.createChannelWithUserIds(Arrays.asList(USER_IDS), false, reservationDate, null, ChannelData, new GroupChannel.GroupChannelCreateHandler() {
                                @Override
                                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                    if (e != null) {
                                        // Error.
                                        e.printStackTrace();
                                        return;
                                    }
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("owner_id", "4");
                                    map.put("is_check", "0");
                                    map.put("reservation_id", "-");
                                    map.put("is_pay", "0");

                                    // Not Error
                                    groupChannel.createMetaData(map, new BaseChannel.MetaDataHandler() {
                                        @Override
                                        public void onResult(Map<String, String> map, SendBirdException e) {
                                            if (e != null)
                                                e.printStackTrace();

                                        }
                                    });
                                }
                            });

                        }

                    }
                });


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                //Log.d(TAG,"LoginProcess -17 loginSuccess");
                startActivity(intent);
                finish();


            }
        });

    }

    private void loginResult(String err) {
        //로그인 성공
        if (err.equals("0")) {
//            Log.d(TAG, "LoginProcess -8 login err =0");

            try {
                editor.putString(getString(R.string.shared_token), jsonResult.getString("jwt_sktoken"));
                editor.putString(getString(R.string.shared_email), jsonResult.getJSONObject("ret").getString("email"));
                editor.putString(getString(R.string.shared_type), jsonResult.getJSONObject("ret").getString("type"));
                editor.putString(getString(R.string.shared_userid), jsonResult.getJSONObject("ret").getString("userinfo_id"));
                editor.putString(getString(R.string.shared_name), jsonResult.getJSONObject("ret").getString("name"));
                editor.commit();

                userId = sharedPreferences.getString(getString(R.string.shared_userid), "");
                userName = sharedPreferences.getString(getString(R.string.shared_name), "");
                getUserInfo();
            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);
            }
        } else if (userType.equals("1")) {
            //Log.d(TAG,"LoginProcess -9 login err");

            //카카오톡 로그인을 시도했으나 회원가입이 되어있지 않을 경우
            Intent intent = new Intent(IntroLoginActivity.this, IntroSignupActivity.class);
            intent.putExtra("user_type", userType);//카카오톡 로그인
            intent.putExtra("user_name", userName);
            intent.putExtra("kakao_id", kakaoUserId);
            intent.putExtra("login_type", "카카오톡 회원가입");
            intent.putExtra("kakao_profile", kakaoprofileUrl);
            intent.putExtra("user_email", userEmail);
            startActivityForResult(intent, REQUEST_LOGIN);

        } else if (userType.equals("2")) {
            //페이스북 로그인을 시도했으나 회원가입이 되어있지 않을 경우
            Intent intent = new Intent(IntroLoginActivity.this, IntroSignupActivity.class);
            intent.putExtra("user_type", userType);//페이스북 로그인
            intent.putExtra("user_name", userName);
            intent.putExtra("login_type", "페이스북 회원가입");
            intent.putExtra("facebook_id", facebookUserId);
            intent.putExtra("user_email", userEmail);
            startActivityForResult(intent, REQUEST_LOGIN);
        } else if (err.equals("100")) {
            MainActivity.showToast(getApplicationContext(), "네트워크 연결을 확인해주세요.");
        } else {
            MainActivity.showToast(getApplicationContext(), "아이디와 비밀번호를 확인해주세요.");
        }
    }

    private void getUserInfo() {
//        Log.d(TAG, "LoginProcess -10 getuserinfo");
        SendPost sendPost = new SendPost(IntroLoginActivity.this);
        sendPost.setUrl(getString(R.string.url_get_user_info));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {

                    String err;

                    jsonResult = new JSONObject(result);
                    err = jsonResult.getString("err");
                    if (err.equals("0")) {
//                        Log.d(TAG, "LoginProcess -11 getuserinfo err =0");

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


                        sendBirdConnect();
                    } else {
                        MainActivity.showToast(getApplicationContext(), "서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);


                }
            }
        });
        sendPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "") +
                        "&type=" + sharedPreferences.getString(getString(R.string.shared_type), ""));

    }


    @Override
    public void onBackPressed() {
        backPressClosehHandler.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            userType = "1";
//            Log.d(TAG, "LoginProcess -4 on sessionOpened");

            // 사용자 정보를 가져옴, 회원가입 미가입시 자동가입 시킴
            kakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                //Log.d(TAG,"LoginProcess -5 on sessionFailed");

            }
        }


    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    protected void kakaorequestMe() {

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
//                Log.d(TAG, "LoginProcess -6 on requestFailed");

                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;
                if (ErrorCode == ClientErrorCode) {
                    MainActivity.showToast(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 잠시 후 다시 시도해주세요.");
                }
            }


            //
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                errorResult.getErrorCode();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
//                Log.d(TAG, "LoginProcess -7 on requestonSuccess");

                kakaoprofileUrl = userProfile.getProfileImagePath();
                kakaoUserId = String.valueOf(userProfile.getId());
                userName = userProfile.getNickname();
                if (!iskakao) {
                    iskakao = true;
                    login(1, kakaoUserId);

                }

            }

            @Override
            public void onNotSignedUp() {
                // 자동가입이 아닐경우 동의창
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        iskakao = false;
    }

    private void login(String email, String passwd) {
        if (email.isEmpty()) {
            MainActivity.showToast(getApplicationContext(), "아이디를 입력해주세요.");
        } else if (passwd.isEmpty()) {
            MainActivity.showToast(getApplicationContext(), "비밀번호를 입력해주세요.");
        } else {

            SendPostHttps sendPostHttps = new SendPostHttps(IntroLoginActivity.this,
                    new SendPostHttps.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            String err = "";
                            try {
                                jsonResult = new JSONObject(result);

                                err = jsonResult.getString("err");
                                loginResult(err);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);

                            }
                        }
                    });
            sendPostHttps.setUrl(getString(R.string.url_auth));

            sendPostHttps.execute("email=" + email +
                    "&passwd=" + passwd
            );
            //MainActivity.showToast(getApplicationContext(), "id : " + USER_ID + " | passwd : " + editPasswd.getText().toString().trim() + " | auto login : " + cbAutologin.isChecked());

        }
    }

    private void login(int type, String UserId) {
        SendPostHttps sendPostHttps = new SendPostHttps(IntroLoginActivity.this,
                new SendPostHttps.CallbackEvent() {
                    @Override
                    public void getResult(String result) {
                        String err = "";
                        try {

                            jsonResult = new JSONObject(result);

                            err = jsonResult.getString("err");
                            loginResult(err);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                            FirebaseCrash.report(e);

                        }
                    }
                });
        if (type == 1) {
            //카카오톡 로그인

            sendPostHttps.setUrl(getString(R.string.url_auth_kakao));

            sendPostHttps.execute("kakao_id=" + kakaoUserId);

        } else if (type == 2) {
            //페이스북 로그인


            sendPostHttps.setUrl(getString(R.string.url_auth_facebook));

            sendPostHttps.execute("facebook_id=" + facebookUserId);
            LoginManager.getInstance().logOut();

        }
        //MainActivity.showToast(getApplicationContext(), "id : " + USER_ID + " | passwd : " + editPasswd.getText().toString().trim() + " | auto login : " + cbAutologin.isChecked());

    }

}
