package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.NonSwipeViewPager;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.Library.SendPostHttps;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntroSignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    NonSwipeViewPager viewpager;
    RelativeLayout rlPerAgree, rlPerCarmonyAgree;
    JSONObject jsonResult;
    AlertDialog.Builder abPerAgree, abPerCarmonyAgree;
    AlertDialog adPerAgree, adPerCarmonyAgree;
    Button btSignNext, btSignAuth, btSignAuthCheck;
    Toolbar toolbar;
    private static final String TAG = "IntroSignupActivity";
    boolean isSignEmail, isSignCbPer, isSignCbPerCarmony, isSignCbPush, isSignName, isSignPhone, isSignPasswd, isSignRePasswd, isSignPath, isSignRecom;
    SignupAdapter adapter;
    int authNum;


    /* 정규식 예제
    * 1) ^ : 문자열의 시작
    2) $ : 문자열의 종료
    3) . : 임의의 한 문자 (문자의 종류와 관계없음)
    4) | : or의 의미임
    5) ? : 앞 문자가 없거나 하나 있을때
    6) + : 앞 문자가 하나 이상임 (최소 한개 이상)
    7) * : 앞 문자가 없을 수도 있고, 무한정 많을 수도 있음
    8) [] : 문자 클래스의 지정, 문자의 집합이나 범위를 나태내면 - 기호를 사용한다. [] 내에서 ^를 사용하면 not의 의미임
    9) {} : 횟수나 범위를 나타냄. 예를 들어 k{5}의 경우 k가 5번 반복되는 경우임, a{3,5}는 a가 3번 이상 5번 이하 반복되는 경우임
    10) \w : 알파벳이나 숫자
    11) \W : \w의 not. 즉 알파벳이나 숫자를 제외한 문자
    12) \d : [0-9]와 동일
    13) \D : 숫자를 제외한 모든 문자

    기본적인 정규 표현식과 그 예를 모아보면 다음과 같습니다.

    1) 숫자만 : ^[0-9]*$
    2) 영문자만 : ^[a-zA-Z]*$
    3) 한글만 : ^[가-힣]*$
    4) 영어 & 숫자만 : ^[a-zA-Z0-9]*$
    5) E-Mail : ^[a-zA-Z0-9]+@[a-zA-Z0-9]+$
    6) 휴대폰 : ^01(?:0|1|[6-9]) - (?:\d{3}|\d{4}) - \d{4}$
    7) 일반전화 : ^\d{2.3} - \d{3,4} - \d{4}$
    8) 주민등록번호 : \d{6} \- [1-4]\d{6}
    9) IP 주소 : ([0-9]{1,3}) \. ([0-9]{1,3}) \. ([0-9]{1,3}) \. ([0-9]{1,3})*/

    //^(?=.*\d)(?=.*[~`!@#$%\^&*()-])(?=.*[a-zA-Z]).{8,20}$ : 8~20자 특수문자 및 영어 및 숫자 필참
    //비밀번호 정규식
    public static final Pattern VALID_PASSWORD_REGEX_ALPHA_NUM = Pattern.compile("^(?=.*\\d)(?=.*[a-zA-Z]).{8,20}$"); // 8자리 ~ 20자리까지 가능 숫자 + 영어 필수

    //이름 정규식
    public static final Pattern VALID_NAME = Pattern.compile("^[가-힣]{2,5}|[a-zA-Z]{2,20}|[a-zA-Z]{2,10}\\s[a-zA-Z]{2,10}$"); // 한글 2~5자리, 영어 2~10 스페이스바 2~10자리
    //전화번호 정규식
    public static final Pattern VALID_PHONE_NUM = Pattern.compile("^[0-9]{11}$"); // 11자리 가능
    public static final Pattern VALID_LICEN_NUM_ONE = Pattern.compile("^[0-9]{2}$"); // 2자리 가능
    public static final Pattern VALID_LICEN_NUM_TWO = Pattern.compile("^[0-9]{6}$"); // 6자리 가능
    public static final Pattern VALID_LICEN_BIRTH = Pattern.compile("^[0-9]{6}$"); // 6자리 가능
    public static final Pattern VALID_LICEN_NUM = Pattern.compile("^[a-zA-Z0-9]{4,7}$"); // 4자리 ~ 7자리까지 가능
    public static final Pattern VALID_LICEN_DATE = Pattern.compile("^[0-9]{8}$"); // 2자리 ~ 5자리까지 가능
    public static final Pattern VALID_LICEN_NUM_THREE = Pattern.compile("^[0-9]{2}$"); // 2자리 ~ 5자리까지 가능
    //이메일 정규식
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    RelativeLayout rlSignAuth;
    View viewOne, viewtwo;
    EditText etSignEmail, etSignAuthNum, etSignName, etSignPhone, etSignPasswd, etSignRePasswd, etSignPath, etSignRecom;
    String strKakaoId = "-", strFacebookId = "-", strKakaoProfile, strSignEmail, strSignName, strSignPhone, strSignPasswd, strSignPath, strSignRecom;
    CheckBox cbPerAgree, cbPerCarmonyAgree, cbPushAgree;
    Spinner spSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro_signup);
        //등록된 액션바에게 옵션 부여.
        bindingXml();

        //뷰페이저 이후에 있어야 이놈들이 인식하나보다
        btSignNext.setOnClickListener(this);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    //페이지 변경 시 레이아웃 수정
                    case 0:
                        break;
                    case 1:
                        btSignNext.setText("가입 신청하기");
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void viewPagerSetup() {
        adapter = new SignupAdapter();

        viewOne = getLayoutInflater().inflate(R.layout.view_signup_one, null);

        adapter.addView(viewOne);
        viewtwo = getLayoutInflater().inflate(R.layout.view_signup_two, null);

        adapter.addView(viewtwo);
        viewpager.setAdapter(adapter);

    }

    public class SignupAdapter extends PagerAdapter {

        private final List<View> mViews = new ArrayList<>();

        @Override
        public int getCount() {

            return mViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;

            switch (position) {
                case 0:
                    //첫번째 뷰
                    view = mViews.get(0);

                    etSignEmail = (EditText) view.findViewById(R.id.et_sign_email);

                    etSignName = (EditText) view.findViewById(R.id.et_sign_name);
                    etSignAuthNum = (EditText) view.findViewById(R.id.et_sign_auth_check);
                    etSignPhone = (EditText) view.findViewById(R.id.et_sign_phone);
                    etSignPasswd = (EditText) view.findViewById(R.id.et_sign_passwd);
                    etSignRePasswd = (EditText) view.findViewById(R.id.et_sign_re_passwd);
                    btSignAuth = (Button) view.findViewById(R.id.bt_sign_auth);
                    btSignAuthCheck = (Button) view.findViewById(R.id.bt_sign_auth_check);
                    rlSignAuth = (RelativeLayout) view.findViewById(R.id.ll_sign_auth);
                    btSignAuth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (validatePhone(etSignPhone.getText().toString())) {
                                Animation animation = new AlphaAnimation(0, 1);
                                animation.setDuration(700);
                                rlSignAuth.setAnimation(animation);
                                rlSignAuth.setVisibility(View.VISIBLE);
                                strSignPhone = etSignPhone.getText().toString();
                                SendPost sendPost = new SendPost(IntroSignupActivity.this);
                                sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                                    @Override
                                    public void getResult(String result) {
                                        String err = "";
                                        try {

                                            jsonResult = new JSONObject(result);

                                            err = jsonResult.getString("err");
                                            if (err.equals("0")) {
                                                MainActivity.showToast(getApplicationContext(), "인증번호를 발송했습니다.");
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
                                sendPost.setUrl(getString(R.string.url_get_sign_auth_num));
                                Random random = new Random();

                                authNum = (int) (random.nextFloat() * 1000000);
                                sendPost.execute("auth_num=" + authNum +
                                        "&user_phone=" + strSignPhone);
                            } else {
                                MainActivity.showToast(getApplicationContext(), "전화번호를 확인해주세요!");
                            }
                        }
                    });
                    btSignAuthCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (etSignAuthNum.getText().toString().equals(authNum + "")) {
                                //인증번호가 유효할 경우
                                MainActivity.showToast(getApplicationContext(), "인증되었습니다.");
                                isSignPhone = true;
                                etSignPhone.setClickable(false);
                                etSignPhone.setFocusable(false);
                                btSignAuth.setClickable(false);
                                etSignPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                                etSignPhone.setText(strSignPhone);
                                btSignAuthCheck.setClickable(false);

                                Animation animation = new AlphaAnimation(1, 0);
                                animation.setDuration(700);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        rlSignAuth.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                rlSignAuth.setAnimation(animation);
                                rlSignAuth.setVisibility(View.INVISIBLE);
                            } else {
                                MainActivity.showToast(getApplicationContext(), "인증번호를 확인해주세요");
                            }

                        }
                    });
                    if (getIntent() != null && !(getIntent().getStringExtra("user_type").equals(""))) {
                        if (getIntent().getStringExtra("user_type").equals("1")) {
                            etSignName.setText(getIntent().getStringExtra("user_name"));
                            MainActivity.showLongToast(IntroSignupActivity.this, "이메일은 인증에 사용되므로 다시 확인해주세요");
                            etSignPhone.requestFocus();
                            strKakaoId = getIntent().getStringExtra("kakao_id");
                            strKakaoProfile = getIntent().getStringExtra("kakao_profile");

                        } else if (getIntent().getStringExtra("user_type").equals("2")) {
                            //페이스북 로그인 일 경우
                            etSignName.setText(getIntent().getStringExtra("user_name"));
                            MainActivity.showLongToast(IntroSignupActivity.this, "이메일은 인증에 사용되므로 다시 확인해주세요");
                            etSignEmail.setText(getIntent().getStringExtra("user_email"));
                            etSignPhone.requestFocus();
                            strFacebookId = getIntent().getStringExtra("facebook_id");
                        }
                    }

                    break;

                case 1:
                    //두번째 뷰
                    view = mViews.get(1);
                    rlPerAgree = (RelativeLayout) view.findViewById(R.id.rl_per_agree);
                    rlPerCarmonyAgree = (RelativeLayout) view.findViewById(R.id.rl_per_carmony_agree);
                    spSign = (Spinner) view.findViewById(R.id.sp_sign);
                    cbPerAgree = (CheckBox) view.findViewById(R.id.cb_per_agree);
                    cbPerCarmonyAgree = (CheckBox) view.findViewById(R.id.cb_per_carmony_agree);
                    cbPushAgree = (CheckBox) view.findViewById(R.id.cb_push_agree);
                    etSignPath = (EditText) view.findViewById(R.id.et_sign_path);
                    editTextSetup();
                    spSign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i == 0) {
                                if (!etSignPath.getText().toString().trim().equals("")) {
                                    spSign.setSelection(getIndex(spSign, etSignPath.getText().toString()));
                                }
                            } else {
                                etSignPath.setText(adapterView.getItemAtPosition(i) + "");
                                etSignPath.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                                isSignPath = true;
                            }


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                    rlPerAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            abPerAgree = new AlertDialog.Builder(IntroSignupActivity.this);
                            View mView;
                            mView = getLayoutInflater().inflate(R.layout.dialog_singup_rule, null, false);
                            Button btClose;
                            btClose = (Button) mView.findViewById(R.id.bt_alert_signup_rule_close);
                            btClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cbPerAgree.setChecked(true);
                                    adPerAgree.dismiss();
                                }
                            });
                            abPerAgree.setView(mView);
                            adPerAgree = abPerAgree.create();
                            adPerAgree.show();
                        }
                    });
                    rlPerCarmonyAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            abPerCarmonyAgree = new AlertDialog.Builder(IntroSignupActivity.this);
                            View mView;
                            mView = getLayoutInflater().inflate(R.layout.dialog_singup_carmony_rule, null, false);
                            Button btClose;
                            btClose = (Button) mView.findViewById(R.id.bt_alert_signup_carmony_rule_close);
                            btClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cbPerCarmonyAgree.setChecked(true);
                                    adPerCarmonyAgree.dismiss();
                                }
                            });
                            abPerCarmonyAgree.setView(mView);
                            adPerCarmonyAgree = abPerCarmonyAgree.create();
                            adPerCarmonyAgree.show();
                        }
                    });


                    etSignRecom = (EditText) view.findViewById(R.id.et_sign_recom);
                    break;

                default:
                    //Log.d(TAG,"IntroSignupActivityViewpager default");
                    break;

            }

            container.addView(view);

            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub

            container.removeView((View) object);

        }

        public void addView(View view) {
            mViews.add(view);
            notifyDataSetChanged();
        }

        //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
        @Override
        public boolean isViewFromObject(View v, Object obj) {
            // TODO Auto-generated method stub
            return v == obj;
        }

    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void bindingXml() {

        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        toolbar.setTitle("");
        ((TextView) toolbar.findViewById(R.id.tv_toolbar)).setText(getIntent().getStringExtra("login_type"));

        viewpager = (NonSwipeViewPager) findViewById(R.id.signuppager);
        //첫번째 뷰는 자동으로 추가.
        viewPagerSetup();

        btSignNext = (Button) findViewById(R.id.bt_sign_next);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_big_navy));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sign_next:
                int position;
                position = viewpager.getCurrentItem();
                //계속 가입하기 버튼.
                switch (viewpager.getCurrentItem()) {
                    case 0:
                        isSignEmail = validateEmail(etSignEmail.getText().toString());
                        isSignPasswd = validatePassword(etSignPasswd.getText().toString());
                        isSignName = validateName(etSignName.getText().toString());
                        isSignRePasswd = etSignRePasswd.getText().toString().equals(etSignPasswd.getText().toString());

                        if (isSignEmail) {
                            if (isSignName) {
                                if (isSignPhone) {
                                    if (isSignRePasswd) {
                                        if (isSignPasswd) {

                                            strSignEmail = etSignEmail.getText().toString();
                                            strSignName = etSignName.getText().toString();
                                            strSignPhone = etSignPhone.getText().toString();
                                            strSignPasswd = etSignPasswd.getText().toString();
                                            viewpager.setCurrentItem(position + 1);

                                        } else {
                                            //isSingPasswd
                                            MainActivity.showToast(getApplicationContext(), "비밀번호 양식을 확인해주세요");
                                        }

                                    } else {
                                        //isSingREPasswd
                                        MainActivity.showToast(getApplicationContext(), "비밀번호가 일치하지 않아요");

                                    }

                                } else {
                                    //isSingPhone
                                    MainActivity.showToast(getApplicationContext(), "휴대폰번호를 인증해주세요");

                                }

                            } else {
                                //isSingName
                                MainActivity.showToast(getApplicationContext(), "닉네임은 영어 혹은 한글로만 작성이 가능합니다");

                            }

                        } else {
                            //isSingEmail
                            MainActivity.showToast(getApplicationContext(), "이메일 양식을 확인해주세요");

                        }

                        break;
                    case 1:
                        //두번째 뷰에서 가입하기.
                        isSignRecom = validateNull(etSignRecom.getText().toString());
                        isSignCbPer = cbPerAgree.isChecked();
                        isSignCbPerCarmony = cbPerCarmonyAgree.isChecked();
                        isSignCbPush = cbPushAgree.isChecked();

                        if (isSignPath) {
                            if (isSignCbPer) {
                                if (isSignCbPerCarmony) {


                                    strSignPath = etSignPath.getText().toString();
                                    if (isSignRecom)
                                        strSignRecom = etSignRecom.getText().toString();
                                    else
                                        strSignRecom = "-";
                                    SendPostHttps sendPostHttps = new SendPostHttps(IntroSignupActivity.this,
                                            new SendPostHttps.CallbackEvent() {
                                                @Override
                                                public void getResult(String result) {
                                                    String err = "";
                                                    try {

                                                        jsonResult = new JSONObject(result);

                                                        err = jsonResult.getString("err");
                                                        if (err.equals("0")) {
                                                            MainActivity.showToast(getApplicationContext(), "회원가입을 완료하였습니다.");
                                                            Intent intent = new Intent(getApplicationContext(), IntroLoginActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.putExtra("user_email", strSignEmail);
                                                            intent.putExtra("user_password", strSignPasswd);
                                                            intent.putExtra("is_signup", true);
                                                            startActivity(intent);
                                                            finish();

                                                        } else {

                                                            MainActivity.showToast(getApplicationContext(), "");
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                        FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                                        FirebaseCrash.report(e);

                                                    }
                                                }
                                            });

                                    sendPostHttps.setUrl(getString(R.string.url_register_user));
                                    sendPostHttps.execute("user_name=" + strSignName +
                                            "&email=" + strSignEmail +
                                            "&user_phone=" + strSignPhone +
                                            "&user_sex=" + "man" +
                                            "&user_path=" + strSignPath +
                                            "&user_recom=" + strSignRecom +
                                            "&passwd=" + strSignPasswd +
                                            "&kakao_id=" + strKakaoId +
                                            "&facebook_id=" + strFacebookId);
                                } else {

                                    //isSgnCbPer
                                    MainActivity.showToast(getApplicationContext(), "카모니 이용 약관에 동의하지 않으셨어요");
                                }
                            } else {
                                //isSgnCbPer
                                MainActivity.showToast(getApplicationContext(), "개인정보 수집/이용 약관에 동의하지 않으셨어요");
                            }
                        } else {
                            //isSignPath
                            MainActivity.showToast(getApplicationContext(), "가입경로를 선택해주세요");

                        }

                        break;
                    default:
                        //Log.d(TAG,"IntroSignupActivityViewpager onclick default");
                        break;

                }
                break;
            default:
                //Log.d(TAG,"IntroSignupActivityViewpager onclick default");
                break;

        }

    }


    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validatePassword(String pwStr) {
        Matcher matcher = VALID_PASSWORD_REGEX_ALPHA_NUM.matcher(pwStr);
        return matcher.matches();
    }

    public static boolean validateName(String nameStr) {
        Matcher matcher = VALID_NAME.matcher(nameStr);
        return matcher.matches();
    }

    public static boolean validatePhone(String phoneStr) {
        Matcher matcher = VALID_PHONE_NUM.matcher(phoneStr);
        return matcher.matches();
    }

    public static boolean validateNull(String str) {
        str = str.trim();
        boolean isNull = false;
        if (str.length() > 0)
            isNull = true;
        return isNull;
    }

    public void editTextSetup() {
        etSignEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // TODO Auto-generated method stub
                if (b) {
                    etSignEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_horiz_black_24dp, 0);
                } else {
                    if (!etSignEmail.getText().toString().equals("") && validateEmail(etSignEmail.getText().toString())) {
                        SendPost sendPost = new SendPost(getApplicationContext());
                        sendPost.setLoadingImg(false);
                        sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                            @Override
                            public void getResult(String result) {
                                try {

                                    jsonResult = new JSONObject(result);

                                    if (jsonResult.getString("err").equals("0")) {
                                        etSignEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                                        isSignEmail = true;
                                    } else {
                                        MainActivity.showToast(getApplicationContext(), "이미 가입한 이메일입니다.");
                                        etSignEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_block_black_24dp, 0);
                                        isSignEmail = false;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                    FirebaseCrash.report(e);

                                }

                            }
                        });
                        sendPost.setUrl(getString(R.string.url_get_check_email));
                        sendPost.execute("email=" + etSignEmail.getText().toString());

                    } else {
                        MainActivity.showToast(getApplicationContext(), "이메일 양식을 확인해주세요");
                        etSignEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_block_black_24dp, 0);
                    }
                }

            }

        });

        etSignName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etSignName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_horiz_black_24dp, 0);
                } else {
                    if (validateName(etSignName.getText().toString())) {
                        etSignName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    } else {
                        MainActivity.showToast(getApplicationContext(), "닉네임은 영어 혹은 한글로만 작성이 가능합니다");
                        etSignName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_block_black_24dp, 0);
                    }

                }
            }
        });
        etSignPasswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etSignPasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_horiz_black_24dp, 0);
                } else {
                    if (validatePassword(etSignPasswd.getText().toString())) {
                        etSignPasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    } else {
                        MainActivity.showToast(getApplicationContext(), "비밀번호 양식을 확인해주세요");
                        etSignPasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_block_black_24dp, 0);
                    }

                }
            }
        });
        etSignRePasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSignPasswd.getText().toString().equals(etSignRePasswd.getText().toString()))
                    etSignRePasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etSignRePasswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etSignRePasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_horiz_black_24dp, 0);
                } else {
                    if (etSignRePasswd.getText().toString().equals(etSignPasswd.getText().toString())) {
                        etSignRePasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_done_black_24dp, 0);
                    } else {

                        MainActivity.showToast(getApplicationContext(), "비밀번호가 일치하지 않아요");
                        etSignRePasswd.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_block_black_24dp, 0);
                    }

                }
            }
        });
        etSignPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etSignPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_more_horiz_black_24dp, 0);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent() != null && getIntent().getStringExtra("is_intro") != null && getIntent().getStringExtra("is_intro").equals("y")) {
            Intent intent = new Intent(IntroSignupActivity.this, IntroTutorialActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
