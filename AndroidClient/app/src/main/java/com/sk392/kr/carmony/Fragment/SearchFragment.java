package com.sk392.kr.carmony.Fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sk392.kr.carmony.Activity.MainActivity;
import com.sk392.kr.carmony.Activity.SearchSubwaySearchActivity;
import com.sk392.kr.carmony.Adapter.SearchOptionAdapter;
import com.sk392.kr.carmony.Dialog.CarModelDialogFragment;
import com.sk392.kr.carmony.Dialog.OptionDialogFragment;
import com.sk392.kr.carmony.Item.OptionItem;
import com.sk392.kr.carmony.Library.RecyclerItemClickListener;
import com.sk392.kr.carmony.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment implements OptionDialogFragment.OptionDialogListener {
    ArrayAdapter<String> mSpinnerAdapter;
    RecyclerView rvOption;
    LinearLayoutManager lmOption;
    private static final String TAG = "SearchFragment";
    Toolbar tbResult;
    TextView tvResult;
    boolean[] isCarModelList;
    final static public int CARDIALOG_CODE = 1000;
    final static public int OPTIONDIALOG_CODE = 1001;
    final static public int CLICKABLE_YES = 1002;
    final static public int CLICKABLE_NO = 1003;
    final static public int SEARCH_SUBWAY = 1004;
    Button btSearch;
    // Spinner spModel;
    OptionDialogFragment optionDialog;
    CarModelDialogFragment carModelDialog;
    static public TypedArray optionDrawable;

    static public int[] modelsDrawable;
    List<OptionItem> selectedOptionItems;
    SearchOptionAdapter searchOptionAdapter;
    static public String[] optionStringKo, optionStringEn, modelsString,modelsStringEn;
    int dialogFlag;
    int mYear, mMonth, mDay, mHour, mMinute;
    int startYear, startMonth, startDay, startHour, startMinute;
    int endYear, endMonth, endDay, endHour, endMinute;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    RelativeLayout rlSearchStart, rlSearchEnd, rlSearchCarModel, rlSearchSubway;
    TextView tvSearchStart, tvSearchEnd, tvSearchCarModel,tvSearchSubway;
    boolean mIsStart = false, mIsEnd = false, mIsSubway = false;
    //옵션 개수만큼 설정해준다. 2017-02-12 : 7개
    boolean[] mSelectedPosition = {false,false,false,false,false,false, false};
    GregorianCalendar calendar;
    View mView;

    //다이얼로그 인터페이스 리스너.


    public List<OptionItem> getSelectedOptionItems() {
        return selectedOptionItems;
    }

    public void setSelectedOptionItems(List<OptionItem> selectedOptionItems) {
        this.selectedOptionItems = selectedOptionItems;
    }

    public boolean[] getmSelectedPosition() {
        return mSelectedPosition;
    }

    public void setmSelectedPosition(boolean[] mSelectedPosition) {
        this.mSelectedPosition = mSelectedPosition;
    }

    @Override
    public void checkSelectedItem(DialogFragment dialog, List<OptionItem>  selectedOptionItems , boolean[] selectedPosition) {
        this.selectedOptionItems = selectedOptionItems;
        this.mSelectedPosition = selectedPosition;
        List<OptionItem> selectedList = new ArrayList<>();

        //임시로 가장 왼쪽에 옵션추가를 넣어주기위함.  멤버변수에 넣지 않는다.
        selectedList.add(new OptionItem(R.drawable.add_icon, "옵션 추가", "addOption",false));
        selectedList.addAll(selectedOptionItems);
        searchOptionAdapter.setItems(selectedList);
        rvOption.setAdapter(searchOptionAdapter);
        searchOptionAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search, container, false);
        //검색 시작 날짜 선택

        //총 옵션 리스트의 이름및 xml
        dialogFlag = 0;
        setupBinding();
        optionDialog = new OptionDialogFragment();
        //번들을 추가해서 디테일인지 서치인지 구분하여 버튼을 변경
        Bundle arguments = new Bundle();
        arguments.putInt("type", CLICKABLE_YES);
        arguments.putBooleanArray("selectedPosition", mSelectedPosition);
        String str="" ;
        for(int i=0; i<mSelectedPosition.length;i++){
            str += i+" = "+mSelectedPosition[i];
        }
        isCarModelList = new boolean[modelsDrawable.length];
        isCarModelList[0]= true;//초기값은 전체보기

        optionDialog.setArguments(arguments);
        optionDialog.setTargetFragment(SearchFragment.this, OPTIONDIALOG_CODE);
        carModelDialog = new CarModelDialogFragment();
        carModelDialog.setTargetFragment(SearchFragment.this, CARDIALOG_CODE);





        rlSearchStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = new GregorianCalendar();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                //
                if(mHour>=22) {
                    mHour = 8;
                    mDay++;
                }else if(mHour<=8)
                    mHour = 8;
                mMinute = calendar.get(Calendar.MINUTE);

                dialogFlag = 1;
                datePickerDialog = DatePickerDialog.newInstance(dateSetListener, mYear, mMonth, mDay);
                datePickerDialog.setTitle("대여시간");
                datePickerDialog.vibrate(true);
                datePickerDialog.dismissOnPause(true);
                datePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                Calendar now = Calendar.getInstance();
                datePickerDialog.setMinDate(now);

                timePickerDialog = TimePickerDialog.newInstance(timeSetListener, mHour, mMinute, true);
                timePickerDialog.vibrate(true);
                timePickerDialog.dismissOnPause(true);
                timePickerDialog.enableSeconds(false);
                timePickerDialog.setTimeInterval(1, 30);//시간 / 분 / 초 간격
                timePickerDialog.setMinTime(8,0,0);
                timePickerDialog.setMaxTime(23,0,0);
                timePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                timePickerDialog.setTitle("대여시간");
                datePickerDialog.show(getFragmentManager(),"DatePickerDialog");
            }
        });
        rlSearchEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //시작 값이 있을 경우
                if (!tvSearchStart.getText().toString().isEmpty()) {
                    dialogFlag = 2;
                    datePickerDialog = DatePickerDialog.newInstance(dateSetListener, startYear, startMonth, startDay+1);
                    datePickerDialog.setTitle("반납시간");
                    datePickerDialog.vibrate(true);
                    datePickerDialog.dismissOnPause(true);
                    datePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.DAY_OF_MONTH,startDay+1);
                    datePickerDialog.setMinDate(now);

                    timePickerDialog = TimePickerDialog.newInstance(timeSetListener, startHour, startMinute, true);
                    timePickerDialog.vibrate(true);
                    timePickerDialog.enableMinutes(false);
                    timePickerDialog.dismissOnPause(true);
                    timePickerDialog.enableSeconds(false);
                    timePickerDialog.setTimeInterval(1, 30);//시간 / 분 / 초 간격
                    timePickerDialog.setMinTime(8,0,0);
                    if(startMinute==30)
                        timePickerDialog.setMaxTime(22,30,0);
                    else
                        timePickerDialog.setMaxTime(23,0,0);

                    timePickerDialog.setAccentColor(Color.parseColor("#f6672b"));
                    timePickerDialog.setTitle("반납시간");
                    datePickerDialog.show(getFragmentManager(),"DatePickerDialog");

                } else {
                    //시작 값이 없을 경우
                    MainActivity.showToast(getActivity().getApplicationContext(), "시작 시간을 먼저 설정해주세요.");
                }

            }
        });
        rlSearchCarModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carModelDialog.setIsCheckedList(isCarModelList);
                carModelDialog.show(getFragmentManager(), "carModelDialog");

            }
        });
        rlSearchSubway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), SearchSubwaySearchActivity.class);
                startActivityForResult(intent, SEARCH_SUBWAY);
            }
        });
        mSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                // 안드로이드 values 폴더에 arrays에 셋팅된 List를 Adapter에 셋팅 해준다.
                // getResources() 메서드는 리소스 사용에 관한 메서드로 Activity 상속시
                // 사용할 수 있다.
                getResources().getStringArray(R.array.car_models));


        //검색버튼
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mIsStart && mIsEnd && mIsSubway) {
                    FragmentTransaction trans = getFragmentManager()
                            .beginTransaction();
                    String mItems = "";
                    SearchResultFragment resultFragment = new SearchResultFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("offset", 0);
                    bundle.putString("location", tvSearchSubway.getText().toString());
                    bundle.putInt("row_cnt", 30);//검색 개수.
                    String minute;
                    if(startMinute==0)
                        minute = "00";
                    else
                        minute = startMinute +"";

                    bundle.putString("start_date", startYear + "-" + (startMonth+1) + "-" + startDay + " " + startHour + ":" + minute + ":00");
                    if(endMinute==0)
                        minute = "00";
                    else
                        minute = endMinute +"";

                    bundle.putString("end_date", endYear + "-" + (endMonth+1) + "-" + endDay + " " + endHour + ":" + minute + ":00");
                    bundle.putInt("option_cnt", searchOptionAdapter.getItemCount() - 1);
                    for (int i = 1; i < searchOptionAdapter.getItemCount(); i++) {
                        if (i == (searchOptionAdapter.getItemCount()))
                            mItems = mItems + searchOptionAdapter.getItem(i).getTitleEn();
                        mItems = mItems + searchOptionAdapter.getItem(i).getTitleEn() + "/";
                    }
                    bundle.putString("option", mItems);

                    String carShape ="";
                    String[] carShapeList =tvSearchCarModel.getText().toString().split(",");
                    for(int j=0;j<carShapeList.length;j++) {
                        for (int i = 0; i < modelsString.length; i++) {
                            if (carShapeList[j].equals(modelsString[i])){
                                if(!carShape.equals(""))
                                    carShape +="/";
                                carShape += modelsStringEn[i];
                            }
                        }
                    }
                    bundle.putString("car_shape", carShape);
                    bundle.putString("car_shape_cnt", carShapeList.length+"");
                    Log.d(TAG +"111", startYear + "-" + (startMonth+1) + "-" + startDay + " " + startHour + ":" + minute + ":00" +"//"+ endYear + "-" + (endMonth+1) + "-" + endDay + " " + endHour + ":" + minute + ":00");

                    resultFragment.setArguments(bundle);
                    mSelectedPosition = new boolean[mSelectedPosition.length];
                    trans.add(R.id.root_fragment,resultFragment);
                    //trans.replace(R.id.root_fragment, resultFragment);//replace대신 add를 사용하므로써 이전 검색 값 저장.

                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);

                    trans.commit();
//                    mIsSubway = false;
//                    mIsStart = false;
//                    mIsEnd = false;

                    //뒤로가기가 종료로 이어지지 않돌고 연결.
                    ((MainActivity) getActivity()).setIsMain(false);
                }else if(!mIsStart){
                    MainActivity.showToast(getActivity().getApplicationContext(),"대여시간을 설정해주세요");
                }else if(!mIsEnd){
                    MainActivity.showToast(getActivity().getApplicationContext(),"반납시간을 설정해주세요");
                }else if(!mIsSubway){
                    MainActivity.showToast(getActivity().getApplicationContext(),"대여 지역을 설정해주세요");
                }

            }

        });
        List<OptionItem> items = new ArrayList<>();

        items.add(new OptionItem(R.drawable.add_icon, "옵션 추가", "optionAdd",false));
        searchOptionAdapter = new SearchOptionAdapter(getActivity().getApplicationContext(), items, R.layout.fragment_search);
        rvOption.setAdapter(searchOptionAdapter);
        rvOption.addOnItemTouchListener(new RecyclerItemClickListener(getActivity().getApplicationContext(), rvOption, new RecyclerItemClickListener.OnItemClickListener() {
            //가장 처음에 있는 아이템이 클릭된다면 옵션 다이얼로그 실행(옵션 추가 아이템)
            @Override
            public void onItemClick(View view, int position) {
                if (position ==0) {

                    optionDialog.show(getFragmentManager(), "ABCD");
                    //옵션 추가 눌렀을 때 반응 보이도록.
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_option_item);
                    imageView.setPressed(true);
                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return mView;

    }

    //데이터 피커리스너
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

            //반납시간 설정이라면
            if (dialogFlag == 1) {
                startYear = year;
                startMonth = monthOfYear;
                startDay = dayOfMonth;
                if (mDay == dayOfMonth) {
                    //최소 한 시간 후 부터 이용 할 수 있도록.
                    if(mMinute>=0 && mMinute<30){
                        timePickerDialog.setMinTime(mHour+1,30,0);
                    }else{
                        timePickerDialog.setMinTime(mHour+2,0,0);
                    }
                } else {
                    timePickerDialog.setMinTime(8,0,0);
                    timePickerDialog.setStartTime(8,0,0);
                }
            } else if (dialogFlag == 2) {

                endYear = year;
                endMonth = monthOfYear;
                endDay = dayOfMonth;
                if (startDay + 1 == dayOfMonth) {

                    timePickerDialog.setMinTime(startHour,startMinute,0);
                } else {
                    timePickerDialog.setMinTime(8,0,0);
                    timePickerDialog.setStartTime(8,0,0);
                }
            }
            timePickerDialog.show(getFragmentManager(),"TimePickerDialog");

        }
    };
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute , int second) {
            String zerocheck = "";//분이 0분일경우 뒤에 0을 하나 더 붙여준다.
            if (dialogFlag == 1) {
                dialogFlag = 0;


                startHour = hourOfDay;
                startMinute = minute;
                if (startMinute == 0) zerocheck = "0";
                else zerocheck = "";
                tvSearchEnd.setText("");
                mIsEnd = false;
                mIsStart = true;
                tvSearchStart.setText((startMonth + 1) + "월" + startDay + "일 " + startHour + ":" + startMinute + zerocheck);

            } else if (dialogFlag == 2) {
                dialogFlag = 0;
                mIsEnd = true;
                //최소시간 설정
                endHour = hourOfDay;
                endMinute = minute;
                if (endMinute == 0) zerocheck = "0";
                else zerocheck = "";
                //두 날짜의 차이가1일이 나는지 체크한다.
                checkMinDate();
                //+1일의 연산이 정상적으로 수행하되도록 체크한다.
                checkDate();
                tvSearchEnd.setText((endMonth + 1) + "월" + endDay + "일 " + endHour + ":" + endMinute + zerocheck);
            }
        }
    };

    public void checkMinDate() {

    }

    public void checkDate() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {

                case CARDIALOG_CODE:
                    tvSearchCarModel.setText(data.getStringExtra("car_name").toString());
                    isCarModelList = data.getBooleanArrayExtra("isCheckedList");
                    break;
                case OPTIONDIALOG_CODE:

                    break;

                case SEARCH_SUBWAY:
                    tvSearchSubway.setText(data.getStringExtra("SubwayName"));
                    //data.getStringExtra("SubwayId");
                    tvSearchSubway.setTextColor(getResources().getColor(R.color.colorBlack));
                    mIsSubway=true;
                    break;

            }
    }

    public void setupBinding() {
        optionDrawable = getResources().obtainTypedArray(R.array.option_drawable);


        optionStringKo = getResources().getStringArray(R.array.option_string_ko);
        optionStringEn = getResources().getStringArray(R.array.option_string_en);
        for(int i=0; i<optionStringKo.length; i++) {
            Log.d("SearchFragment, Array", optionDrawable.getResourceId(i,-1) + " = ");
        }
        modelsDrawable = getResources().getIntArray(R.array.car_models_drawable);
        modelsString = getResources().getStringArray(R.array.car_models);
        modelsStringEn = getResources().getStringArray(R.array.car_models_en);

        btSearch = (Button) mView.findViewById(R.id.bt_search);
        rvOption = (RecyclerView) mView.findViewById(R.id.rv_search_option);
        lmOption = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        tvSearchEnd = (TextView) mView.findViewById(R.id.tv_search_end);
        tvSearchStart = (TextView) mView.findViewById(R.id.tv_search_start);
        rvOption.setLayoutManager(lmOption);
        tbResult = (Toolbar) getActivity().findViewById(R.id.introtoolbar);
        tvResult = (TextView) tbResult.findViewById(R.id.tv_toolbar);
        rlSearchStart = (RelativeLayout) mView.findViewById(R.id.rl_search_start);
        rlSearchEnd = (RelativeLayout) mView.findViewById(R.id.rl_search_end);
        rlSearchCarModel = (RelativeLayout) mView.findViewById(R.id.rl_search_car_model);
        tvSearchCarModel = (TextView) mView.findViewById(R.id.tv_search_car_model);
        tvSearchSubway = (TextView) mView.findViewById(R.id.tv_search_subway);
        rlSearchSubway = (RelativeLayout) mView.findViewById(R.id.rl_search_subway);

    }
}



