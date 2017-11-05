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

public class IntroFindIdActivity extends AppCompatActivity {
    Button btFindid;
    private static final  String TAG = "IntroFindIdActivity";
    String userPhone;
    EditText etFindid;
    JSONObject jsonResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_find_id);
        Toolbar toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        toolbar.setTitle("");
        TextView tv = (TextView)toolbar.findViewById(R.id.tv_toolbar);
        tv.setText("아이디 찾기");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_big_navy));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etFindid = (EditText)findViewById(R.id.et_find_id);

        btFindid = (Button)findViewById(R.id.bt_find_id);
        btFindid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etFindid.getText().toString().length()==11){
                    userPhone = etFindid.getText().toString();
                    SendPost sendPost = new SendPost(IntroFindIdActivity.this);
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            String err = "";
                            try {

                                jsonResult = new JSONObject(result);

                                err = jsonResult.getString("err");
                                if(err.equals("0")){
                                    MainActivity.showToast(getApplicationContext(),"문자를 발송했습니다.");
                                }else{
                                    MainActivity.showToast(getApplicationContext(),"서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);

                            }
                        }
                    });
                    sendPost.setUrl(getString(R.string.url_get_user_email));
                    sendPost.execute("user_phone=" + userPhone);

                }else{
                    MainActivity.showToast(getApplicationContext(),"휴대폰 번호를 확인해주세요");
                }
            }
        });
    }
}
