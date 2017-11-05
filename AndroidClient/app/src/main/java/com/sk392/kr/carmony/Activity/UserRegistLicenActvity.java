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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;

import static com.sk392.kr.carmony.Activity.IntroSignupActivity.VALID_LICEN_BIRTH;
import static com.sk392.kr.carmony.Activity.IntroSignupActivity.VALID_LICEN_DATE;
import static com.sk392.kr.carmony.Activity.IntroSignupActivity.VALID_LICEN_NUM;
import static com.sk392.kr.carmony.Activity.IntroSignupActivity.VALID_LICEN_NUM_ONE;
import static com.sk392.kr.carmony.Activity.IntroSignupActivity.VALID_LICEN_NUM_THREE;
import static com.sk392.kr.carmony.Activity.IntroSignupActivity.VALID_LICEN_NUM_TWO;
import static com.sk392.kr.carmony.Activity.IntroSignupActivity.validateNull;

public class UserRegistLicenActvity extends AppCompatActivity implements View.OnClickListener {
    private boolean isRegistLicenLicenNumOne,isRegistLicenLicenName, isRegistLicenLicenNumTwo, isRegistLicenLicenNumThree, isRegistLicenBirth, isRegistLicenLicenNum, isRegistLicenLicenDate, isRegistLicenLicenLoc;
    private EditText etRegistLicenLicenNumOne,etRegistLicenLicenName, etRegistLicenLicenNumTwo, etRegistLicenLicenNumThree, etRegistLicenLicenBirth, etRegistLicenLicenNum, etRegistLicenLicenDate;
    private String strRegistLicenLicenNumOne, strRegistLicenLicenNumTwo, strRegistLicenLicenNumThree, strRegistLicenBirth, strRegistLicenLicenLoc, strRegistLicenLicenNum, strRegistLicenLicenDate,strRegistLicenLicenName;
    private static String TAG = "RegistLicenActivity";
    private RelativeLayout rlRegistLicenMain, rlRegistLicenIntroduce;
    private ShowcaseView showcaseView;
    private int counter = 0;
    private Toolbar toolbar;
    private JSONObject jsonResult;
    private TextView tvToolbar;
    private Button btRegistLicenRegist;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Spinner spLicen;
    private Boolean isIntroDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_regist_licen);
        bindingXml();
        setupData();
    }

    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.view_regist_licen_date)), true);
                showcaseView.setContentTitle("면허증 발급일자");
                showcaseView.setContentText(getString(R.string.hint_licendate));
                break;

            case 1:
                showcaseView.setShowcase(new ViewTarget(findViewById(R.id.view_regist_licen_secret_num)), true);
                showcaseView.setContentTitle("면허증 일련번호");
                showcaseView.setContentText(getString(R.string.hint_licennum));
                showcaseView.setTarget(Target.NONE);
                showcaseView.setButtonText("완료");
                break;
            case 2:
                showcaseView.hide();
                rlRegistLicenIntroduce.setVisibility(View.GONE);
                rlRegistLicenMain.setVisibility(View.VISIBLE);
                isIntroDone = true;
                break;
        }
        counter++;
    }

    private void bindingXml() {
        rlRegistLicenIntroduce = (RelativeLayout) findViewById(R.id.rl_regist_licen_introduce);
        rlRegistLicenMain = (RelativeLayout) findViewById(R.id.rl_regist_licen_main);
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        spLicen = (Spinner) findViewById(R.id.sp_licen);
        etRegistLicenLicenNumOne = (EditText) findViewById(R.id.et_licen_num_one);
        etRegistLicenLicenNumTwo = (EditText) findViewById(R.id.et_licen_num_two);
        etRegistLicenLicenNumThree = (EditText) findViewById(R.id.et_licen_num_three);
        etRegistLicenLicenBirth = (EditText) findViewById(R.id.et_regist_licen_birth);
        etRegistLicenLicenNum = (EditText) findViewById(R.id.et_regist_licen_licen_num);
        etRegistLicenLicenDate = (EditText) findViewById(R.id.et_regist_licen_licen_date);
        btRegistLicenRegist = (Button)findViewById(R.id.bt_regist_licen_regist);
        etRegistLicenLicenName = (EditText)findViewById(R.id.et_regist_licen_name);
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void setupData() {
        showcaseView = new ShowcaseView.Builder(this)
                .setTarget(new ViewTarget(findViewById(R.id.view_regist_licen_num)))
                .setContentTitle("면허증 번호")
                .setContentText("면허증 번호 (지역 + 숫자 10자리)")
                .withMaterialShowcase()
                .setStyle(R.style.showcaseRegistLicenTheme)
                .setOnClickListener(this)
                .build();
        showcaseView.setButtonText("다음");

        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //데이터를 저장했으나 승인이 나기 전.
        //승인이 났다면 여기 들어오지 못한다.
        if(sharedPreferences.getString(getString(R.string.shared_is_licen_edit),"").equals("y")){
            //데이터를 입력해둔 상태라면 결과창에 띄워준다.
            showcaseView.hide();
            isIntroDone = true;
            rlRegistLicenIntroduce.setVisibility(View.GONE);
            rlRegistLicenMain.setVisibility(View.VISIBLE);
            etRegistLicenLicenName.setText(sharedPreferences.getString(getString(R.string.shared_licen_name),""));
            etRegistLicenLicenNum.setText(sharedPreferences.getString(getString(R.string.shared_licen_num),""));
            etRegistLicenLicenNumOne.setText(sharedPreferences.getString(getString(R.string.shared_licen_num_one),""));
            etRegistLicenLicenNumTwo.setText(sharedPreferences.getString(getString(R.string.shared_licen_num_two),""));
            etRegistLicenLicenNumThree.setText(sharedPreferences.getString(getString(R.string.shared_licen_num_three),""));
            etRegistLicenLicenDate.setText(sharedPreferences.getString(getString(R.string.shared_licen_licen_date),""));
            etRegistLicenLicenBirth.setText(sharedPreferences.getString(getString(R.string.shared_licen_birth),""));
            for(int i=0; i<spLicen.getCount();i++){
                if(spLicen.getAdapter().getItem(i).toString().equals(sharedPreferences.getString(getString(R.string.shared_licen_licen_loc),"")))
                    spLicen.setSelection(i);
            }
        }




        tvToolbar.setText("면허증 정보");
        etRegistLicenLicenNumOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 2) {
                    etRegistLicenLicenNumTwo.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etRegistLicenLicenNumTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6) {
                    etRegistLicenLicenNumThree.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etRegistLicenLicenNumThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 2) {
                    etRegistLicenLicenBirth.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etRegistLicenLicenBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 6) {
                    etRegistLicenLicenNum.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ((TextView)findViewById(R.id.tv_regist_licen_hint)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIntroDone=false;
                counter=0;
                showcaseView = new ShowcaseView.Builder(UserRegistLicenActvity.this)
                        .setTarget(new ViewTarget(findViewById(R.id.view_regist_licen_num)))
                        .setContentTitle("면허증 번호")
                        .setContentText("면허증 번호 (지역 + 숫자 10자리)")
                        .withMaterialShowcase()
                        .setStyle(R.style.showcaseRegistLicenTheme)
                        .setOnClickListener(UserRegistLicenActvity.this)
                        .build();
                showcaseView.setButtonText("다음");
                showcaseView.show();

                rlRegistLicenIntroduce.setVisibility(View.VISIBLE);
                rlRegistLicenMain.setVisibility(View.GONE);
            }
        });
        btRegistLicenRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isRegistLicenLicenName = validateNull(etRegistLicenLicenName.getText().toString());
                isRegistLicenLicenLoc = validateNull(spLicen.getSelectedItem().toString());
                isRegistLicenLicenNumOne = validateLicenNumOne(etRegistLicenLicenNumOne.getText().toString());
                isRegistLicenLicenNumTwo = validateLicenNumTwo(etRegistLicenLicenNumTwo.getText().toString());
                isRegistLicenLicenNumThree = validateLicenNumThree(etRegistLicenLicenNumThree.getText().toString());
                isRegistLicenLicenNum = validateLicenNum(etRegistLicenLicenNum.getText().toString());
                isRegistLicenBirth = validateLicenBirth(etRegistLicenLicenBirth.getText().toString());
                isRegistLicenLicenDate = validateLicenDate(etRegistLicenLicenDate.getText().toString());
                if(isRegistLicenLicenName) {
                    if (isRegistLicenLicenLoc && isRegistLicenLicenNumOne && isRegistLicenLicenNumTwo && isRegistLicenLicenNumThree) {
                        if (isRegistLicenLicenNum) {
                            if (isRegistLicenBirth) {
                                if (isRegistLicenLicenDate) {

                                    strRegistLicenLicenNum = etRegistLicenLicenNum.getText().toString();
                                    strRegistLicenLicenNumOne = etRegistLicenLicenNumOne.getText().toString();
                                    strRegistLicenLicenNumTwo = etRegistLicenLicenNumTwo.getText().toString();
                                    strRegistLicenLicenNumThree = etRegistLicenLicenNumThree.getText().toString();
                                    strRegistLicenBirth = etRegistLicenLicenBirth.getText().toString();
                                    strRegistLicenLicenDate = etRegistLicenLicenDate.getText().toString();
                                    strRegistLicenLicenName = etRegistLicenLicenName.getText().toString();
                                    strRegistLicenLicenLoc = spLicen.getSelectedItem().toString();
                                    //데이터를 sharedpreference에 저장한다.
                                    editor.putString(getString(R.string.shared_licen_num), strRegistLicenLicenNum);
                                    editor.putString(getString(R.string.shared_licen_num_one), strRegistLicenLicenNumOne);
                                    editor.putString(getString(R.string.shared_licen_num_two), strRegistLicenLicenNumTwo);
                                    editor.putString(getString(R.string.shared_licen_num_three), strRegistLicenLicenNumThree);
                                    editor.putString(getString(R.string.shared_licen_birth), strRegistLicenBirth);
                                    editor.putString(getString(R.string.shared_licen_licen_date), strRegistLicenLicenDate);
                                    editor.putString(getString(R.string.shared_licen_name), strRegistLicenLicenName);
                                    editor.putString(getString(R.string.shared_licen_licen_loc), strRegistLicenLicenLoc);
                                    editor.putString(getString(R.string.shared_is_licen_edit), "y");
                                    editor.commit();

                                    //데이터를 네트워크에 저장한다.
                                    SendPost sendPost = new SendPost(UserRegistLicenActvity.this);
                                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                                        @Override
                                        public void getResult(String result) {
                                            String err = "";
                                            try {

                                                jsonResult = new JSONObject(result);

                                                err = jsonResult.getString("err");
                                                if (err.equals("0")) {
                                                    setResult(RESULT_OK);
                                                    finish();
                                                } else {
                                                    MainActivity.showToast(getApplicationContext(), "서버에 잠시 접근할 수가.. 없습니다!");
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                                FirebaseCrash.report(e);

                                            }
                                        }
                                    });
                                    sendPost.setUrl(getString(R.string.url_set_regist_licen));
                                    sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "") +
                                            "&licen=" + strRegistLicenLicenNum +
                                            "&licen_name=" + strRegistLicenLicenName +
                                            "&licen_num_one=" + strRegistLicenLicenNumOne +
                                            "&licen_num_two=" + strRegistLicenLicenNumTwo +
                                            "&licen_num_three=" + strRegistLicenLicenNumThree +
                                            "&licen_birth=" + strRegistLicenBirth +
                                            "&licen_date=" + strRegistLicenLicenDate +
                                            "&licen_loc=" + strRegistLicenLicenLoc);


                                } else {
                                    //isSignLicenDate
                                    MainActivity.showToast(getApplicationContext(), "면허증 날짜를 정확히 입력해주세요");

                                }

                            } else {
                                //isSignBirth
                                MainActivity.showToast(getApplicationContext(), "생년월일을 정확히 입력해주세요");
                            }

                        } else {
                            //isSignLicenNum
                            MainActivity.showToast(getApplicationContext(), "면허증 일련번호를 정확히 입력해주세요");

                        }

                    } else {
                        //isSignLicenLoc && isSignLicenNumOne && isSignLicenNumTwo && isSignLicenNumThree
                        MainActivity.showToast(getApplicationContext(), "면허증 지역 및 번호를 확인해주세요");

                    }
                }else{
                    //isSignLicenName
                    MainActivity.showToast(getApplicationContext(), "이름은 영문 혹은 한글로만 작성이 가능합니다");

                }

            }
        });

    }

    public static boolean validateLicenNumOne(String licenNumOneStr) {
        Matcher matcher = VALID_LICEN_NUM_ONE.matcher(licenNumOneStr);
        return matcher.matches();
    }

    public static boolean validateLicenNumTwo(String licenNumTwoStr) {
        Matcher matcher = VALID_LICEN_NUM_TWO.matcher(licenNumTwoStr);
        return matcher.matches();
    }

    public static boolean validateLicenNumThree(String licenNumThreeStr) {
        Matcher matcher = VALID_LICEN_NUM_THREE.matcher(licenNumThreeStr);
        return matcher.matches();
    }

    public static boolean validateLicenNum(String licenNumStr) {
        Matcher matcher = VALID_LICEN_NUM.matcher(licenNumStr);
        return matcher.matches();
    }

    public static boolean validateLicenDate(String licenDateStr) {
        Matcher matcher = VALID_LICEN_DATE.matcher(licenDateStr);
        return matcher.matches();
    }

    public static boolean validateLicenBirth(String licenBirthStr) {
        Matcher matcher = VALID_LICEN_BIRTH.matcher(licenBirthStr);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        if(isIntroDone)
            super.onBackPressed();
    }
}
