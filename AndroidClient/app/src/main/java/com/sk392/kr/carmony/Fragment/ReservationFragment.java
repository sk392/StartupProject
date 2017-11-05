package com.sk392.kr.carmony.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Activity.MainActivity;
import com.sk392.kr.carmony.Activity.ReservationDetailActivity;
import com.sk392.kr.carmony.Adapter.ReservationAdapter;
import com.sk392.kr.carmony.Item.ReservationItem;
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

public class ReservationFragment extends Fragment implements View.OnClickListener, SendPost.CallbackEvent {
    View resView;
    Toolbar tbReservation;
    ImageButton ibToolbarReservation;
    TextView tvResToolbar;
    SharedPreferences sharedPreferences;
    boolean isOwner;
    private static final String TAG = "ReservationFragment";
    SwipeRefreshLayout srlReservation;
    int userType;
    JSONObject jsonResult;
    public static final int RESDETAILACTIVITY = 193;
    RecyclerView rvResList;
    public View.OnClickListener mOnClickListener;
    List<ReservationItem> resList;
    ReservationAdapter reservationAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resView = inflater.inflate(R.layout.fragment_reservation, container, false);
        resList = new ArrayList<>();

        setRetainInstance(true);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name), Context.MODE_PRIVATE);
        mOnClickListener = this;
        tbReservation = (Toolbar) getActivity().findViewById(R.id.introtoolbar);
        tvResToolbar = (TextView) tbReservation.findViewById(R.id.tv_toolbar);
        ibToolbarReservation = (ImageButton) tbReservation.findViewById(R.id.ib_toolbar_reservation);
        rvResList = (RecyclerView) resView.findViewById(R.id.rv_reservation);
        srlReservation = (SwipeRefreshLayout) resView.findViewById(R.id.srl_reservation);
        srlReservation.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOwner)
                    setupOwnerList();
                else
                    setupRentList();
                srlReservation.setRefreshing(false);
            }
        });
        rvResList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        rvResList.setHasFixedSize(true);
        rvResList.setItemAnimator(new DefaultItemAnimator());
        ReservationItem beforePlaces = new ReservationItem(ReservationAdapter.HEADER, "이전 예약내역");
        beforePlaces.invisibleChildren = new ArrayList<>();

        ReservationItem afterPlaces = new ReservationItem(ReservationAdapter.HEADER, "예약 요청내역");
        afterPlaces.invisibleChildren = new ArrayList<>();

        ReservationItem nowPlaces = new ReservationItem(ReservationAdapter.HEADER, "현재 예약내역");
        nowPlaces.invisibleChildren = new ArrayList<>();

        resList.add(afterPlaces);
        resList.add(nowPlaces);
        resList.add(beforePlaces);

        reservationAdapter = new ReservationAdapter(resList, getActivity().getApplicationContext(), this);
        rvResList.setAdapter(reservationAdapter);

        if (sharedPreferences.getString(getString(R.string.shared_type), "").equals("0")) {
            //차주가 아닐 경우
            isOwner = false;
            setupRentList();
        } else {
            //차주 일 경우
            isOwner = true;
            setupOwnerList();
        }

        ibToolbarReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOwner) {

                    setupRentList();
                    tvResToolbar.setText(R.string.reservation_fage_name);
                } else {
                    setupOwnerList();
                    tvResToolbar.setText(R.string.reservation_fage_name_owner);

                }
                isOwner = !isOwner;
            }
        });

        return resView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //click item
    @Override
    public void onClick(View v) {
        Intent resDetailintent = new Intent(getActivity(), ReservationDetailActivity.class);

        ReservationItem item;
        int itemPosition = rvResList.getChildLayoutPosition(v);
        item = resList.get(itemPosition);
        resDetailintent.putExtra("carinfoId", item.getCarId());
        resDetailintent.putExtra("reservationId", item.getReservationId());
        resDetailintent.putExtra("reservationType", item.getReservationType());
        resDetailintent.putExtra("reservationState", item.getReservationState());
        resDetailintent.putExtra("userinfoId", item.getUserId());
        resDetailintent.putExtra("ownerId", item.getOwnerId());
        resDetailintent.putExtra("startDate", item.getResStart());
        resDetailintent.putExtra("endDate", item.getResEnd());
        resDetailintent.putExtra("cost", item.getCost());
        resDetailintent.putExtra("deliveryCost", item.getDeliveryCost());
        resDetailintent.putExtra("lateCost", item.getLateCost());
        resDetailintent.putExtra("onedayCost", item.getOnedayCost());
        resDetailintent.putExtra("userType", userType);
        startActivityForResult(resDetailintent, RESDETAILACTIVITY);
    }

    public void setupRentList() {
        userType = 0;
        SendPost sendPost = new SendPost(getActivity());
        sendPost.setUrl(getString(R.string.url_get_reservation_list));
        sendPost.setCallbackEvent(this);
        //드라이버로서 검색.
        sendPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "") +
                        "&type=" + userType);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESDETAILACTIVITY && data != null) {
                if (data.getExtras().getBoolean("is_cancel")) {
                    if (isOwner) {
                        //차주 일 경우
                        setupOwnerList();
                    } else {
                        //차주가 아닐 경우
                        setupRentList();
                    }
                }
            }
        }
    }

    public void setupOwnerList() {
        userType = 1;
        SendPost sendPost = new SendPost(getActivity());
        sendPost.setUrl(getString(R.string.url_get_reservation_list));
        sendPost.setCallbackEvent(this);
        //드라이버로서 검색.
        sendPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "") +
                        "&type=" + userType);
        Log.d(TAG, "sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "") +
                "&type=" + userType);

    }

    @Override
    public void getResult(String result) {
        try {
            Date startDate, endDate, nDate;
            List<ReservationItem> afterList = new ArrayList<>(), nList = new ArrayList<>();
            String err;

            Calendar nCalendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatted = dateFormat.format(nCalendar.getTime());
            ReservationItem places = new ReservationItem(ReservationAdapter.HEADER, "이전 예약내역");
            places.invisibleChildren = new ArrayList<>();

            jsonResult = new JSONObject(result);
            nDate = dateFormat.parse(formatted);
            err = jsonResult.getString("err");
            Log.d("ReservationFragment", jsonResult.toString());
            if (err.equals("0")) {
                if (!jsonResult.getString("cnt").equals("0")) {
                    JSONArray jsonArray = jsonResult.getJSONArray("ret");
                    resList.clear();

                    //전체 아이템을 분류하는 시점.
                    for (int i = 0; i < jsonResult.getInt("cnt"); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        startDate = dateFormat.parse(jsonObject.getString("start_date"));
                        endDate = dateFormat.parse(jsonObject.getString("end_date"));

                        if (startDate.after(nDate)) {
                            //예약 요청내역
                            //대여 시간이가 현재 시간보다  이후인 경우 / 15일 일 때 16잏 이후

                            afterList.add(new ReservationItem(ReservationAdapter.CHILD, jsonObject.getString("carinfo_img_t")
                                    , jsonObject.getString("model"), jsonObject.getString("model_year"), jsonObject.getString("start_date")
                                    , jsonObject.getString("end_date"), jsonObject.getString("carinfo_id"), jsonObject.getString("reservation_id")
                                    , ReservationAdapter.RES_AFTER, jsonObject.getString("userinfo_id"), jsonObject.getString("cost")
                                    , jsonObject.getString("delivery_cost"), jsonObject.getString("oneday_cost"), jsonObject.getString("late_cost")
                                    , jsonObject.getString("isdelivery"), jsonObject.getString("isoneday"), jsonObject.getString("state")
                                    , jsonObject.getString("owner_id")));

                        } else if (endDate.before(nDate)) {
                            //이전 예약내역
                            //반납 시간이 현재 시간보다 이전인 경우.
                            if (jsonObject.getString("state").equals("1")) {
                                //결제완료된 애들만
                                places.invisibleChildren.add(new ReservationItem(ReservationAdapter.CHILD, jsonObject.getString("carinfo_img_t")
                                        , jsonObject.getString("model"), jsonObject.getString("model_year"), jsonObject.getString("start_date")
                                        , jsonObject.getString("end_date"), jsonObject.getString("carinfo_id"), jsonObject.getString("reservation_id")
                                        , ReservationAdapter.RES_BEFORE, jsonObject.getString("userinfo_id"), jsonObject.getString("cost")
                                        , jsonObject.getString("delivery_cost"), jsonObject.getString("oneday_cost"), jsonObject.getString("late_cost")
                                        , jsonObject.getString("isdelivery"), jsonObject.getString("isoneday"), jsonObject.getString("state")
                                        , jsonObject.getString("owner_id")));

                            }

                        } else {
                            //현재 예약내역
                            //현재 시간이 시작시간과 종료 시간 사이인 경우
                            if (jsonObject.getString("state").equals("1")) {
                                //결제완료 된 애들만
                                nList.add(new ReservationItem(ReservationAdapter.CHILD, jsonObject.getString("carinfo_img_t")
                                        , jsonObject.getString("model"), jsonObject.getString("model_year"), jsonObject.getString("start_date")
                                        , jsonObject.getString("end_date"), jsonObject.getString("carinfo_id"), jsonObject.getString("reservation_id")
                                        , ReservationAdapter.RES_NOW, jsonObject.getString("userinfo_id"), jsonObject.getString("cost")
                                        , jsonObject.getString("delivery_cost"), jsonObject.getString("oneday_cost"), jsonObject.getString("late_cost")
                                        , jsonObject.getString("isdelivery"), jsonObject.getString("isoneday"), jsonObject.getString("state")
                                        , jsonObject.getString("owner_id")));


                            }
                        }
                    }
                    if (afterList.size() == 0) {
                        //자식이 없는 경우  invisiblechildren이 null이 아니게끔해서 화살표가 열리지 않은 것처럼.
                        ReservationItem item = (new ReservationItem(ReservationAdapter.HEADER, "예약 요청내역"));
                        item.invisibleChildren = new ArrayList<>();
                        item.invisibleChildren.add(new ReservationItem(ReservationAdapter.NON_CHILD, "예약이 없습니다."));
                        resList.add(item);

                    } else {
                        //자식이 있는 경우
                        resList.add(new ReservationItem(ReservationAdapter.HEADER, "예약 요청내역"));

                        for (int i = 0; i < afterList.size(); i++) {
                            resList.add(afterList.get(i));

                        }

                    }

                    if (nList.size() == 0) {
                        //자식이 없는 경우 invisiblechildren이 null이 아니게끔해서 화살표가 열리지 않은 것처럼.
                        ReservationItem item = (new ReservationItem(ReservationAdapter.HEADER, "현재 예약내역"));
                        item.invisibleChildren = new ArrayList<>();
                        item.invisibleChildren.add(new ReservationItem(ReservationAdapter.NON_CHILD, "예약이 없습니다."));
                        resList.add(item);

                    } else {
                        //자식이 있는 경우
                        resList.add(new ReservationItem(ReservationAdapter.HEADER, "현재 예약내역"));

                        for (int i = 0; i < nList.size(); i++) {
                            resList.add(nList.get(i));

                        }

                    }
                    if (places.invisibleChildren.size() == 0) {
                        //이전 예약에 자식이 없는 경우
                        places.invisibleChildren.add(new ReservationItem(ReservationAdapter.NON_CHILD, "예약이 없습니다."));
                    }
                    resList.add(places);


                    rvResList.setAdapter(reservationAdapter);
                    reservationAdapter.notifyDataSetChanged();
                } else {
                    //결과가 0인경우!
                    resList.clear();
                    ReservationItem item = (new ReservationItem(ReservationAdapter.HEADER, "예약 요청내역"));
                    item.invisibleChildren = new ArrayList<>();
                    item.invisibleChildren.add(new ReservationItem(ReservationAdapter.NON_CHILD, "예약이 없습니다."));
                    resList.add(item);
                    ReservationItem nItem = (new ReservationItem(ReservationAdapter.HEADER, "현재 예약내역"));
                    nItem.invisibleChildren = new ArrayList<>();
                    nItem.invisibleChildren.add(new ReservationItem(ReservationAdapter.NON_CHILD, "예약이 없습니다."));
                    resList.add(nItem);
                    places.invisibleChildren.add(new ReservationItem(ReservationAdapter.NON_CHILD, "예약이 없습니다."));
                    resList.add(places);


                    rvResList.setAdapter(reservationAdapter);
                    reservationAdapter.notifyDataSetChanged();

                }
            } else {
                MainActivity.showToast(getActivity().getApplicationContext(), "서버에 잠시 접근할 수가 없습니다. 잠시 후 다시 시도해주세요.");
                Log.d("reservationFragment_err", err + jsonResult.getString("err_result"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
            FirebaseCrash.report(e);

        } catch (ParseException e) {
            e.printStackTrace();
            FirebaseCrash.logcat(Log.ERROR, TAG, "String to date parse Fail");
            FirebaseCrash.report(e);

        }
    }

    public boolean getisOwner() {
        return isOwner;
    }

}
