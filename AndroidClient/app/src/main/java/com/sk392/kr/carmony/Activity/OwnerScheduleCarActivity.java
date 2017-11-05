package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.OwnerCarInfoAdapter;
import com.sk392.kr.carmony.Item.OwnerCarInfoItem;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OwnerScheduleCarActivity extends AppCompatActivity implements View.OnClickListener,SendPost.CallbackEvent{

    Toolbar toolbar;
    TextView tvToolbar;
    private static final String TAG = "OwnerScheduleCarActivity";

    OwnerCarInfoAdapter ownerScheduleCarAdapter;
    List<OwnerCarInfoItem> ownerScheduleCarItems;
    RecyclerView rvOwnerScheduleCar;
    SharedPreferences sharedPreferences;
    JSONObject jsonResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_schedule_car);
        rvOwnerScheduleCar = (RecyclerView)findViewById(R.id.rv_owner_schedule_car_info);
        toolbar = (Toolbar)findViewById(R.id.introtoolbar);
        tvToolbar = (TextView)toolbar.findViewById(R.id.tv_toolbar);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);

        ownerScheduleCarItems = new ArrayList<>();
        SendPost sendPost = new SendPost(OwnerScheduleCarActivity.this);
        sendPost.setUrl(getString(R.string.url_get_user_car_list));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken="+sharedPreferences.getString(getString(R.string.shared_token),""));




        ownerScheduleCarAdapter = new OwnerCarInfoAdapter(getApplicationContext(),ownerScheduleCarItems,R.layout.item_owner_car_info,this);
        rvOwnerScheduleCar.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvOwnerScheduleCar.setHasFixedSize  (true);
        rvOwnerScheduleCar.setItemAnimator(new DefaultItemAnimator());
        rvOwnerScheduleCar.setAdapter(ownerScheduleCarAdapter);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = rvOwnerScheduleCar.getChildLayoutPosition(v);
        OwnerCarInfoItem item = ownerScheduleCarItems.get(itemPosition);
        Intent intent=new Intent();
        intent.putExtra("carinfoId",item.getCarinfoId());
        intent.setClass(getApplicationContext(),OwnerScheduleActivity.class);
        startActivity(intent);

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
        tvToolbar.setText("보유 차량");
    }
    private void jsonScheduleResult(String err) {

        if (err.equals("0")) {
            try {
                JSONArray jsonArray = jsonResult.getJSONArray("ret");
                for(int i=0;i<jsonResult.getInt("cnt");i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Log.d("aa",jsonObject.toString());
                    ownerScheduleCarItems.add(new OwnerCarInfoItem(jsonObject.getString("carinfo_img_t"),jsonObject.getString("model"),jsonObject.getString("model_year"),jsonObject.getString("carinfo_id")));

                }
                ownerScheduleCarAdapter = new OwnerCarInfoAdapter(getApplicationContext(),ownerScheduleCarItems,R.layout.item_owner_car_info,this);
                rvOwnerScheduleCar.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                rvOwnerScheduleCar.setHasFixedSize(true);
                rvOwnerScheduleCar.setItemAnimator(new DefaultItemAnimator());
                rvOwnerScheduleCar.setAdapter(ownerScheduleCarAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);
            }

        }else{
            MainActivity.showToast(getApplicationContext(),"서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Override
    public void getResult(String result) {
        try {
            String err;
            Log.d("bt",result);
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            jsonScheduleResult(err);
        } catch (JSONException e){
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }
}