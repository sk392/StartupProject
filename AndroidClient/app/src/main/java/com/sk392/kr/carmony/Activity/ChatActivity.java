package com.sk392.kr.carmony.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.google.firebase.crash.FirebaseCrash;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sk392.kr.carmony.Library.SendBirdHelper;
import com.sk392.kr.carmony.Library.SendPost;
import com.sk392.kr.carmony.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Sendbird의 모듈을 사용하고 Sendbird를 통해 직접적으로 채팅을 할 수 있는 액티비티
 *
 * <pre>
 * <b>History:</b>
 *    Latte, 1.0, 2017.03.06 초기작성
 * </pre>
 *
 * @author Latte
 */

public class ChatActivity extends FragmentActivity {
    private SendBirdChatFragment mSendBirdMessagingFragment;

    private static final String TAG = "ChatActivity";


    private View mTopBarContainer;
    private View mSettingsContainer;
    private String strChannelUrl;
    private String strUserName;
    private String strIsCarmony;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.sendbird_slide_in_from_bottom, R.anim.sendbird_slide_out_to_top);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initFragment();
    }



    @Override
    protected void onPause() {
        super.onPause();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityPausedForOldAndroids();
    }

    //앱의 방향(가로 || 세로) 이 바뀌었을 때 감지하는 메소드.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeMenubar();//방향이 바뀌면 메뉴바 사이즈를 바꿔준다.
    }

    //메뉴바 사이즈를 리사이징하는 메소드
    private void resizeMenubar() {
        ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            lp.height = (int) (48 * getResources().getDisplayMetrics().density);
        }
        mTopBarContainer.setLayoutParams(lp);
    }

    //채팅방이 종료되었을 때 호출되는 메소드
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.sendbird_slide_in_from_top, R.anim.sendbird_slide_out_to_bottom);
    }

    //Fragment 초기화 하는 메소드
    private void initFragment() {
        mSendBirdMessagingFragment = new SendBirdChatFragment(); //Fragment 초기화
        strChannelUrl = getIntent().getStringExtra("channel_url"); //보여줄 채팅방 url(Group Channel 객체를 만들 때 사용한다.
        strUserName = getIntent().getStringExtra("user_name"); //채팅방 제목을 보여주기 위해서
        strIsCarmony = getIntent().getStringExtra("is_carmony"); //채팅방 제목을 보여주기 위해서
//        Log.d(TAG,"is_carmony :" + strIsCarmony);
        mSendBirdMessagingFragment.setArguments(getIntent().getExtras()); //Fragment에 Extra 데이터 전달.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mSendBirdMessagingFragment)
                .commit(); //초기화한 프레그먼트를 보여준다.

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SendBirdHelper.MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
//                Log.d(TAG,"onRequestPermissionsResult default");
                break;
        }
    }


    /*
     *
     * Sendbird 내부에 있는 fragment
    */
    public static class SendBirdChatFragment extends Fragment {
        private static final int REQUEST_PICK_IMAGE = 100;
        private static final int REQUEST_INVITE_USERS = 200;
        private static final String identifier = "SendBirdGroupChat";
        private Toolbar tbChat;
        private TextView tvToolbar, tvTopMessage;
        private String strResOwnerId,strResReservationId,strResIsCheck,strResIsPay;
        private SharedPreferences sharedPreferences;
        private ListView lvChat;
        private SendBirdMessagingAdapter mAdapter;
        private EditText etChatMessage;
        private Button btChatSend , btToolbar;
        private ImageButton ibChatUpload;
        private ProgressBar pbChatUpload;

        private String strChannelUrl,strUserName,strIsCarmony;
        private GroupChannel gcChat;
        private PreviousMessageListQuery mPrevMessageListQuery;
        private boolean mIsUploading;

        public SendBirdChatFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

            strChannelUrl = getArguments().getString("channel_url");
            strUserName = getArguments().getString("user_name");
            strIsCarmony = getArguments().getString("is_carmony");
            sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name),MODE_PRIVATE);
            tbChat = (Toolbar) rootView.findViewById(R.id.introtoolbar);
            tvToolbar = (TextView) tbChat.findViewById(R.id.tv_toolbar);
            tvTopMessage = (TextView)rootView.findViewById(R.id.tv_chat_top_message);
            btToolbar = (Button)tbChat.findViewById(R.id.bt_toolbar_chat);

            //카모니와의 대화방의 경우
            if(strIsCarmony!=null&&strIsCarmony.equals("y")) {
                btToolbar.setVisibility(View.GONE);
                tvTopMessage.setText("카모니에게 무엇이든 물어보세요!");
            }else{
                tvTopMessage.setText("예약이 확정되어야 결제를 진행할 수 있습니다.");
                btToolbar.setVisibility(View.VISIBLE);
            }
            initUIComponents(rootView);
            return rootView;
        }

        private void initGroupChannel() {
            GroupChannel.getChannel(strChannelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        return;
                    }
                    gcChat = groupChannel;
                    gcChat.markAsRead();
                    List<String> keys = new ArrayList<String>();
                    keys.add("owner_id");
                    keys.add("is_check");
                    keys.add("reservation_id");
                    keys.add("is_pay");

                    mapDataUpdate(keys);

                    mAdapter = new SendBirdMessagingAdapter(getActivity(), gcChat);
                    lvChat.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                    updateGroupChannelTitle();

                    loadPrevMessages(true);
                }
            });
            //여기서 결제관련된 어쩌구 저쩌구 하면될듯.!


        }


        private void updateGroupChannelTitle() {
            tvToolbar.setText(strUserName);
        }


        @Override
        public void onPause() {
            super.onPause();
            if (!mIsUploading) {
                SendBird.removeChannelHandler(identifier);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (!mIsUploading) {
                SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
                    @Override
                    public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                        if (baseChannel.getUrl().equals(strChannelUrl)) {
                            if (mAdapter != null) {
                                gcChat.markAsRead();
                                mAdapter.appendMessage(baseMessage);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onReadReceiptUpdated(GroupChannel groupChannel) {
                        if (groupChannel.getUrl().equals(strChannelUrl)) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onTypingStatusUpdated(GroupChannel groupChannel) {
                        if (groupChannel.getUrl().equals(strChannelUrl)) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onUserJoined(GroupChannel groupChannel, User user) {
                        if (groupChannel.getUrl().equals(strChannelUrl)) {
                            //                          updateGroupChannelTitle();
                        }
                    }

                    @Override
                    public void onUserLeft(GroupChannel groupChannel, User user) {
                        if (groupChannel.getUrl().equals(strChannelUrl)) {
//                            updateGroupChannelTitle();
                        }
                    }
                });

                initGroupChannel();
            } else {
                mIsUploading = false;

                /**
                 * Set this as true to restart auto-background detection,
                 * when your Activity is shown again after the external Activity is finished.
                 */
                SendBird.setAutoBackgroundDetection(true);
            }
            tbChat.setNavigationIcon(R.drawable.arrow_big_navy);
            tbChat.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            //툴바 버튼 관리
            btToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SendPost sendPost = new SendPost(getActivity());
                    sendPost.setUrl(getString(R.string.url_get_reservation_state));
                    sendPost.setCallbackEvent(new SendPost.CallbackEvent() {
                        @Override
                        public void getResult(String result) {
                            try {
                                String err,startDate ="";
                                JSONObject jsonResult;

                                jsonResult = new JSONObject(result);

                                err = jsonResult.getString("err");
                                if(err.equals("0")){
                                    startDate = jsonResult.getJSONObject("ret").getString("start_date");

                                    SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

                                    Date day1 = null;
                                    Date day2 = new Date();
                                    try {
                                        day1 = format.parse(startDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if(jsonResult.getJSONObject("ret").getString("state").equals("3")){
                                        MainActivity.showToast(getActivity(),"해당 예약이 취소되었습니다.");
                                    }else if( day1.compareTo( day2 ) == -1){
                                        //현재 시간보다 예약 시간이 지난경우는 결제 불가.
                                        MainActivity.showToast(getActivity(),"기한이 지나 예약이 취소되었습니다.");
                                    }else{
                                        //예약이 취소되지 않았다면
                                        List<String> keys = new ArrayList<String>();
                                        keys.add("owner_id");
                                        keys.add("is_check");
                                        keys.add("reservation_id");
                                        keys.add("is_pay");
                                        mapDataUpdate(keys);

                                        if(strResOwnerId.equals(sharedPreferences.getString(getString(R.string.shared_userid),""))){
                                            //오너라면 확정 버튼
                                            if(strResIsCheck.equals("0")) {
                                                new BottomDialog.Builder(getActivity())
                                                        .setTitle("확정")
                                                        .setContent("해당 대여를 확정하시겠습니까? 드라이버는 확정한 후에 결제할 수 있습니다.")
                                                        .setCancelable(true)
                                                        .setNegativeText("취소")
                                                        .setPositiveText("예")
                                                        .setPositiveTextColorResource(R.color.white)
                                                        .setPositiveBackgroundColorResource(R.color.reddish_orange)
                                                        .onPositive(new BottomDialog.ButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull BottomDialog bottomDialog) {
                                                                HashMap<String, String> map = new HashMap<>();
                                                                map.put("owner_id", strResOwnerId);
                                                                strResIsCheck = "1";
                                                                map.put("is_check", "1");
                                                                map.put("reservation_id", strResReservationId);
                                                                map.put("is_pay", strResIsPay);
                                                                // Not Error
                                                                gcChat.updateMetaData(map, new BaseChannel.MetaDataHandler() {
                                                                    @Override
                                                                    public void onResult(Map<String, String> map, SendBirdException e) {
                                                                        MainActivity.showToast(getActivity().getApplicationContext(), "예약이 확정되었습니다.");

                                                                    }
                                                                });
                                                            }
                                                        })
                                                        .show();


                                            }else{
                                                Intent intent = new Intent();
                                                intent.setClass(getActivity().getApplicationContext(),ChatPaymentActivity.class);
                                                intent.putExtra("is_check",strResIsCheck);
                                                intent.putExtra("channel_url",strChannelUrl);
                                                intent.putExtra("reservation_id",strResReservationId);
                                                intent.putExtra("is_pay",strResReservationId);
                                                intent.putExtra("owner_id",strResOwnerId);
                                                intent.putExtra("user_name",strUserName);
                                                intent.putExtra("is_pay",strResIsPay);
                                                intent.putExtra("is_owner", "1");//오너인 경우 1 아닌 경우 0
                                                startActivity(intent);
                                            }
                                        }else{
                                            if(strResIsCheck.equals("1")) {
                                                //드라이버라면 결제페이지로넘어가도록 한다.
                                                Intent intent = new Intent();
                                                intent.setClass(getActivity().getApplicationContext(),ChatPaymentActivity.class);
                                                intent.putExtra("is_check",strResIsCheck);
                                                intent.putExtra("channel_url",strChannelUrl);
                                                intent.putExtra("reservation_id",strResReservationId);
                                                intent.putExtra("is_pay",strResReservationId);
                                                intent.putExtra("owner_id",strResOwnerId);
                                                intent.putExtra("user_name",strUserName);
                                                intent.putExtra("is_owner", "0");//오너인 경우 1 아닌 경우 0
                                                startActivity(intent);
                                            }else{
                                                MainActivity.showToast(getActivity().getApplicationContext(),"확정전 입니다. 차주가 확정을 눌러야 결제가 진행됩니다.");
                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrash.logcat(Log.ERROR, TAG, "getResult to json parse Fail");
                                FirebaseCrash.report(e);


                            }
                        }
                    });
                    sendPost.setLoadingImg(false);
                    sendPost.execute("sktoken=" + sharedPreferences.getString(getString(R.string.shared_token), "")
                            + "&reservation_id=" + strResReservationId);




                }
            });

        }

        private void initUIComponents(View rootView) {
            lvChat = (ListView) rootView.findViewById(R.id.list);
            turnOffListViewDecoration(lvChat);

            btChatSend = (Button) rootView.findViewById(R.id.btn_send);
            ibChatUpload = (ImageButton) rootView.findViewById(R.id.btn_upload);
            pbChatUpload = (ProgressBar) rootView.findViewById(R.id.progress_btn_upload);
            etChatMessage = (EditText) rootView.findViewById(R.id.etxt_message);

            btChatSend.setEnabled(false);
            btChatSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send();
                }
            });

            ibChatUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SendBirdHelper.requestReadWriteStoragePermissions(getActivity())) {
                        mIsUploading = true;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);

                        /**
                         * Set this as false to maintain SendBird connection,
                         * even when an external Activity is started.
                         */
                        SendBird.setAutoBackgroundDetection(false);
                    }
                }
            });

            etChatMessage.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            send();
                        }
                        return true; // Do not hide keyboard.
                    }

                    return false;
                }
            });
            etChatMessage.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            etChatMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    btChatSend.setEnabled(s.length() > 0);

                    if (s.length() == 1) {
                        gcChat.startTyping();
                    } else if (s.length() <= 0) {
                        gcChat.endTyping();
                    }
                }
            });

            lvChat.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    SendBirdHelper.hideKeyboard(getActivity());
                    return false;
                }
            });
            lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (view.getFirstVisiblePosition() == 0 && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
                            loadPrevMessages(false);
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        }

        private void loadPrevMessages(final boolean refresh) {
            if (gcChat == null) {
                return;
            }

            if (refresh || mPrevMessageListQuery == null) {
                mPrevMessageListQuery = gcChat.createPreviousMessageListQuery();
            }

            if (mPrevMessageListQuery.isLoading()) {
                return;
            }

            if (!mPrevMessageListQuery.hasMore()) {
                return;
            }

            mPrevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
                @Override
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (refresh) {
                        mAdapter.clear();
                    }

                    for (BaseMessage message : list) {
                        mAdapter.insertMessage(message);
                    }
                    mAdapter.notifyDataSetChanged();
                    lvChat.setSelection(list.size());
                }
            });
        }

        private void showUploadProgress(boolean tf) {
            if (tf) {
                ibChatUpload.setEnabled(false);
                ibChatUpload.setVisibility(View.INVISIBLE);
                pbChatUpload.setVisibility(View.VISIBLE);
            } else {
                ibChatUpload.setEnabled(true);
                ibChatUpload.setVisibility(View.VISIBLE);
                pbChatUpload.setVisibility(View.GONE);
            }
        }

        private void turnOffListViewDecoration(ListView listView) {
            listView.setDivider(null);
            listView.setDividerHeight(0);
            listView.setHorizontalFadingEdgeEnabled(false);
            listView.setVerticalFadingEdgeEnabled(false);
            listView.setHorizontalScrollBarEnabled(false);
            listView.setVerticalScrollBarEnabled(true);
            listView.setSelector(new ColorDrawable(0x00ffffff));
            listView.setCacheColorHint(0x00000000); // For Gingerbread scrolling bug fix
        }

        private void invite(String[] userIds) {
            gcChat.inviteWithUserIds(Arrays.asList(userIds), new GroupChannel.GroupChannelInviteHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                    upload(data.getData());
                } else if (requestCode == REQUEST_INVITE_USERS) {
                    String[] userIds = data.getStringArrayExtra("user_ids");
                    invite(userIds);
                }
            }
        }

        private void send() {
            if (etChatMessage.getText().length() <= 0) {
                return;
            }

            gcChat.sendUserMessage(etChatMessage.getText().toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAdapter.appendMessage(userMessage);
                    mAdapter.notifyDataSetChanged();

                    etChatMessage.setText("");
                }
            });

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                SendBirdHelper.hideKeyboard(getActivity());
            }
        }

        private void upload(Uri uri) {
            Hashtable<String, Object> info = SendBirdHelper.getFileInfo(getActivity(), uri);
            final String path = (String) info.get("path");
            final File file = new File(path);
            final String name = file.getName();
            final String mime = (String) info.get("mime");
            final int size = (Integer) info.get("size");

//                Toast.makeText(getActivity(), "Uploading file must be located in local storage.", Toast.LENGTH_LONG).show();
                showUploadProgress(true);
                gcChat.sendFileMessage(file, name, mime, size, "", new BaseChannel.SendFileMessageHandler() {
                    @Override
                    public void onSent(FileMessage fileMessage, SendBirdException e) {
                        showUploadProgress(false);
                        if (e != null) {
//                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mAdapter.appendMessage(fileMessage);
                        mAdapter.notifyDataSetChanged();
                    }
                });
        }

        private void mapDataUpdate(List<String> keys){
            gcChat.getMetaData(keys, new BaseChannel.MetaDataHandler() {
                @Override
                public void onResult(Map<String, String> map, SendBirdException e) {
                    if (e != null) {
                        //error
                    } else {
                        strResOwnerId = map.get("owner_id");
                        strResIsCheck = map.get("is_check");
                        strResReservationId =map.get("reservation_id");
                        strResIsPay =map.get("is_pay");
                    }
                }
            });

        }
    }

    public static class SendBirdMessagingAdapter extends BaseAdapter {
        private static final int TYPE_UNSUPPORTED = 0;
        private static final int TYPE_USER_MESSAGE = 1;
        private static final int TYPE_ADMIN_MESSAGE = 2;
        private static final int TYPE_FILE_MESSAGE = 3;
        private static final int TYPE_TYPING_INDICATOR = 4;

        private final Context mContext;
        private final LayoutInflater mInflater;
        private final ArrayList<Object> mItemList;
        private final GroupChannel gcChat;

        public SendBirdMessagingAdapter(Context context, GroupChannel channel) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
            gcChat = channel;
        }

        @Override
        public int getCount() {
            return mItemList.size() + (gcChat.isTyping() ? 1 : 0);
        }

        @Override
        public Object getItem(int position) {
            if (position >= mItemList.size()) {
                List<User> members = gcChat.getTypingMembers();
                ArrayList<String> names = new ArrayList<>();
                for (User member : members) {
                    names.add(member.getNickname());
                }

                return names;
            }
            return mItemList.get(position);
        }

        public void delete(Object object) {
            mItemList.remove(object);
        }

        public void clear() {
            mItemList.clear();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void insertMessage(BaseMessage message) {
            mItemList.add(0, message);
        }

        public void appendMessage(BaseMessage message) {
            mItemList.add(message);
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= mItemList.size()) {
                return TYPE_TYPING_INDICATOR;
            }

            Object item = mItemList.get(position);
            if (item instanceof UserMessage) {
                return TYPE_USER_MESSAGE;
            } else if (item instanceof FileMessage) {
                return TYPE_FILE_MESSAGE;
            } else if (item instanceof AdminMessage) {
                return TYPE_ADMIN_MESSAGE;
            }

            return TYPE_UNSUPPORTED;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final Object item = getItem(position);

            if (convertView == null || ((ViewHolder) convertView.getTag()).getViewType() != getItemViewType(position)) {
                viewHolder = new ViewHolder();
                viewHolder.setViewType(getItemViewType(position));
                TextView tv;
                ImageView iv;
                View v;

                switch (getItemViewType(position)) {

                    case TYPE_UNSUPPORTED:
                        convertView = new View(mInflater.getContext());
                        convertView.setTag(viewHolder);
                        break;

                    case TYPE_USER_MESSAGE:


                        convertView = mInflater.inflate(R.layout.item_chat_group_user_message, parent, false);

                        v = convertView.findViewById(R.id.left_container);
                        viewHolder.setView("left_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                        viewHolder.setView("left_thumbnail", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left);
                        viewHolder.setView("left_message", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_name);
                        viewHolder.setView("left_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_time);
                        viewHolder.setView("left_time", tv);

                        v = convertView.findViewById(R.id.right_container);
                        viewHolder.setView("right_container", v);
                        tv = (TextView) convertView.findViewById(R.id.txt_right);
                        viewHolder.setView("right_message", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                        viewHolder.setView("right_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_time);
                        viewHolder.setView("right_time", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_status);
                        viewHolder.setView("right_status", tv);

                        convertView.setTag(viewHolder);
                        break;

                    case TYPE_ADMIN_MESSAGE:
                        convertView = mInflater.inflate(R.layout.item_chat_admin_message, parent, false);
                        viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                        convertView.setTag(viewHolder);
                        break;

                    case TYPE_FILE_MESSAGE:

                        convertView = mInflater.inflate(R.layout.item_chat_group_file_message, parent, false);

                        v = convertView.findViewById(R.id.left_container);
                        viewHolder.setView("left_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                        viewHolder.setView("left_thumbnail", iv);
                        iv = (ImageView) convertView.findViewById(R.id.img_left);
                        viewHolder.setView("left_image", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_name);
                        viewHolder.setView("left_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_time);
                        viewHolder.setView("left_time", tv);

                        v = convertView.findViewById(R.id.right_container);
                        viewHolder.setView("right_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_right);
                        viewHolder.setView("right_image", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                        viewHolder.setView("right_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_time);
                        viewHolder.setView("right_time", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_status);
                        viewHolder.setView("right_status", tv);

                        convertView.setTag(viewHolder);
                        break;

                    case TYPE_TYPING_INDICATOR:
                        convertView = mInflater.inflate(R.layout.view_group_typing_indicator, parent, false);
                        viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                        convertView.setTag(viewHolder);
                        break;
                    default:
                        break;
                }
            }

            viewHolder = (ViewHolder) convertView.getTag();
            switch (getItemViewType(position)) {
                case TYPE_UNSUPPORTED:
                    break;
                case TYPE_USER_MESSAGE:
                    UserMessage message = (UserMessage) item;
                    if (message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                        viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                        viewHolder.getView("right_name", TextView.class).setText(message.getSender().getNickname());
                        viewHolder.getView("right_message", TextView.class).setText(message.getMessage());
                        viewHolder.getView("right_time", TextView.class).setText(SendBirdHelper.getDisplayDateTime(mContext, message.getCreatedAt()));

                        int unreadCount = gcChat.getReadReceipt(message);
                        viewHolder.getView("right_status", TextView.class).setText(getUnreadCount(unreadCount));



                    } else {
                        viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                        SendBirdHelper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), message.getSender().getProfileUrl(), true);
                        viewHolder.getView("left_name", TextView.class).setText(message.getSender().getNickname());
                        viewHolder.getView("left_message", TextView.class).setText(message.getMessage());
                        viewHolder.getView("left_time", TextView.class).setText(SendBirdHelper.getDisplayDateTime(mContext, message.getCreatedAt()));
                    }
                    break;
                case TYPE_ADMIN_MESSAGE:
                    AdminMessage adminMessage = (AdminMessage) item;
                    viewHolder.getView("message", TextView.class).setText(Html.fromHtml(adminMessage.getMessage()));
                    break;
                case TYPE_FILE_MESSAGE:
                    final FileMessage fileLink = (FileMessage) item;

                    if (fileLink.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                        viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                        viewHolder.getView("right_name", TextView.class).setText(fileLink.getSender().getNickname());
                        if (fileLink.getType().toLowerCase().startsWith("image")) {
                            SendBirdHelper.displayUrlImage(viewHolder.getView("right_image", ImageView.class), fileLink.getUrl());
                        } else {
                            viewHolder.getView("right_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                        }
                        viewHolder.getView("right_time", TextView.class).setText(SendBirdHelper.getDisplayDateTime(mContext, fileLink.getCreatedAt()));

                        int unreadCount = gcChat.getReadReceipt(fileLink);
                        viewHolder.getView("right_status", TextView.class).setText(getUnreadCount(unreadCount));

                        viewHolder.getView("right_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Carmony")
                                        .setMessage("다운로드 하시겠습니까?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    SendBirdHelper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    FirebaseCrash.logcat(Log.ERROR, TAG, "Chat Download Fail");
                                                    FirebaseCrash.report(e);

                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create()
                                        .show();
                            }
                        });
                    } else {
                        viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                        SendBirdHelper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), fileLink.getSender().getProfileUrl(), true);
                        viewHolder.getView("left_name", TextView.class).setText(fileLink.getSender().getNickname());
                        if (fileLink.getType().toLowerCase().startsWith("image")) {
                            SendBirdHelper.displayUrlImage(viewHolder.getView("left_image", ImageView.class), fileLink.getUrl());
                        } else {
                            viewHolder.getView("left_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                        }
                        viewHolder.getView("left_time", TextView.class).setText(SendBirdHelper.getDisplayDateTime(mContext, fileLink.getCreatedAt()));

                        viewHolder.getView("left_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Carmony")
                                        .setMessage("다운로드 하시겠습니까?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    SendBirdHelper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    FirebaseCrash.logcat(Log.ERROR, TAG, "Chat Download Fail");
                                                    FirebaseCrash.report(e);

                                                }
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create()
                                        .show();
                            }
                        });
                    }
                    break;

                case TYPE_TYPING_INDICATOR: {
                    int itemCount = ((List) item).size();
                    String typeMsg = ((List) item).get(0)
                            + ((itemCount > 1) ? " +" + (itemCount - 1) : "")
                            + ((itemCount > 1) ? " are " : " is ")
                            + "typing...";
                    viewHolder.getView("message", TextView.class).setText(typeMsg);
                    break;
                }
            }

            return convertView;
        }

        static class ViewHolder {
            private Hashtable<String, View> holder = new Hashtable<>();
            private int type;

            public int getViewType() {
                return this.type;
            }

            public void setViewType(int type) {
                this.type = type;
            }

            public void setView(String k, View v) {
                holder.put(k, v);
            }

            public View getView(String k) {
                return holder.get(k);
            }

            public <T> T getView(String k, Class<T> type) {
                return type.cast(getView(k));
            }
        }
        //지금 현재 채팅방엔 카모니 계정이 들어가 있으므로
        private String getUnreadCount(int unreadCount){
            String strUnreadCount ="";
            if (unreadCount > 1) {

                //3명 이상 넣을 일이 없고
                //운영자가 있음을 알리지 않기위해서 1명이 읽지 않아도 없애버린다.
                strUnreadCount = "1";
            } else {
                strUnreadCount = "";
            }
            return strUnreadCount;
        }

    }//SendBIrdMessagingAdapterClass

    @Override
    public void onBackPressed() {
        //푸시 메시지를 통해 올라왔을 경우!
        if(getIntent().getStringExtra("is_message") !=null&&getIntent().getStringExtra("is_message").equals("y")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
        //애니메이션
    }

}
