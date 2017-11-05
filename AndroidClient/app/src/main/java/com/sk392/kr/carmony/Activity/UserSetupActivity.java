package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sk392.kr.carmony.R;

public class UserSetupActivity extends AppCompatActivity implements View.OnClickListener {
    public static int CODE_REGISTLICEN = 10101;
    Toolbar tbUserSetup;
    ImageView ivUserSetup;
    TextView tvToolbar, tvUserSetupName, tvUserSetupEmail, tvUserSetupPhone;
    Button btContactChange, btPasswdChange, btLogout, btUserSetupLicen, btUserSetupCard;
    SharedPreferences sharedPreferences;
    private static final String TAG = "UserSetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);
        bindingXml();
        dataSetup();
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
        tvToolbar.setText("계정 설정");
        if (sharedPreferences.getString(getString(R.string.shared_islicen), "").equals("y")) {
            btUserSetupLicen.setPadding(144, 0, 72, 0);
            btUserSetupLicen.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0);
        } else if (sharedPreferences.getString(getString(R.string.shared_is_licen_edit), "").equals("y")) {
            btUserSetupLicen.setPadding(144, 0, 72, 0);
            btUserSetupLicen.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_horiz_black_24dp, 0);

        } else {
            btUserSetupLicen.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_usersetup_contact_change:
                intent.setClass(getApplicationContext(), UserContactChangeActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_usersetup_passwd_change:
                intent.setClass(getApplicationContext(), UserPasswdChangeActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_usersetup_logout:
                SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
                sharedEditor.clear();
                sharedEditor.commit();
                SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
                    @Override
                    public void onUnregistered(SendBirdException e) {
                        if (e != null) {
                            // Error!
                            e.printStackTrace();
                            return;
                        }


                        SendBird.disconnect(new SendBird.DisconnectHandler() {
                            @Override
                            public void onDisconnected() {
                                Intent intent1 = new Intent();
                                MainActivity.showToast(getApplicationContext(), "로그아웃 되었습니다.");
                                intent1.setClass(getApplicationContext(), IntroLoginActivity.class);
                                startActivity(intent1);
                                ActivityCompat.finishAffinity(UserSetupActivity.this);
                            }
                        });
                    }
                });


                break;

            case R.id.bt_usersetup_regist_card:

                MainActivity.showToast(getApplicationContext(), "곧 출시 예정입니다!");

                break;

            case R.id.bt_usersetup_regist_licen:

                if (sharedPreferences.getString(getString(R.string.shared_islicen), "").equals("y")) {
                    //승인되었을 때,
                    MainActivity.showToast(getApplicationContext(), " 면허증이 승인되었습니다. 대여를 시작해보세요!");
                } else if (sharedPreferences.getString(getString(R.string.shared_islicen), "").equals("n")) {
                    MainActivity.showToast(getApplicationContext(), " 면허증 심사중입니다.");
                }

                intent.setClass(getApplicationContext(), UserRegistLicenActvity.class);
                startActivityForResult(intent,CODE_REGISTLICEN);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==CODE_REGISTLICEN){
            new BottomDialog.Builder(UserSetupActivity.this)
                    .setTitle("승인요청완료")
                    .setContent("면허증 등록요청이 완료되었습니다. 심사에는 약 1일이 소요됩니다. \n 바로 승인을 원할 경우 플러스 친구로 요청해주세요!")
                    .setCancelable(true)
                    .setNegativeText("완료")
                    .setPositiveText("바로승인받기")
                    .setPositiveTextColorResource(R.color.white)
                    .setPositiveBackgroundColorResource(R.color.reddish_orange)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://plus.kakao.com/home/@%EC%B9%B4%EB%AA%A8%EB%8B%88"));
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void bindingXml() {
        tbUserSetup = (Toolbar) findViewById(R.id.introtoolbar);
        tbUserSetup.setTitle("");
        tvToolbar = (TextView) tbUserSetup.findViewById(R.id.tv_toolbar);
        tvUserSetupEmail = (TextView) findViewById(R.id.tv_usersetup_email);
        tvUserSetupName = (TextView) findViewById(R.id.tv_usersetup_name);
        tvUserSetupPhone = (TextView) findViewById(R.id.tv_usersetup_phone);
        ivUserSetup = (ImageView) findViewById(R.id.iv_usersetup_profile);
        btContactChange = (Button) findViewById(R.id.bt_usersetup_contact_change);
        btPasswdChange = (Button) findViewById(R.id.bt_usersetup_passwd_change);
        btUserSetupCard = (Button) findViewById(R.id.bt_usersetup_regist_card);
        btUserSetupLicen = (Button) findViewById(R.id.bt_usersetup_regist_licen);
        btLogout = (Button) findViewById(R.id.bt_usersetup_logout);

        tbUserSetup.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        btContactChange.setOnClickListener(this);
        btUserSetupLicen.setOnClickListener(this);
        btUserSetupCard.setOnClickListener(this);
        btPasswdChange.setOnClickListener(this);
    }

    private void dataSetup() {
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        Glide.with(getApplicationContext()).load(sharedPreferences.getString(getString(R.string.shared_userprofileurl), ""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(ivUserSetup);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        tvUserSetupPhone.setText(sharedPreferences.getString(getString(R.string.shared_userphone), ""));
        tvUserSetupEmail.setText(sharedPreferences.getString(getString(R.string.shared_email), ""));
        tvUserSetupName.setText(sharedPreferences.getString(getString(R.string.shared_name), ""));

    }


}
