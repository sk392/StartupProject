package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.OwnerScheduleAdapter;
import com.sk392.kr.carmony.Item.OwnerScheduleItem;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OwnerScheduleActivity extends AppCompatActivity implements SendPost.CallbackEvent{
    Toolbar toolbar;
    TextView tvToolbar;
    int scheduleRequestCode = 2720;
    Date nDate;
    private static final String TAG = "OwnerScheduleActivity";

    RecyclerView rvOwnerSchedule;
    OwnerScheduleAdapter ownerScheduleAdapter;
    ImageButton ibToolbar;
    LinearLayout llOwnerSchedule;
    List<OwnerScheduleItem> list;
    int[][] scheduleList = new int[30][3];
    String carinfoId;
    SharedPreferences sharedPreferences;
    JSONObject jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_schedule);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        llOwnerSchedule = (LinearLayout)findViewById(R.id.ll_onwer_schedule);
        llOwnerSchedule.scrollBy(0,0);
        llOwnerSchedule.scrollTo(0,0);

        toolbar.requestFocus();
        tvToolbar = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_big_navy);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnerScheduleActivity.this.onBackPressed();
            }
        });
        ibToolbar = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_schedule);
        ibToolbar.setVisibility(View.VISIBLE);
        ibToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("carinfoId", carinfoId);
                intent.setClass(getApplicationContext(), OwnerScheduleAddActivity.class);
                startActivityForResult(intent, scheduleRequestCode);
            }
        });
        carinfoId = getIntent().getStringExtra("carinfoId");
        tvToolbar.setText("차량 일정");
        rvOwnerSchedule = (RecyclerView) findViewById(R.id.rv_owner_schedule);
        list = new ArrayList<>();
        setupScheduleList();/*
        ownerScheduleAdapter = new OwnerScheduleAdapter(getApplicationContext(), list, R.layout.item_schedule);
        rvOwnerSchedule.setAdapter(ownerScheduleAdapter);
        rvOwnerSchedule.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));*/
    }

    //현재 일부터 한달까지 추가한다.
    private void setupScheduleList() {

        //표준시간(협정 세계시(UTD))와 발생하는 초 값 UTC= 1970년 1월 1일
        long now = System.currentTimeMillis();
        //오늘부터 30일간의 정보를 가져온다.
        nDate = new Date(now);
        scheduleCheck();

    }

    //시작시간을 받아서 현재시간부터 한달 후 시간까지의 스케쥴을 가져온다.
    //yyyy-mm-dd HH:mm:ss 형태이어야 한다.
    private void scheduleCheck() {
        SendPost sendPost = new SendPost(OwnerScheduleActivity.this);
        sendPost.setUrl(getString(R.string.url_get_owner_schedule));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&carinfo_id=" + carinfoId);
        Log.d("ownerscheduleActivity","sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&carinfo_id=" + carinfoId);
    }

    /*
    * 스케줄 아이템을 추가하는 메소드. 관리되는 리스트는 따로 관리하며 원하는 시작시간과 끝나는 시간을 넣어준다.
    * */
    private Boolean scheduleAdd(String startStr, String endStr) {
        Log.d("str", startStr + " // " + endStr);


        int schedulePosition;
        //NN변수 들은 변수 값을들 서로 선언하고 주고받을 때 값이 연동되는 현상(이유불명)을 해소하기 위해 만들었다.
        Date startDate, endDate, dayStartDate = null, dayEndDate, dayNDate = null, dayNNDate;
        Calendar startCalendar, endCalendar, nCalendar, dayStartCalendar, dayEndCalendar, dayNCalendar, dayNNCalendar;

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        nCalendar = Calendar.getInstance();

        dayStartCalendar = Calendar.getInstance();
        dayEndCalendar = Calendar.getInstance();
        dayNCalendar = Calendar.getInstance();
        dayNNCalendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        int startHour = 0, endHour = 0;

        try {

            //데이트 형태로 변환
            startDate = transFormat.parse(startStr);
            endDate = transFormat.parse(endStr);

            // day단위로 변환하기 위해서 뒤에 시간,분,초를 때고 새로운 변수를 통해 선언한다.
            dayStartDate = dayFormat.parse(startStr);
            dayEndDate = dayFormat.parse(endStr);
            dayNDate = dayFormat.parse(dayFormat.format(nDate));
            dayNCalendar.setTime(dayNDate);
            dayNNDate = dayFormat.parse(dayFormat.format(nDate));
            dayNNCalendar.setTime(dayNNDate);

            //오늘 날짜보다 이전에 시작한 스케쥴이 오늘 이후에 끝날 경우 표시를 위해.
            if (dayNNDate.after(dayStartDate)) {
                //시작 날짜가 오늘 날짜보다 미만일 때 시작 날짜를 오늘 날짜로 설정한다.
                dayStartDate = dayNNDate;

                startHour = 8;
            }

            //마지막 날짜가 30일 후보다 뒤인 경우 표시를 위해.
            dayNDate.setDate(dayNDate.getDate() + 29);
            if (dayEndDate.after(dayNDate)) {
                //마지막 출력인 30일 후보다 끝나는 시간이 늦는다면
                dayEndDate = dayNDate;
                endHour = 23;
            }

            startCalendar.setTime(startDate);
            endCalendar.setTime(endDate);
            nCalendar.setTime(nDate);
            dayStartCalendar.setTime(dayStartDate);
            dayEndCalendar.setTime(dayEndDate);


        } catch (ParseException e) {
            e.printStackTrace();

            FirebaseCrash.logcat(Log.ERROR, TAG, "ParseException Fail");
            FirebaseCrash.report(e);
        } catch (NullPointerException e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "NullPointerException Fail");
            FirebaseCrash.report(e);
        }
        //현재 날짜부터 스케쥴 시작날짜가 얼마나 떨어져 있는지.(뷰에서 포지션을 값을 가진다.)
        int interval = 0;
        while (dayNCalendar.compareTo(dayStartCalendar) != 0) {
            //시작날짜와 오늘날짜 사이의 간격 값을 의미
            interval++;
            //시작날짜 + 1 일
            dayNCalendar.add(Calendar.DATE, 1);
        }
        //첫날부터 차이가 난 날까지만 큼 시작 포지션으로 잡는다.
        schedulePosition = interval;

        interval = 0;
        while (dayStartCalendar.compareTo(dayEndCalendar) != 0) {
            //시작 날짜가 오늘보다 이전이라면 오늘 값을 주기 때문에, 끝나는 날이 오늘보다 이전인 경우 무한 루프에 빠질 수 있다
            //시작날짜 >끝난 날짜가 되기 때문에!
            if (dayStartCalendar.compareTo(dayEndCalendar) > 0) {
                break;
            }
            //시작날짜와 끝나는 날짜의 간격을 보여줌.
            interval++;
            //시작날짜 + 1 일
            dayStartCalendar.add(Calendar.DATE, 1);
        }

        //시작 및 끝나는 시간 값이 설정되어 있지 않다면 설정한다.
        //시작일 혹은 끝나는 일이 스케쥴 표시 제한을 넘길경우 각 값이 설정되는 경우를 제외한다.
        if (startHour == 0) startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        if (endHour == 0) endHour = endCalendar.get(Calendar.HOUR_OF_DAY);

        Boolean isAfter;
        isAfter = dayStartDate.after(dayNDate);

        //오늘 이전에 끝나는 예약이 넘어온 경우  && 마지막 날이후에 시작되는 경우 예약에 표시되지 않기 위해 거른다.
        if (dayStartCalendar.compareTo(dayEndCalendar) <= 0 && !isAfter) {
            if (interval == 0) {
                //대여가 같은 날 시작해서 같은 날 끝난 경우

                scheduleList[schedulePosition][0] = 2;//아이템 타입값을 저장.
                //이전 값이 있고, 그 값이 지금시간 값보다 크다면.
                if (scheduleList[schedulePosition][1] == 0 || startHour < scheduleList[schedulePosition][1])
                    scheduleList[schedulePosition][1] = startHour;
                //이전 값이 있고, 그 값이 현재 종료 시간보다 작다면
                if (scheduleList[schedulePosition][2] == 0 || endHour > scheduleList[schedulePosition][2])
                    scheduleList[schedulePosition][2] = endHour;

            } else {
                //대여가 같은 날 시작해서 다른 날 끝난 경우
                //첫날은 시작시간부터 ~23시까지, 마지막날엔 8시~ endHour까지,중간엔 모두 가득채우기.
                for (int j = 0; j <= interval; j++) {
                    if (j == 0) {
                        //초항인지 체크
                        scheduleList[schedulePosition + j][0] = 2;//아이템 타입값을 저장.
                        //이전 값이 없거나, 그 값이 지금시간 값보다 작다면.
                        Log.d("tt1", scheduleList[schedulePosition + j][1] + "");
                        if (scheduleList[schedulePosition + j][1] == 0 || startHour < scheduleList[schedulePosition + j][1]) {
                            scheduleList[schedulePosition + j][1] = startHour;
                        }
                        Log.d("tt2", scheduleList[schedulePosition + j][1] + "");

                        scheduleList[schedulePosition + j][2] = 23;

                    } else if (j == interval) {
                        //마지막항인지 체크
                        scheduleList[schedulePosition + j][0] = 2;//아이템 타입값을 저장.
                        scheduleList[schedulePosition + j][1] = 8;
                        Log.d("tt3", scheduleList[schedulePosition + j][2] + "");
                        //이전 값이 있고, 그 값이 현재 종료 시간보다 작다면
                        if (scheduleList[schedulePosition + j][2] == 0 || endHour > scheduleList[schedulePosition + j][2]) {
                            scheduleList[schedulePosition + j][2] = endHour;
                        }
                        Log.d("tt4", scheduleList[schedulePosition + j][2] + "");

                    } else {
                        //아닌 중간 경우 체크
                        scheduleList[schedulePosition + j][0] = 2;//아이템 타입값을 저장.
                        scheduleList[schedulePosition + j][1] = 8;
                        scheduleList[schedulePosition + j][2] = 23;
                    }

                }
            }

        }
        return true;
    }

    private List<OwnerScheduleItem> scheduleUpdate() {
        List<OwnerScheduleItem> list1;
        String strCurTimeMonth,strCurTimeDay;
        SimpleDateFormat curTimeFormatMonth,curTimeFormatDay;
        long now = System.currentTimeMillis();
        list1 = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            // 현재 시간을 저장 한다.
            Date date = new Date(now);
            curTimeFormatMonth = new SimpleDateFormat("MMM");
            curTimeFormatDay = new SimpleDateFormat("d");
            strCurTimeMonth = curTimeFormatMonth.format(date);
            strCurTimeDay = curTimeFormatDay.format(date);
            now = now + 86400000;//하루를 더해주어 다음날을 구한다. 밀리 세컨즈이므로 하루시간(86400초)* 1000을 해준다.
            //타입이 1이면 스케쥴 없는 날, 2면 있는 날을 말한다.
            if (scheduleList[i][0] == 2) {
                //스케쥴이 있는 날.
                Log.d("TT", i + "");
                list1.add(new OwnerScheduleItem("2", strCurTimeMonth,strCurTimeDay, scheduleList[i][1], scheduleList[i][2]));
            } else {
                //스케쥴이 없는 날.
                list1.add(new OwnerScheduleItem("1", strCurTimeMonth,strCurTimeDay));
            }
        }
        return list1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //scheduleadd 에서 온 결과라면.
        if (scheduleRequestCode == requestCode && resultCode == RESULT_OK) {

            setupScheduleList();/*
            ownerScheduleAdapter = new OwnerScheduleAdapter(getApplicationContext(), list, R.layout.item_schedule);
            rvOwnerSchedule.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            rvOwnerSchedule.setAdapter(ownerScheduleAdapter);
            ownerScheduleAdapter.notifyDataSetChanged();*/
        }
    }

    private void jsonScheduleResult(String err) {

        if (err.equals("0")) {
            try {
                if(jsonResult.getInt("cnt")>0) {
                    JSONArray jsonArray = jsonResult.getJSONArray("ret");
                    //하나의 단위. 여기 안에서 스케줄이 있는 개수만큼 진행된다.
                    for (int i = 0; i < jsonResult.getInt("cnt"); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        scheduleAdd(jsonObject.getString("start_date"), jsonObject.getString("end_date"));
                    }
                    Log.d("papa",list.toString());

                }else{

                    Log.d("ttt","스케쥴이 없어영!");
                }
                list = scheduleUpdate();
                ownerScheduleAdapter = new OwnerScheduleAdapter(getApplicationContext(), list, R.layout.item_schedule);
                rvOwnerSchedule.setAdapter(ownerScheduleAdapter);
                rvOwnerSchedule.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                ownerScheduleAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);


            }

        }
    }

    @Override
    public void getResult(String result) {
        try {
            String err;

            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            jsonScheduleResult(err);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }
}
