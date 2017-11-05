package com.sk392.kr.carmony.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sk392.kr.carmony.Activity.IntroSignupActivity.validatePassword;

public class UserPasswdChangeActivity extends AppCompatActivity {
    Toolbar tbUserSetup;
    TextView tvToolbar;
    private static final String TAG = "UserPasswdChangeActivity";

    EditText etCurrentPasswd, etAfterPasswd, etAfterRePasswd;
    SharedPreferences sharedPreferences;
    String err;

    Button btChangePasswd;
    JSONObject jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        setContentView(R.layout.activity_user_passwd_change);
        tbUserSetup = (Toolbar) findViewById(R.id.introtoolbar);
        tbUserSetup.setTitle("");
        tvToolbar = (TextView) tbUserSetup.findViewById(R.id.tv_toolbar);
        btChangePasswd = (Button) findViewById(R.id.bt_change_passwd);

        etCurrentPasswd = (EditText) findViewById(R.id.et_change_current_pass);
        etAfterPasswd = (EditText) findViewById(R.id.et_change_after_pass);
        etAfterRePasswd = (EditText) findViewById(R.id.et_change_after_re_pass);
        etAfterPasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btChangePasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAfterPasswd.getText().toString().equals(etAfterRePasswd.getText().toString())) {
                    if (validatePassword(etAfterPasswd.getText().toString()))
                        getJsonResult();
                    else
                        MainActivity.showToast(getApplicationContext(), "입력한 비밀번호를 확인해주세요.");


                } else {
                    MainActivity.showToast(getApplicationContext(), "입력한 비밀번호가 일치하지 않습니다.");
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
        tvToolbar.setText("비밀번호 변경");
    }

    private void getJsonResult() {
        SendPost sendPost = new SendPost(UserPasswdChangeActivity.this);
        sendPost.setUrl(getString(R.string.url_set_user_passwd));
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    jsonResult = new JSONObject(result);

                    err = jsonResult.getString("err");
                    if (err.equals("0")) {
                        finish();
                        MainActivity.showToast(getApplicationContext(), "비밀번호가 변경되었습니다.");
                    } else {
                        MainActivity.showToast(getApplicationContext(), "비밀번호가 일치하지 않습니다.");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                    FirebaseCrash.report(e);
                }

            }
        });

        Log.d("abcd", "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&passwd=" + etCurrentPasswd.getText().toString()
                + "&new_passwd=" + etAfterPasswd.getText().toString());
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&passwd=" + etCurrentPasswd.getText().toString()
                + "&new_passwd=" + etAfterPasswd.getText().toString());
    }
}
