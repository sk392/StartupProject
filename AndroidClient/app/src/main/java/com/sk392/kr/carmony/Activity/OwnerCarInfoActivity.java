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

public class OwnerCarInfoActivity extends AppCompatActivity implements View.OnClickListener, SendPost.CallbackEvent{
    Toolbar toolbar;
    TextView tvToolbar;
    SharedPreferences sharedPreferences;
    JSONObject jsonResult;
    private static final String TAG = "OwnerCarInfoActivity";

    OwnerCarInfoAdapter ownerCarInfoAdapter;
    List<OwnerCarInfoItem> ownerCarInfoItems;
    RecyclerView rvOwnerCarInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_car_info);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);
        rvOwnerCarInfo = (RecyclerView)findViewById(R.id.rv_owner_car_info);
        toolbar = (Toolbar)findViewById(R.id.introtoolbar);
        tvToolbar = (TextView)toolbar.findViewById(R.id.tv_toolbar);
        ownerCarInfoItems = new ArrayList<>();
        SendPost sendPost = new SendPost(OwnerCarInfoActivity.this);
        sendPost.setUrl(getString(R.string.url_get_user_car_list));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken="+sharedPreferences.getString(getString(R.string.shared_token),""));


        ownerCarInfoAdapter = new OwnerCarInfoAdapter(getApplicationContext(),ownerCarInfoItems,R.layout.item_owner_car_info,OwnerCarInfoActivity.this);
        rvOwnerCarInfo.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvOwnerCarInfo.setHasFixedSize(true);
        rvOwnerCarInfo.setItemAnimator(new DefaultItemAnimator());
        rvOwnerCarInfo.setAdapter(ownerCarInfoAdapter);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = rvOwnerCarInfo.getChildLayoutPosition(v);
        OwnerCarInfoItem item = ownerCarInfoItems.get(itemPosition);
        Intent intent=new Intent();
        intent.putExtra("carinfoId",item.getCarinfoId());
        intent.putExtra("tvToolbar",item.getCarModel()+" "+item.getCarYear());
        intent.setClass(getApplicationContext(),OwnerCarInfoDetailActivity.class);
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
    private void jsonCarinfoResult(String err) {

        if (err.equals("0")) {
            try {
                JSONArray jsonArray = jsonResult.getJSONArray("ret");
                for(int i=0;i<jsonResult.getInt("cnt");i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ownerCarInfoItems.add(new OwnerCarInfoItem(jsonObject.getString("carinfo_img_t"),jsonObject.getString("model"),jsonObject.getString("model_year"),jsonObject.getString("carinfo_id")));

                }
                ownerCarInfoAdapter = new OwnerCarInfoAdapter(getApplicationContext(),ownerCarInfoItems,R.layout.item_owner_car_info,OwnerCarInfoActivity.this);
                rvOwnerCarInfo.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                rvOwnerCarInfo.setHasFixedSize(true);
                rvOwnerCarInfo.setItemAnimator(new DefaultItemAnimator());
                rvOwnerCarInfo.setAdapter(ownerCarInfoAdapter);

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
            Log.d("bt",result);
            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            jsonCarinfoResult(err);
        } catch (JSONException e){
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        }
    }
}
