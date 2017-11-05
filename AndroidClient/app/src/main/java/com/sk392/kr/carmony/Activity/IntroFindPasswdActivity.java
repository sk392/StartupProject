package com.sk392.kr.carmony.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntroFindPasswdActivity extends AppCompatActivity {
    Button btFindpasswd;
    private  static final String TAG = "IntroFindPasswdActivity";

    JSONObject jsonResult;
    String strUserEmail;
    //이메일 정규식
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_find_passwd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        toolbar.setTitle("");
        TextView tv = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        tv.setText("비밀번호 찾기");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_big_navy));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btFindpasswd = (Button) findViewById(R.id.bt_find_passwd);
        btFindpasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUserEmail = ((EditText) findViewById(R.id.et_find_passwd)).getText().toString();
                if (validateEmail(strUserEmail)) {
                    SendPost sendPost = new SendPost(IntroFindPasswdActivity.this);
                    sendPost.setUrl(getString(R.string.url_set_new_password));
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            try {

                                String err;

                                jsonResult = new JSONObject(result);
                                err = jsonResult.getString("err");
                                if (err.equals("0")) {
                                    MainActivity.showToast(getApplicationContext(), "이메일로 신규 패스워드가 발송되었습니다.");
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
                    sendPost.execute("user_email=" + strUserEmail);


                } else {
                    MainActivity.showToast(getApplicationContext(), "이메일을 확인해주세요");
                }

            }
        });
    }


    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

}
