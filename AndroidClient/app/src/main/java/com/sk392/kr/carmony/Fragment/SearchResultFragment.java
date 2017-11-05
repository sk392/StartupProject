package com.sk392.kr.carmony.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Activity.MainActivity;
import com.sk392.kr.carmony.Activity.SearchResultDetailActivity;
import com.sk392.kr.carmony.Adapter.SearchResultAdapter;
import com.sk392.kr.carmony.Item.ResultItem;
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;


public class SearchResultFragment extends Fragment implements View.OnClickListener, SendPost.CallbackEvent {
    Toolbar tbResult;
    TextView tvResult,tvCarResultNone;
    RecyclerView rvResult;
    SwipeRefreshLayout srlCarResult;
    List<ResultItem> listResult;
    SearchResultAdapter searchResultAdapter;
    ResultItem item;
    private static final String TAG = "SearchResultFragment";
    AppBarLayout abResult;
    boolean isStartAcitvity;
    JSONObject jsonResult;
    SharedPreferences sharedPreferences;
    Intent detailIntent;
    ProgressDialog progressDialog;

    public View.OnClickListener mOnClickListener;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_search_result, container,false);
        srlCarResult = (SwipeRefreshLayout)mView.findViewById(R.id.srl_car_result);
        srlCarResult.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchResultAdapter.notifyDataSetChanged();
                srlCarResult.setRefreshing(false);
            }
        });
        rvResult = (RecyclerView) mView.findViewById(R.id.rv_car_result);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name), Context.MODE_PRIVATE);
        bundle = getArguments();
        mOnClickListener = this;
        progressDialog = new ProgressDialog(getActivity());
        //getsupportactionbar에서 뷰를 추출할수도 있지만, 가상의 메소드라 안된다.
        abResult = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        detailIntent = new Intent(getActivity(), SearchResultDetailActivity.class);
        tbResult = (Toolbar) getActivity().findViewById(R.id.introtoolbar);
        tvResult = (TextView) tbResult.findViewById(R.id.tv_toolbar);
        tvCarResultNone = (TextView)mView.findViewById(R.id.tv_car_result_none);
        listResult = new ArrayList<>();

        setupListResult(getActivity());
        return mView;

    }

    public void setupListResult(Context context) {
        SendPost sendPost = new SendPost(context);
        sendPost.setUrl(getString(R.string.url_get_car_list));
        sendPost.setCallbackEvent(this);
        Log.d(TAG, "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&offset=" + bundle.getInt("offset")
                + "&row_cnt=" + bundle.getInt("row_cnt")
                + "&car_shape=" + bundle.getString("car_shape")
                + "&car_shape_cnt=" + bundle.getString("car_shape_cnt")
                + "&start_date=" + bundle.getString("start_date")
                + "&end_date=" + bundle.getString("end_date")
                + "&option_cnt=" + bundle.getInt("option_cnt")
                + "&option=" + bundle.getString("option"));
        sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                + "&offset=" + bundle.getInt("offset")
                + "&row_cnt=" + bundle.getInt("row_cnt")
                + "&car_shape=" + bundle.getString("car_shape")
                + "&car_shape_cnt=" + bundle.getString("car_shape_cnt")
                + "&start_date=" + bundle.getString("start_date")
                + "&end_date=" + bundle.getString("end_date")
                + "&option_cnt=" + bundle.getInt("option_cnt")
                + "&option=" + bundle.getString("option")
        );
    }

    public void setupListRecyclerView(RecyclerView recyclerView, List<ResultItem> list) {
        searchResultAdapter = new SearchResultAdapter(getActivity().getApplicationContext(), list, R.layout.item_search_result, mOnClickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(searchResultAdapter);
        recyclerView.setItemAnimator(new OvershootInRightAnimator());
        recyclerView.animate();
        recyclerView.setHasFixedSize(true);
    }

    private void jsonSearchResult(String err) {
        listResult = new ArrayList<>();
        //서버로 부터 받아온 데이터를 리스트에 추갛핳..
        if (err.equals("0")) {
            try {
                if(jsonResult.getInt("cnt") !=0) {
                    JSONArray jsonArray = jsonResult.getJSONArray("ret");
                    for (int i = 0; i < jsonResult.getInt("cnt"); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.d(TAG,"차량 "+ i+ jsonObject.toString());
                        if (!(sharedPreferences.getString(getString(R.string.shared_userid), "").equals(jsonObject.getString("userinfo_id")))) {
                            //내 차가 아닌 경우만 출력
                            int[] cost = calculateCost(bundle.getString("start_date"), bundle.getString("end_date")
                                    , jsonObject.getInt("cost_deli_wday"), jsonObject.getInt("cost_deli_wday_add")
                                    , jsonObject.getInt("cost_deli_wend"), jsonObject.getInt("cost_deli_wend_add")
                                    , jsonObject.getString("isoneday"));
                            listResult.add(new ResultItem((float) jsonObject.getDouble("carinfo_review_score")
                                    , (float) jsonObject.getDouble("ow_review_score")
                                    , jsonObject.getString("model"), jsonObject.getString("model_year"), cost[0] + ""
                                    , jsonObject.getString("userinfo_img_url")
                                    , jsonObject.getString("carinfo_img_t"), jsonObject.getString("carinfo_id")
                                    , jsonObject.getString("name"), cost[1] + "", cost[3] + "", jsonObject.getString("isoneday")
                                    , (cost[2] - cost[3]) + ""
                            ));
                        }
                    }
                }else{
                    //결과가 없을 경우
                    tvCarResultNone.setVisibility(View.VISIBLE);
                    rvResult.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                FirebaseCrash.report(e);
            }

        }
        setupListRecyclerView(rvResult, listResult);

    }

    //뷰 아이템이 클릭되었을 때 실행되는 메소드
    @Override
    public void onClick(View v) {
        int itemPosition = rvResult.getChildLayoutPosition(v);
        item = listResult.get(itemPosition);
        detailIntent.putExtra("cost", item.getTvExCost());
        detailIntent.putExtra("lateCost", item.getLateCost());
        detailIntent.putExtra("deliveryCost", item.getTvExDeliveryCost());
        detailIntent.putExtra("isOneday", item.getIsOneday());
        detailIntent.putExtra("onedayCost", item.getTvExOnedayCost());
        detailIntent.putExtra("carinfoId", item.getCarId());
        detailIntent.putExtra("startDate", bundle.getString("start_date"));
        detailIntent.putExtra("endDate", bundle.getString("end_date"));
        detailIntent.putExtra("location", bundle.getString("location"));
        startActivity(detailIntent);
        isStartAcitvity = true;
    }

    /*
        * 결과창에서만 백버튼이 생기도록 조정.
        * */
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setIsMain(false);
        tbResult.setNavigationIcon(R.drawable.arrow_big_navy);
        tbResult.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        tvResult.setText("차량 검색");

    }

    @Override
    public void onPause() {
        super.onPause();
        abResult.setExpanded(true);
        ((MainActivity) getActivity()).setIsMain(true);
        tbResult.setNavigationIcon(null);
        tbResult.setNavigationOnClickListener(null);

    }

    // 주중은 일 18시 ~ 금 18시 까지
    // 주말은 금 18시 ~ 일 18시 까지.
    // 대여 가능 시간은 8시 ~ 23시 까지.
    // 주말 차지시간이 5시간 미만이면 주중 가격, 6시간 이상이면 시간당 주말 요금 + 남은 시간당 주중 요금
    // 원데이 보험은 하루에 만오천원, 하루 추가당(1시간만 넘어도 하루가 추가된다) 5천원씩, 그리고 이 가격은 나이나 경력에 따라 달라질 수 있다.
    // int[0] = 총 비용
    // int[1] = 원데이 비용
    // int[2] = 딜리버리 비용
    private int[] calculateCost(String strStartDate, String strEndDate, int wdayCost
            , int wdayCostAdd, int wendCost, int wendCostAdd, String isOneday) {
        // 추가로 몇 시간이 발생하는지
        // 또는 시간이 주말에 껴있는지 체크.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int cost[] = {0, 0, 0, 0};
        try {
            Date startDate = formatter.parse(strStartDate);
            Date endDate = formatter.parse(strEndDate);
            Calendar startCal = new GregorianCalendar(Locale.KOREA);
            Calendar endCal = new GregorianCalendar(Locale.KOREA);
            startCal.setTime(startDate);
            endCal.setTime(startDate);

            //시작날과 끝나는 날을 시간단위로 나눈다.
            long diff = endDate.getTime() - startDate.getTime();
            int diffHours = (int) (diff / (60 * 60 * 1000));
            int diffDay = (int) (diff / (24 * 60 * 60 * 1000));
            int mdiffHour = diffHours - (diffDay * 24);

            int[][] mHours;
            if (mdiffHour == 0)
                mHours = new int[diffDay][24];
            else {
                diffDay++;
                //+1 을 하는 이유는  하루 단위로 잘리지 않는 시간이 남는 경우도 체크해야하기 떄문에
                // 한칸을 더 늘려서 남는 시간을 배정하기 위해.
                mHours = new int[diffDay][24];
            }

            //하루 단위로 짤라서 남는 시간을 체크하기 위해
            for (int i = 0; i < diffDay; i++) {
                int nWeek, nHour, mHour;
                mHour = 24;

                //마지막 날인경우 남는 시간만큼만.
                if (i == diffDay - 1)
                    if (mdiffHour != 0)
                        mHour = mdiffHour;

                //하루 단위로 주중 시간인지 주말 시간인 체크해서 배열에 넣는다.
                for (int j = 0; j < mHour; j++) {

                    //1시간을 더해서 끝나는 시간을 체크.
                    startCal.add(Calendar.HOUR, 1);

                    //1 = 일, 2 = 월, 3 = 화, 4 = 수, 5 = 목, 6 = 금, 7 = 토
                    nWeek = startCal.get(Calendar.DAY_OF_WEEK);
                    // 월~ 목
                    if (nWeek <= 5 && nWeek >= 2) {
                        mHours[i][j] = 1;//1이면 주중
                    }
                    // 금요일 일 때
                    else if (nWeek == 6) {
                        nHour = startCal.get(Calendar.HOUR_OF_DAY);
                        //1시간 더 한 값이( 1시간의 종료 값이) 18이하면 주중, 19이상이면 주말.
                        if (nHour <= 18) {
                            mHours[i][j] = 1;//1이면 주중

                        } else {
                            mHours[i][j] = 2;//2이면 주말

                        }
                    }
                    // 일요일 일 때
                    else if(nWeek == 1){
                        nHour = startCal.get(Calendar.HOUR_OF_DAY);
                        //1시간 더 한 값이( 1시간의 종료 값이) 18이하면 주중, 19이상이면 주말.
                        if (nHour > 18) {
                            mHours[i][j] = 1;//1이면 주중

                        } else {
                            mHours[i][j] = 2;//2이면 주말
                        }
                    }// 토 ~ 일
                    else {
                        mHours[i][j] = 2;//2이면 주말
                    }
                }
                int wEndHours = 0, wDayHours = 0;
                //하루 단위로 주중, 주말 시간을 체크해서 요금을 정산한다.
                for (int k = 0; k < mHours[i].length; k++) {
                    //하루에  주중시간과 주말시간이 갈리는 경우를 체크한다.
                    if (mHours[i][k] == 1) {
                        wDayHours++;
                    } else if (mHours[i][k] == 2) {
                        wEndHours++;
                    }
                }

                    //10시간 이상과 그렇지 않은 경우로 나눈다.
                if ((wEndHours + wDayHours) > 10) {
                    //주말 시간이 5시간 이하면 주중 값으로 계산.
                    if (wEndHours <= 5) {
                        cost[2] += wdayCost;
                        if (cost[3] == 0) cost[3] = wdayCost;
                    }
                    // 주말 시간이 5시간 초과 10미만이면, 추가 시간만큼 계산해서 준다.
                    else if (wEndHours > 5 && wEndHours < 10) {
                        // 주말 추가 가격.만큼 계산하고 10시간에서 뺀만큼만 주중 가격 계산
                        cost[2] += wEndHours * wendCostAdd;
                        cost[2] += (10 - wEndHours) * wdayCostAdd;
                    } else {
                        //주말 시간이 10시간 이상이라면 주말 가격으로 받는다.
                        cost[2] += wendCost;
                        if (cost[3] == 0) cost[3] = wendCost;
                    }
                } else {
                    cost[2] += wEndHours * wendCostAdd;
                    cost[2] += wDayHours * wdayCostAdd;

                }

            }
            if (isOneday.equals("y")) {
                //첫 날은 15000원(최대 15000원이니까 이 정도로 잡는다.) 그 이후는 5처넌씩 하루에 추가된다.
                for (int i = 0; i < mHours.length; i++) {
                    if (i == 0) {
                        cost[1] += 15000;
                    } else {
                        cost[1] += 5000;
                    }
                }
            }else
                cost[1]=0;
            cost[0] = cost[1] + cost[2];

        } catch (ParseException e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "ParseException Fail");
            FirebaseCrash.report(e);
            e.printStackTrace();
        }

        return cost;
    }

    @Override
    public void getResult(String result) {
        String err = "";

        try {

            jsonResult = new JSONObject(result);

            err = jsonResult.getString("err");
            jsonSearchResult(err);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);
        }
    }



}

