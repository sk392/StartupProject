
package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class OwnerScheduleAddActivity extends AppCompatActivity implements SendPost.CallbackEvent {
    GregorianCalendar nCalendar, mCalendar, startCalendar;
    int mYear, mMonth, mDay, mHour, mMinute;
    private static final String TAG = "OwnerScheduleAddActivity";

    int startYear, startMonth, startDay, startHour, startMinute;
    int endYear, endMonth, endDay, endHour, endMinute;
    RelativeLayout rlScheduleStart, rlScheduleEnd;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    int dialogFlag;
    TextView tvScheduleStart, tvScheduleEnd;
    Toolbar toolbar;
    TextView tvToolbar;
    String carinfoId;
    JSONObject jsonResult;
    SharedPreferences sharedPreferences;
    Button btScheduleAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_schedule_add);
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        carinfoId = getIntent().getStringExtra("carinfoId");
        tvToolbar.setText("일정 등록");
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btScheduleAdd = (Button) findViewById(R.id.bt_schedule_add);
        startCalendar = new GregorianCalendar();
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);

        //일정 등록버튼 서버에 등록이 성공적으로 전송되면 종료하고 이전 액티비티에 완료를 전달한다.
        btScheduleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendPost sendPost = new SendPost(OwnerScheduleAddActivity.this);
                sendPost.setCallbackEvent(OwnerScheduleAddActivity.this);
                sendPost.setUrl(getString(R.string.url_set_owner_schedule));
                sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                        + "&carinfo_id=" + carinfoId
                        + "&start_date=" + startYear + "-" + (startMonth + 1) + "-" + startDay + " " + startHour + ":00:00"
                        + "&end_date=" + endYear + "-" + (endMonth + 1) + "-" + endDay + " " + endHour + ":00:00");


            }
        });
        nCalendar = new GregorianCalendar();
        mCalendar = new GregorianCalendar();
        mCalendar.add(Calendar.DATE, 30);
        mYear = nCalendar.get(Calendar.YEAR);
        mMonth = nCalendar.get(Calendar.MONTH);
        mDay = nCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = nCalendar.get(Calendar.HOUR_OF_DAY) + 1;
        mMinute = nCalendar.get(Calendar.MINUTE);
        setupBinding();

        rlScheduleStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFlag = 1;
                datePickerDialog = DatePickerDialog.newInstance(dateSetListener, mYear, mMonth, mDay);
                datePickerDialog.setTitle("일정 시작");
                datePickerDialog.vibrate(true);
                datePickerDialog.dismissOnPause(true);
                datePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                Calendar now = Calendar.getInstance();
                datePickerDialog.setMinDate(nCalendar);
                datePickerDialog.setMaxDate(mCalendar);

                timePickerDialog = TimePickerDialog.newInstance(timeSetListener, mHour, mMinute, true);
                timePickerDialog.vibrate(true);
                timePickerDialog.dismissOnPause(true);
                timePickerDialog.enableSeconds(false);
                timePickerDialog.setTimeInterval(1, 60);//시간 / 분 / 초 간격
                timePickerDialog.setMinTime(8,0,0);
                timePickerDialog.setMaxTime(23,0,0);
                timePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                timePickerDialog.setTitle("일정 종료");

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

            }
        });
        rlScheduleEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                //시작 값이 없을 경우
                if (!tvScheduleStart.getText().toString().isEmpty()) {
                    dialogFlag = 2;
                    //시작시간 이후로 시간을 설정하기 위해서
                    datePickerDialog = DatePickerDialog.newInstance(dateSetListener, mYear, mMonth, mDay);
                    datePickerDialog.setTitle("일정 시작");
                    datePickerDialog.vibrate(true);
                    datePickerDialog.dismissOnPause(true);
                    datePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                    startCalendar.set(startYear, startMonth, startDay, startHour - 1, startMinute);
                    datePickerDialog.setMinDate(startCalendar);
                    datePickerDialog.setMaxDate(mCalendar);

                    timePickerDialog = TimePickerDialog.newInstance(timeSetListener, mHour, mMinute, true);
                    timePickerDialog.vibrate(true);
                    timePickerDialog.dismissOnPause(true);
                    timePickerDialog.enableSeconds(false);
                    timePickerDialog.setTimeInterval(1, 60);//시간 / 분 / 초 간격
                    timePickerDialog.setMinTime(8,0,0);
                    timePickerDialog.setMaxTime(23,0,0);
                    timePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                    timePickerDialog.setTitle("일정 종료");
                    datePickerDialog.show(getFragmentManager(), "DatePickerDialog");

                } else {
                    MainActivity.showToast(getApplicationContext(), "시작 시간을 먼저 설정해주세요.");
                }

            }
        });

    }

    //데이터 피커리스너
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            int beforeDay = mDay;
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            //반납시간 설정이라면

            if (dialogFlag == 1) {
                if (beforeDay == mDay) {//오늘 날짜와 시작 날짜가 같은 경우 시간 제한.
                    timePickerDialog.setMinTime(mHour +1,0,0);
                    timePickerDialog.setStartTime(mHour +10,0);
                }else{
                    timePickerDialog.setMinTime(8,0,0);
                    timePickerDialog.setStartTime(8,0,0);
                }
            } else if (dialogFlag == 2) {
                if (startDay == mDay) {//시작 날짜와 끝나는 날짜가 같은경우 시간 제한(스케쥴은 하루단위가 아니므로  startDay+1을 하지 않는다.)
                    timePickerDialog.setMinTime(startHour,0,0);
                    timePickerDialog.setStartTime(startHour,0,0);
                }else{
                    timePickerDialog.setMinTime(8,0,0);
                    timePickerDialog.setStartTime(8,0,0);
                }
            }
            timePickerDialog.show(getFragmentManager(), "TimePickerDialog");

        }
    };
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

            mHour = hourOfDay;
            mMinute = minute;
            String zerocheck = "";//분이 0분일경우 뒤에 0을 하나 더 붙여준다.

            if (dialogFlag == 1) {
                dialogFlag = 0;

                startYear = mYear;
                startMonth = mMonth;
                startDay = mDay;
                startHour = mHour;
                startMinute = mMinute;

                if (startMinute == 0) zerocheck = "0";
                else zerocheck = "";
                tvScheduleStart.setText((startMonth + 1) + "월" + startDay + "일 " + startHour + ":" + startMinute + zerocheck);

            } else if (dialogFlag == 2) {
                dialogFlag = 0;
                //최소시간 설정
                endYear = mYear;
                endMonth = mMonth;
                endDay = mDay;
                endHour = mHour;
                endMinute = mMinute;
                if (endMinute == 0) zerocheck = "0";
                else zerocheck = "";
                tvScheduleEnd.setText((endMonth + 1) + "월" + endDay + "일 " + endHour + ":" + endMinute + zerocheck);
            }
        }
    };

    public void setupBinding() {
        tvScheduleEnd = (TextView) findViewById(R.id.tv_schedule_end);
        tvScheduleStart = (TextView) findViewById(R.id.tv_schedule_start);
        rlScheduleStart = (RelativeLayout) findViewById(R.id.rl_schedule_start);
        rlScheduleEnd = (RelativeLayout) findViewById(R.id.rl_schedule_end);
    }


    @Override
    public void getResult(String result) {
        try {
            String err;
            Log.d("bt", result);
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            if (err.equals("0")) {
                //서버 전송이 완료되면 액티비티를 종료한다.
                Intent intentResult = getIntent();
                setResult(RESULT_OK, intentResult);
                finish();
            } else {
                MainActivity.showToast(getApplicationContext(), "서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }
}
