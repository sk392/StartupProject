package com.sk392.kr.carmony.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

public class UserEditingContentActivity extends AppCompatActivity {
    TextView tvToolbar,tvEditingIntroCnt;
    Toolbar toolbar;
    EditText etIntro;
    boolean isIntro = false;
    private static final String TAG = "UserEditingContentActivity";

    Button btIntro;
    RelativeLayout rlEditingIntro;
    String err;
    private InputMethodManager imm;
    JSONObject jsonResult;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_editing_content);
        toolbar = (Toolbar)findViewById(R.id.introtoolbar);
        tvToolbar = (TextView)toolbar.findViewById(R.id.tv_toolbar);
        tvEditingIntroCnt = (TextView)findViewById(R.id.tv_editing_intro_cnt);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etIntro = (EditText)findViewById(R.id.et_intro);
        etIntro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvEditingIntroCnt.setText("(" + charSequence.length() + "/80)");
                isIntro = charSequence.length() >=10&& charSequence.length()<=80;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if(getIntent().getStringExtra("content")!=null && getIntent()!=null)
            etIntro.setText(getIntent().getStringExtra("content"));
        etIntro.requestFocus();
        rlEditingIntro = (RelativeLayout)findViewById(R.id.rl_editing_intro);
        rlEditingIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etIntro.getWindowToken(), 0);
            }
        });
        btIntro = (Button)findViewById(R.id.bt_intro);

        btIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isIntro)
                    getJsonResult();
                else
                    MainActivity.showToast(getApplicationContext(),"자기 소개는 10자 이상 80자 이하로 작성해주세요.");
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tvToolbar.setText("자기 소개");
    }
    private void getJsonResult() {
        SendPost sendPost = new SendPost(UserEditingContentActivity.this);

        if(getIntent().getStringExtra("type").equals("1")){
            //유저 인트로
            sendPost.setUrl(getString(R.string.url_set_user_intro));
        }else{
            //오너 인트로
            sendPost.setUrl(getString(R.string.url_set_owner_intro));
        }
        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
            @Override
            public void getResult(String result) {
                try {
                    jsonResult = new JSONObject(result);

                    err = jsonResult.getString("err");
                    if (err.equals("0")) {

                        Intent intent = new Intent();
                        intent.putExtra("intro",etIntro.getText().toString());
                        setResult(RESULT_OK,intent);
                        if(getIntent().getStringExtra("type").equals("1")) {
                            //유저
                            editor.putString(getString(R.string.shared_content), etIntro.getText().toString());
                        }else{
                            editor.putString(getString(R.string.shared_owner_content), etIntro.getText().toString());
                            //오너
                        }
                        editor.commit();
                        MainActivity.showToast(getApplicationContext(),"변경되었습니다.");
                        finish();

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

        Log.d("abcd","sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                +"&content=" + etIntro.getText().toString());
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                +"&content=" + etIntro.getText().toString());
    }
}
