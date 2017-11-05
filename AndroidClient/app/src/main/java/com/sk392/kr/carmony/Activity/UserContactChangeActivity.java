package com.sk392.kr.carmony.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static com.sk392.kr.carmony.Activity.IntroSignupActivity.validatePhone;

public class UserContactChangeActivity extends AppCompatActivity {
    Toolbar tbUserSetup;
    TextView tvToolbar;
    EditText etCurrentContact,etChangeContactAuthCheck,etAfterContact;
    SharedPreferences sharedPreferences;
    private static final String TAG = "UserContactChangeActivity";
    RelativeLayout rlContactAuth;
    String strContactPhone;
    boolean isContact,isContactRe;
    int authNum;
    String err;
    SharedPreferences.Editor editor;
    Button btChangeContact,btChangeContactAuth,btChangeContactAuthCheck;
    JSONObject jsonResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_user_contact_change);
        tbUserSetup = (Toolbar)findViewById(R.id.introtoolbar);
        tbUserSetup.setTitle("");
        tvToolbar = (TextView)tbUserSetup.findViewById(R.id.tv_toolbar);
        etChangeContactAuthCheck = (EditText)findViewById(R.id.et_change_contact_auth_check);
        btChangeContactAuthCheck = (Button)findViewById(R.id.bt_change_contact_auth_check);

        etCurrentContact=(EditText) findViewById(R.id.et_change_current_contact);
        etCurrentContact.requestFocus();
        etAfterContact=(EditText) findViewById(R.id.et_change_after_contact);
        rlContactAuth = (RelativeLayout)findViewById(R.id.rl_change_contact_auth);
        btChangeContact = (Button)findViewById(R.id.bt_change_contact);
        btChangeContactAuth = (Button)findViewById(R.id.bt_change_contact_auth);
        btChangeContactAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePhone(etAfterContact.getText().toString())) {
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(700);
                    etChangeContactAuthCheck.setClickable(true);
                    etAfterContact.setFocusable(false);
                    etChangeContactAuthCheck.setFocusable(true);
                    btChangeContactAuthCheck.setEnabled(true);
                    rlContactAuth.setAnimation(animation);
                    rlContactAuth.setVisibility(View.VISIBLE);
                    strContactPhone = etAfterContact.getText().toString();
                    SendPost sendPost = new SendPost(UserContactChangeActivity.this);
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            String err = "";
                            try {

                                jsonResult = new JSONObject(result);

                                err = jsonResult.getString("err");
                                if (err.equals("0")) {
                                    MainActivity.showToast(getApplicationContext(), "문자를 발송했습니다.");
                                } else {
                                    MainActivity.showToast(getApplicationContext(), "서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
                                }
                            } catch (JSONException e) {
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);
                                e.printStackTrace();
                            }
                        }
                    });
                    sendPost.setUrl(getString(R.string.url_get_sign_auth_num));
                    Random random = new Random();
                    authNum = (int) (random.nextFloat() * 1000000);
                    sendPost.execute("auth_num=" + authNum +
                            "&user_phone=" + strContactPhone);

                } else {
                    MainActivity.showToast(getApplicationContext(), "전화번호를 확인해주세요!");
                }
            }
        });
        etAfterContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==11) {
                    //이전도 입력했다면
                    if(etCurrentContact.getText().toString().length()==11){
                        btChangeContactAuth.setEnabled(true);
                        //연락처가 맞다면.
                        isContact = true;
                    }else{
                        btChangeContactAuth.setEnabled(false);
                    }
                }
                else {
                    btChangeContactAuth.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btChangeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJsonResult();
            }
        });
        btChangeContactAuthCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etChangeContactAuthCheck.getText().toString().equals(authNum + "")) {
                    //인증번호가 유효할 경우
                    MainActivity.showToast(getApplicationContext(), "인증되었습니다.");
                    //변경되었던 연락처가 맞다면
                    isContactRe = true;
                    etAfterContact.setClickable(false);
                    etChangeContactAuthCheck.setClickable(false);
                    etAfterContact.setFocusable(false);
                    etChangeContactAuthCheck.setFocusable(false);
                    btChangeContactAuthCheck.setEnabled(false);
                    btChangeContact.setEnabled(true);
                    etAfterContact.setText(strContactPhone);

                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(700);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            Log.d("debug123", "start");

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Log.d("debug123", "end");
                            rlContactAuth.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            Log.d("debug123", "repeat");

                        }
                    });
                    rlContactAuth.setAnimation(animation);
                    rlContactAuth.setVisibility(View.INVISIBLE);
                } else {
                    MainActivity.showToast(getApplicationContext(), "인증번호를 확인해주세요");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        tbUserSetup.setNavigationIcon(R.drawable.arrow_big_navy);
        tbUserSetup.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tvToolbar.setText("연락처 변경");
    }
    private void getJsonResult() {
        SendPost sendPost = new SendPost(UserContactChangeActivity.this);
        sendPost.setUrl(getString(R.string.url_set_user_contact));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    jsonResult = new JSONObject(result);

                    err = jsonResult.getString("err");
                    if (err.equals("0")) {
                        finish();
                        editor.putString(getString(R.string.shared_userphone),etAfterContact.getText().toString());
                        editor.commit();
                        MainActivity.showToast(getApplicationContext(),"연락처가 변경되었습니다.");
                    }else{

                        MainActivity.showToast(getApplicationContext(),"연락처가 일치하지 않습니다.");
                    }

                } catch (JSONException e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);
                    e.printStackTrace();
                }

            }
        });

        Log.d("abcd","sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                +"&contact="+ etCurrentContact.getText().toString()
                +"&new_contact=" + etAfterContact.getText().toString());
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                +"&contact="+ etCurrentContact.getText().toString()
                +"&new_contact=" + etAfterContact.getText().toString());
    }


}
