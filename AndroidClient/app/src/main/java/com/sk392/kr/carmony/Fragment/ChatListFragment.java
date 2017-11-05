package com.sk392.kr.carmony.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sk392.kr.carmony.Activity.ChatActivity;
import com.sk392.kr.carmony.Library.SendBirdHelper;
import com.sk392.kr.carmony.R;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Sendbird모듈을 사용하고, 로그인한 계정의 채팅 리스트를 보여주는 Fragment이다.
 * MainActivity에 속해 있다.
 * <p>
 * <pre>
 * <b>History:</b>
 *    Latte, 1.0, 2017.03.06 초기작성
 * </pre>
 *
 * @author Latte
 */
public class ChatListFragment extends Fragment {
    private SendBirdGroupChannelListFragment mSendBirdGroupChannelListFragment;
    private View mTopBarContainer;
    private View mSettingsContainer;
    private static final String TAG = "ChatListActivity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        initFragment();
        return mView;
    }

    //앱 회전에 따른 메소드
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeMenubar();// 앱이 회전될 때 메뉴바 크기를 리사이즈
    }

    //메뉴바 크기를 리사이즈 하는 메소드
    private void resizeMenubar() {
        //      ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mTopBarContainer.getLayoutParams().height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            mTopBarContainer.getLayoutParams().height = (int) (48 * getResources().getDisplayMetrics().density);
        }
//        mTopBarContainer.setLayoutParams(lp);
    }

    //프래그먼트 초기화
    private void initFragment() {
        mSendBirdGroupChannelListFragment = new SendBirdGroupChannelListFragment();//프래그먼트 선언.
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mSendBirdGroupChannelListFragment)
                .commit();
        //선언한 프래그먼트를 화면에 출력.
    }


    //Sendbird그룹 채널리스트를 보여주는 프래그먼트
    public static class SendBirdGroupChannelListFragment extends Fragment {
        private static final String identifier = "SendBirdGroupChannelList";
        private static final int REQUEST_INVITE_USERS = 100;
        private ListView mListView;
        private SwipeRefreshLayout srlChannelList;
        private SendBirdGroupChannelAdapter mAdapter;
        int userPosition = 0;

        private SharedPreferences sharedPreferences;

        private GroupChannelListQuery mQuery;

        //프래그먼트 생성자.
        public SendBirdGroupChannelListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_group_channel_list, container, false);
            initUIComponents(rootView);
            sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name), Context.MODE_PRIVATE);

            return rootView;
        }

        //Ui
        private void initUIComponents(View rootView) {
            srlChannelList = (SwipeRefreshLayout)rootView.findViewById(R.id.srl_channel_list);
            srlChannelList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mAdapter.clear();
                    mAdapter.notifyDataSetChanged();
                    mQuery = GroupChannel.createMyGroupChannelListQuery();
                    mQuery.setIncludeEmpty(true);
                    loadNextChannels();
                    srlChannelList.setRefreshing(false);
                }
            });
            mListView = (ListView) rootView.findViewById(R.id.list);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GroupChannel channel = mAdapter.getItem(position);
                    for (int i = 0; i < channel.getMembers().size(); i++) {
                        if (!(channel.getMembers().get(i).getUserId().equals("4") || channel.getMembers().get(i).getUserId().equals(sharedPreferences.getString(getActivity().getApplicationContext().getString(R.string.shared_userid), "")))) {
                            //Carmony(4)가 아니고, 나도 아닌 경우.
                            userPosition = i;
                        }

                    }
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if (channel.getMemberCount() == 2) {
                        //카모니상담방 일 경우
                        intent.putExtra("user_name", "Carmony");
                        intent.putExtra("channel_url", channel.getUrl());
                        intent.putExtra("is_carmony", "y");
//                        Log.d(TAG, "is_carmony : y");
                        startActivity(intent);
                    } else {
                        intent.putExtra("user_name", channel.getMembers().get(userPosition).getNickname());
                        intent.putExtra("channel_url", channel.getUrl());
                        intent.putExtra("is_carmony", "N");
                        startActivity(intent);

                    }
                }
            });
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount >= (int) (totalItemCount * 0.8f)) {
                        loadNextChannels();
                    }
                }
            });
            /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final GroupChannel channel = mAdapter.getItem(position);
                    for (int i = 0; i < channel.getMembers().size(); i++) {
                        if (!(channel.getMembers().get(i).getUserId().equals("4") || channel.getMembers().get(i).getUserId().equals(sharedPreferences.getString(getActivity().getApplicationContext().getString(R.string.shared_userid), "")))) {
                            //Carmony 회사 계정(4)가 아니고, 나도 아닌 경우.
                            userPosition = i;
                        }

                    }
                    if()
                    new BottomDialog.Builder(getActivity())
                            .setTitle(channel.getMembers().get(userPosition).getNickname())
                            .setContent("방을 나가시겠습니까?")
                            .setCancelable(true)
                            .setNegativeText("취소")
                            .setPositiveText("예")
                            .setPositiveTextColorResource(R.color.white)
                            .setPositiveBackgroundColorResource(R.color.reddish_orange)
                            .onPositive(new BottomDialog.ButtonCallback() {
                                @Override
                                public void onClick(@NonNull BottomDialog bottomDialog) {
                                    channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
                                        @Override
                                        public void onResult(SendBirdException e) {
                                            if (e != null) {
//                                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            MainActivity.showToast(getActivity(),"방을 나갔습니다");
                                            mAdapter.remove(position);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                            })
                            .show();
                    return true;
                }
            });*/

            mAdapter = new SendBirdGroupChannelAdapter(getActivity());
            mListView.setAdapter(mAdapter);
        }

        private void loadNextChannels() {
            if (mQuery == null || mQuery.isLoading()) {
                return;
            }

            if (!mQuery.hasNext()) {
                return;
            }

            mQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
                @Override
                public void onResult(final List<GroupChannel> list, SendBirdException e) {
                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();


                }
            });
        }


        @Override
        public void onPause() {
            super.onPause();
            SendBird.removeChannelHandler(identifier);
        }

        @Override
        public void onResume() {
            super.onResume();

            SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
                @Override
                public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                    if (baseChannel instanceof GroupChannel) {
                        GroupChannel groupChannel = (GroupChannel) baseChannel;
                        mAdapter.replace(groupChannel);
                    }
                }

                @Override
                public void onUserJoined(GroupChannel groupChannel, User user) {
                    // Member changed. Refresh group channel item.
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onUserLeft(GroupChannel groupChannel, User user) {
                    // Member changed. Refresh group channel item.
                    mAdapter.notifyDataSetChanged();
                }
            });

            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
            mQuery = GroupChannel.createMyGroupChannelListQuery();
            mQuery.setIncludeEmpty(true);
            loadNextChannels();
        }

    }

    private static class SendBirdGroupChannelAdapter extends BaseAdapter {
        private final Context mContext;
        private final LayoutInflater mInflater;
        private final ArrayList<GroupChannel> mItemList;

        private SendBirdGroupChannelAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public GroupChannel getItem(int position) {
            return mItemList.get(position);
        }

        public void clear() {
            mItemList.clear();
        }

        private GroupChannel remove(int index) {
            return mItemList.remove(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private void addAll(List<GroupChannel> channels) {
            mItemList.addAll(channels);
        }

        private void replace(GroupChannel newChannel) {
            for (GroupChannel oldChannel : mItemList) {
                if (oldChannel.getUrl().equals(newChannel.getUrl())) {
                    mItemList.remove(oldChannel);
                    break;
                }
            }

            mItemList.add(0, newChannel);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_group_channel, parent, false);
                viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
                viewHolder.setView("txt_topic", convertView.findViewById(R.id.txt_topic));
                viewHolder.setView("txt_res_date", convertView.findViewById(R.id.txt_res_date));
                viewHolder.setView("txt_unread_count", convertView.findViewById(R.id.txt_unread_count));
                viewHolder.setView("txt_date", convertView.findViewById(R.id.txt_date));
                viewHolder.setView("txt_desc", convertView.findViewById(R.id.txt_desc));

                convertView.setTag(viewHolder);
            }

            GroupChannel item = getItem(position);
            viewHolder = (ViewHolder) convertView.getTag();
            int userPosition = 0;//아무도 지칭하지 않는 포지션. 설정
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.shared_main_name), Context.MODE_PRIVATE);

            for (int i = 0; i < item.getMembers().size(); i++) {
                if (item.getMembers().size() == 2) {
                    if (sharedPreferences.getString(mContext.getString(R.string.shared_userid), "").equals("4")) {
                        //카모니 계정이라면
                        if (!(item.getMembers().get(i).getUserId().equals("4"))) {
                            userPosition = i;
//                            Log.d(TAG,"pptp-1 Position : " + i + " / "+item.getMembers().get(i).getUserId());
                        }
//                        Log.d(TAG,"pptp-2");
                    } else {
                        //카모니 계정이 아니라면
//                        Log.d(TAG,"pptp-3");
                        if (item.getMembers().get(i).getUserId().equals("4")) {
//                            Log.d(TAG,"pptp-4 Position : " + i+ " / "+item.getMembers().get(i).getUserId());
                            userPosition = i;
                        }

                    }
                } else {
//                    Log.d(TAG,"pptp-5");
                    if (!(item.getMembers().get(i).getUserId().equals("4")) && !(item.getMembers().get(i).getUserId().equals(sharedPreferences.getString(mContext.getString(R.string.shared_userid), "")))) {
                        //Carmony(4)가 아니고, 나도 아닌 경우.
//                        Log.d(TAG,"pptp-6 Position : " + i+ " / "+item.getMembers().get(i).getUserId());
                        userPosition = i;
                    }
                }
            }
//            Log.d(TAG,"pptp-7");

            viewHolder.getView("txt_topic", TextView.class).setText(item.getMembers().get(userPosition).getNickname());
            //카모니와의 채팅방의 경우
            if (item.getName().equals("-"))
                viewHolder.getView("txt_res_date", TextView.class).setText("");
            else
                viewHolder.getView("txt_res_date", TextView.class).setText("(" + item.getName() + ")");

            SendBirdHelper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getMembers().get(userPosition).getProfileUrl());

            if (item.getUnreadMessageCount() > 0) {
                viewHolder.getView("txt_unread_count", TextView.class).setVisibility(View.VISIBLE);
                String strCount = item.getUnreadMessageCount() + "";
                viewHolder.getView("txt_unread_count", TextView.class).setText(strCount);
            } else {
                viewHolder.getView("txt_unread_count", TextView.class).setVisibility(View.INVISIBLE);
            }


            BaseMessage message = item.getLastMessage();
            if (message == null) {
                viewHolder.getView("txt_date", TextView.class).setText("");
                viewHolder.getView("txt_desc", TextView.class).setText("");
            } else if (message instanceof UserMessage) {
                viewHolder.getView("txt_date", TextView.class).setText(SendBirdHelper.getDisplayTimeOrDate(mContext, message.getCreatedAt()));
                viewHolder.getView("txt_desc", TextView.class).setText(((UserMessage) message).getMessage());
            } else if (message instanceof AdminMessage) {
                viewHolder.getView("txt_date", TextView.class).setText(SendBirdHelper.getDisplayTimeOrDate(mContext, message.getCreatedAt()));
                viewHolder.getView("txt_desc", TextView.class).setText(((AdminMessage) message).getMessage());
            } else if (message instanceof FileMessage) {
                viewHolder.getView("txt_date", TextView.class).setText(SendBirdHelper.getDisplayTimeOrDate(mContext, message.getCreatedAt()));
                viewHolder.getView("txt_desc", TextView.class).setText(mContext.getString(R.string.str_file));
            }
            return convertView;
        }

        private static class ViewHolder {
            private Hashtable<String, View> holder = new Hashtable<>();

            private void setView(String k, View v) {
                holder.put(k, v);
            }

            private View getView(String k) {
                return holder.get(k);
            }

            private <T> T getView(String k, Class<T> type) {
                return type.cast(getView(k));
            }
        }
    }
}
