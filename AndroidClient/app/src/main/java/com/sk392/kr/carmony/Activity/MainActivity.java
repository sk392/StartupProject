package com.sk392.kr.carmony.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;
import com.sk392.kr.carmony.Adapter.MainPagerAdapter;
import com.sk392.kr.carmony.Fragment.ChatListFragment;
import com.sk392.kr.carmony.Fragment.OwnerRootFragment;
import com.sk392.kr.carmony.Fragment.ReservationFragment;
import com.sk392.kr.carmony.Fragment.SearchRootFragment;
import com.sk392.kr.carmony.Fragment.UserinfoFragment;
import com.sk392.kr.carmony.Library.BackPressClosehHandler;
import com.sk392.kr.carmony.Library.NonSwipeViewPager;
import com.sk392.kr.carmony.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* 주석 다는 예
 * @author 계발자
 * @deprecated 클래스나 멤버가 사장되었는지 지정
 * {@docRoot} 현 문서의 루트 디렉토리 경로 지정
 * @exception 메소드에서의 예외 확인
 * {@inhreitDoc} 상위 클래스로부터 주석을 상속
 * {@link} 다른 주제에 관한 인라인 링크 삽입
 * {@linkplain} 다른 주제에 관한 인라인 링크 삽입 - 링크가 평문 텍스트로 표시
 * @param 메소드의 매개변수 문서화
 * @return 메소드의 반환값 문서화
 * @see 다른 주제에 관한 링크 지정
 * @serial 기본 직렬화 필드 문서화
 * @serialData writeObject() 나 writeExteral() 메소드로 기록한 데이타 문서화
 * @serialField ObjectStreamField 컴포넌트 문서화
 * @since 특별한 변경 사항이 소개될때 릴리즈 기록
 * @throws 메소드에서의 예외 확인
 * {@value} static 필드여야 하는 상수값 표시
 * @version 클래스의 버전
 */

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    boolean isMain = true;
    MainPagerAdapter adapter;
    NonSwipeViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    private static final String TAG = "MainActivity";

    OwnerRootFragment ownerRootFragment;
    ReservationFragment resFragment;
    SearchRootFragment rootFragment;
    UserinfoFragment userinfoFragment;
    ChatListFragment chatListFragment;
    ImageButton ibToolbarReservation;
    ImageButton ibToolbarUser, ibToolbarOwner, ibToolbarCarinfo;
    TextView tvTitle;
    SharedPreferences sharedPreferences;
    private BackPressClosehHandler backPressClosehHandler;

    public static void showToast(Context context, String content) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.view_toast_layout_main, null, false);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast_layout_main_message);
        text.setText(content);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showLongToast(Context context, String content) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.view_toast_layout_main, null, false);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast_layout_main_message);
        text.setText(content);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        backPressClosehHandler = new BackPressClosehHandler(this);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_main_name), Context.MODE_PRIVATE);
        final String ownerType = sharedPreferences.getString(getString(R.string.shared_type), "");
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        toolbar.setTitle("");
        ibToolbarReservation = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_reservation);
        ibToolbarUser = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_user);
        ibToolbarOwner = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_owner);
        ibToolbarOwner.setVisibility(View.GONE);
        ibToolbarCarinfo = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_carinfo);
        tvTitle = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        tvTitle.setText("예약하기");
        viewPager = (NonSwipeViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager();
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.search_on);
        tabLayout.getTabAt(1).setIcon(R.drawable.calender_off);
        tabLayout.getTabAt(2).setIcon(R.drawable.massenger_off);
        tabLayout.getTabAt(3).setIcon(R.drawable.car_off);
        tabLayout.getTabAt(4).setIcon(R.drawable.person_off);
        new BottomDialog.Builder(MainActivity.this)
                .setTitle("오픈베타 테스트")
                .setContent("현재 서비스는 오픈베타 테스트 중이므로 실제 차량을 대여하실 수 없습니다.")
                .setCancelable(true)
                .setPositiveText("예")
                .setPositiveTextColorResource(R.color.white)
                .setPositiveBackgroundColorResource(R.color.reddish_orange)
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull BottomDialog bottomDialog) {

                    }
                }).show();
        //탭 화면이 바뀔 때를 인식하는 리스너
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //다른 화면에 넘어가면 앱바를 확장시킨다.
                appBarLayout.setExpanded(true);
                //탭을 눌렀을 때 화면도 바꿔준다.
                viewPager.setCurrentItem(tab.getPosition());
                getSupportFragmentManager().popBackStack();
                tabLayout.getTabAt(0).setIcon(R.drawable.search_off);
                tabLayout.getTabAt(1).setIcon(R.drawable.calender_off);
                tabLayout.getTabAt(2).setIcon(R.drawable.massenger_off);
                tabLayout.getTabAt(3).setIcon(R.drawable.car_off);
                tabLayout.getTabAt(4).setIcon(R.drawable.person_off);
                if (tab.getPosition() == 0) {
                    tab.setIcon(R.drawable.search_on);
                    tvTitle.setText(R.string.search_fage_name);
                    ibToolbarReservation.setVisibility(View.GONE);
                    ibToolbarUser.setVisibility(View.GONE);
                    ibToolbarOwner.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    tab.setIcon(R.drawable.calender_on);
                    ibToolbarUser.setVisibility(View.GONE);
                    ibToolbarOwner.setVisibility(View.GONE);
                    if (sharedPreferences.getString(getString(R.string.shared_type), "").equals("1")) {
                        ibToolbarReservation.setVisibility(View.VISIBLE);

                    } else {
                        ibToolbarReservation.setVisibility(View.GONE);

                    }
                    if (resFragment.getisOwner()) {
                        tvTitle.setText(R.string.reservation_fage_name_owner);
                    } else {
                        tvTitle.setText(R.string.reservation_fage_name);

                    }
                } else if (tab.getPosition() == 2) {
                    tab.setIcon(R.drawable.massenger_on);

                    tvTitle.setText(R.string.message);
                    ibToolbarReservation.setVisibility(View.GONE);
                    ibToolbarUser.setVisibility(View.GONE);
                    ibToolbarOwner.setVisibility(View.GONE);
                } else if (tab.getPosition() == 3) {
                    tab.setIcon(R.drawable.car_on);
                    if (ownerType.equals("1"))
                        ibToolbarOwner.setVisibility(View.VISIBLE);
                    //조건문 걸어서 차주등록이 되어있으면 설정창 보이게 아니면 안보이게!
                    ibToolbarOwner.setBackgroundResource(R.drawable.setting_btn);
                    ibToolbarUser.setVisibility(View.GONE);
                    tvTitle.setText(R.string.owner_fage_name);
                    ibToolbarReservation.setVisibility(View.GONE);
                } else if (tab.getPosition() == 4) {
                    tab.setIcon(R.drawable.person_on);
                    ibToolbarReservation.setVisibility(View.GONE);
                    ibToolbarUser.setBackgroundResource(R.drawable.setting_btn);
                    ibToolbarUser.setVisibility(View.VISIBLE);
                    tvTitle.setText(R.string.profile);
                    ibToolbarOwner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setupViewPager() {
        adapter = new MainPagerAdapter(getFragmentManager());
        ownerRootFragment = new OwnerRootFragment();
        resFragment = new ReservationFragment();
        rootFragment = new SearchRootFragment();
        userinfoFragment = new UserinfoFragment();
        chatListFragment = new ChatListFragment();
        //각각의 프래그먼트를 어뎁터에 추가해주고 뷰페이저에 해당 어뎁터를 뷰페이저에 매칭한다.
        adapter.addFragment(rootFragment);
        adapter.addFragment(resFragment);
        adapter.addFragment(chatListFragment);
        adapter.addFragment(ownerRootFragment);
        adapter.addFragment(userinfoFragment);
        viewPager.setAdapter(adapter);
        //양 옆으로 미리 불러오고 싶은 페이지 수
        viewPager.setOffscreenPageLimit(4);

    }

    @Override
    public void onBackPressed() {
        if (isMain) {
            backPressClosehHandler.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabLayout.getTabAt(viewPager.getCurrentItem()).select();

        //resultdetail에서 예약 버튼을 누르면 채팅창 페이지로 이동한다.
        if (getIntent().getStringExtra("result") != null) {
            if (getIntent().getStringExtra("result").equals("SearchResultDetailActivity")) {
                tabLayout.getTabAt(2).select();
                //여기서 user_id와 user_name은 차주의 (요청 받는 입장의) 데이터이다.
                String[] USER_IDS = {"4", getIntent().getStringExtra("owner_id")};
                String reservationId = getIntent().getStringExtra("reservation_id");
                String reservationDate = getIntent().getStringExtra("reservation_date");
                //오너 아이디 | 확정버튼 여부 (1이면 true 0이면 false) | 예약번호
                String ChannelData = getIntent().getStringExtra("owner_id") + "|" + "0" + "|" + reservationId;
                //user_ids,
                GroupChannel.createChannelWithUserIds(Arrays.asList(USER_IDS), false, reservationDate, null, ChannelData, new GroupChannel.GroupChannelCreateHandler() {
                    @Override
                    public void onResult(final GroupChannel groupChannel, SendBirdException e) {
                        if (e != null) {
                            // Error.
                            e.printStackTrace();
                            return;
                        } else {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("owner_id", getIntent().getStringExtra("owner_id"));
                            map.put("is_check", "0");
                            map.put("reservation_id", getIntent().getStringExtra("reservation_id"));
                            map.put("is_pay", "0");
                            // Not Error
                            groupChannel.createMetaData(map, new BaseChannel.MetaDataHandler() {
                                @Override
                                public void onResult(Map<String, String> map, SendBirdException e) {

                                }
                            });
                            groupChannel.sendUserMessage("예약 요청 정보"
                                    +"\n대여 차량 : "+getIntent().getStringExtra("car_name")
                                    +"\n대여 기간 : "+getIntent().getStringExtra("reservation_date")
                                    +"\n대여 장소 : "+getIntent().getStringExtra("location"), null, new BaseChannel.SendUserMessageHandler() {
                                @Override
                                public void onSent(UserMessage userMessage, SendBirdException e) {
                                    if (e != null) {
                                        // Error.
                                        return;
                                    }
                                }
                            });
                            showLongToast(MainActivity.this, "예약요청이 완료되었습니다. \n차주와의 대화를 통해 예약을 완료하고 결제를 진행하세요!");
                        }
                    }
                });
                getIntent().removeExtra("result");
            }

        }

    }

    public static void showFCMToast(Context context, String content) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.view_toast_layout_fcm_message, null, false);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast_layout_fcm_message);
        text.setText(content);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showFCMLongToast(Context context, String content) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.view_toast_layout_fcm_message, null, false);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast_layout_fcm_message);
        text.setText(content);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }

}