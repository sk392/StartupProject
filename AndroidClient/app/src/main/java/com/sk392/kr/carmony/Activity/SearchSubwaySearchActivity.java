package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.SearchSubwaySearchAdapter;
import com.sk392.kr.carmony.Adapter.SearchSubwaySearchHistoryAdapter;
import com.sk392.kr.carmony.Item.SubwaySearchHistoryItem;
import com.sk392.kr.carmony.Item.SubwaySearchItem;
import com.sk392.kr.carmony.Library.RecyclerDecoration;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;

public class SearchSubwaySearchActivity extends AppCompatActivity implements View.OnClickListener, SendPost.CallbackEvent {
    ImageButton ibSubwayClear, ibSubwayDismiss;
    EditText etSubway;
    RecyclerView rvSubwaySearch;
    List<SubwaySearchItem> subwaySearchItems;
    SearchSubwaySearchAdapter searchSubwaySearchAdapter;
    RelativeLayout rlSubwaySearchHistory;
    TextView tvSubwaySearchHistoryNonResult,tvSubwaySearchHistoryResult;
    RecyclerView rvSubwaySearchHistory;
    List<SubwaySearchHistoryItem> subwaySearchHistoryItems;
    SearchSubwaySearchHistoryAdapter searchSubwaySearchHistoryAdapter;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String TAG = "SearchSubwaySearchActivity";

    JSONObject jsonResult;
    String err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_subway_search);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        ibSubwayClear = (ImageButton) findViewById(R.id.ib_subway_clear);
        ibSubwayClear.setVisibility(View.GONE);
        rlSubwaySearchHistory = (RelativeLayout) findViewById(R.id.rl_subway_search_history);
        subwaySearchItems = new ArrayList<>();
        subwaySearchHistoryItems = new ArrayList<>();
        searchSubwaySearchAdapter = new SearchSubwaySearchAdapter(this, subwaySearchItems, R.layout.item_subway_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("SubwayName", ((TextView) v.findViewById(R.id.tv_subway_item_subwayname)).getText().toString());
                intent.putExtra("SubwayId", ((TextView) v.findViewById(R.id.tv_subway_item_subwayid)).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        searchSubwaySearchHistoryAdapter = new SearchSubwaySearchHistoryAdapter(this, subwaySearchHistoryItems, R.layout.item_subway_search_history, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSubway.setText(((TextView) v.findViewById(R.id.tv_subway_search_history_item_text)).getText().toString());
                setupList();
            }
        },sharedPreferences);

        tvSubwaySearchHistoryNonResult = (TextView)findViewById(R.id.tv_subway_search_history_non_result);
        tvSubwaySearchHistoryResult = (TextView)findViewById(R.id.tv_subway_search_history_result);
        rvSubwaySearch = (RecyclerView) findViewById(R.id.rv_subway_search);
        rvSubwaySearchHistory = (RecyclerView) findViewById(R.id.rv_subway_search_history);
        rvSubwaySearchHistory.setItemAnimator(new OvershootInRightAnimator());
        rvSubwaySearchHistory.setLayoutManager(new LinearLayoutManager(this));
        rvSubwaySearchHistory.setAdapter(searchSubwaySearchHistoryAdapter);
        rvSubwaySearch.addItemDecoration(new RecyclerDecoration(getApplicationContext()));
        rvSubwaySearch.setLayoutManager(new LinearLayoutManager(this));
        rvSubwaySearch.setAdapter(searchSubwaySearchAdapter);

        Set historyList = sharedPreferences.getStringSet(getString(R.string.shared_subway_list), null);
        if (historyList != null && historyList.size()>0) {
            rvSubwaySearch.setVisibility(View.GONE);
            rlSubwaySearchHistory.setVisibility(View.VISIBLE);
            rvSubwaySearchHistory.setVisibility(View.VISIBLE);
            tvSubwaySearchHistoryResult.setVisibility(View.VISIBLE);
            tvSubwaySearchHistoryNonResult.setVisibility(View.GONE);
            subwaySearchHistoryItems.clear();
            for (int k = 0; k < historyList.size(); k++) {
                String[] item = historyList.toArray()[k].toString().split("-");
                Log.d(TAG, k+"번째 Date =  "+historyList.toArray()[k].toString());
                subwaySearchHistoryItems.add(new SubwaySearchHistoryItem(item[0], item[1]));
            }
            searchSubwaySearchHistoryAdapter.notifyDataSetChanged();
        }else{
            rlSubwaySearchHistory.setVisibility(View.VISIBLE);
            tvSubwaySearchHistoryResult.setVisibility(View.GONE);
            tvSubwaySearchHistoryNonResult.setVisibility(View.VISIBLE);

        }

        ibSubwayDismiss = (ImageButton) findViewById(R.id.ib_subway_dismiss);
        ibSubwayDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etSubway = (EditText) findViewById(R.id.et_subway);
        etSubway.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    setupList();
                    return true;
                }
                return false;
            }
        });
        etSubway.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((TextView) findViewById(R.id.tv_subway_non_result)).setVisibility(View.GONE);
                Set historyList = sharedPreferences.getStringSet(getString(R.string.shared_subway_list), null);
                if (historyList != null && historyList.size()>0) {
                    rvSubwaySearch.setVisibility(View.GONE);
                    rlSubwaySearchHistory.setVisibility(View.VISIBLE);
                    rvSubwaySearchHistory.setVisibility(View.VISIBLE);
                    tvSubwaySearchHistoryResult.setVisibility(View.VISIBLE);
                    tvSubwaySearchHistoryNonResult.setVisibility(View.GONE);
                    subwaySearchHistoryItems.clear();
                    int startPosition = 0;
                    if( historyList.size()>5)
                        startPosition = historyList.size()-5;
                    for (int k = startPosition; k < historyList.size(); k++) {
                        String[] item = historyList.toArray()[k].toString().split("-");
                        Log.d(TAG, historyList.toArray()[k].toString());
                        subwaySearchHistoryItems.add(new SubwaySearchHistoryItem(item[0], item[1]));
                    }
                    searchSubwaySearchHistoryAdapter.notifyDataSetChanged();
                }else{
                    rlSubwaySearchHistory.setVisibility(View.VISIBLE);
                    tvSubwaySearchHistoryResult.setVisibility(View.GONE);
                    tvSubwaySearchHistoryNonResult.setVisibility(View.VISIBLE);
                }

                if (charSequence.length() > 0) {
                    ibSubwayClear.setVisibility(View.VISIBLE);
                    subwaySearchItems.clear();
                    searchSubwaySearchAdapter.notifyDataSetChanged();
                    ibSubwayClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            etSubway.setText("");

                            ibSubwayClear.setVisibility(View.GONE);
                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public void onClick(View v) {

    }

    private void setupList() {


        SendPost sendPost = new SendPost(SearchSubwaySearchActivity.this);

        sendPost.setUrl(getString(R.string.url_get_subway_list));
        sendPost.setCallbackEvent(this);
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&subway_name=" + etSubway.getText().toString().trim()
        );
        Log.d("차량리뷰", "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&subway_name=" + etSubway.getText().toString().trim()
        );

    }

    @Override
    public void getResult(String result) {
        try {

            rvSubwaySearchHistory.setVisibility(View.GONE);
            rlSubwaySearchHistory.setVisibility(View.GONE);

            jsonResult = new JSONObject(result);
            err = jsonResult.getString("err");
            if (err.equals("0")) {

                if (jsonResult.getInt("cnt") > 0) {
                    rvSubwaySearch.setVisibility(View.VISIBLE);
                    subwaySearchItems.clear();
                    JSONArray jsonArray = jsonResult.getJSONArray("ret");
                    for (int i = 0; i < jsonResult.getInt("cnt"); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subwaySearchItems.add(new SubwaySearchItem(jsonObject.getString("subway_line"), jsonObject.getString("subway_name"),
                                jsonObject.getString("subway_code"), jsonObject.getString("subway_ex_code"), jsonObject.getString("subway_id")));
                    }
                    searchSubwaySearchAdapter.notifyDataSetChanged();
                    //값이 널이 아니라면
                    if (sharedPreferences.getStringSet(getString(R.string.shared_subway_list), null) != null) {
                        //값을 넣어준당.
                        Set beforeList = sharedPreferences.getStringSet(getString(R.string.shared_subway_list), null);

                        for(int i=0; i<beforeList.size();i++){
                            String[] item = beforeList.toArray()[i].toString().split("-");
                            if(item[0].equals(etSubway.getText().toString().trim())){
                                Set afterList = new HashSet();
                                for(int j=beforeList.size()-1; j>=0;j--){
                                    if(j!=i)
                                        afterList.add(beforeList.toArray()[j]);
                                }
                                beforeList = afterList;
                            }
                        }

                        beforeList.add(etSubway.getText().toString().trim()
                                + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ".");
                        editor.putStringSet(getString(R.string.shared_subway_list), beforeList);
                        editor.commit();
                    } else {
                        Set beforeList = new HashSet();

                        //검색값-데이트 / 검색값-데이트/ 검색값-데이트...
                        beforeList.add(etSubway.getText().toString().trim()
                                + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ".");
                        editor.putStringSet(getString(R.string.shared_subway_list), beforeList);
                        editor.commit();
                    }


                } else {
                    //결과의 수가 0일경우
                    rvSubwaySearch.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.tv_subway_non_result)).setVisibility(View.VISIBLE);
                    MainActivity.showToast(getApplicationContext(), "검색 결과가 없습니다. 다시 한 번 입력해주세요");


                }
            } else {
                //에러인경우
            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);
        }
    }
}

