package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sk392.kr.carmony.Item.SubwaySearchHistoryItem;
import com.sk392.kr.carmony.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sk392 on 2016-10-12.
 */

public class SearchSubwaySearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<SubwaySearchHistoryItem> subwaySearchHistoryItemList;
    int item_layout;
    View.OnClickListener mOnClickListener;
    SharedPreferences mSharedPreferences;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubwaySearchHistoryViewHolder subwaySearchViewHolder = (SubwaySearchHistoryViewHolder) holder;
        SubwaySearchHistoryItem item = subwaySearchHistoryItemList.get(position);

        subwaySearchViewHolder.tvSubwaySearchText.setText(item.getSubwaySearchText());
        subwaySearchViewHolder.tvSubwaySearchDate.setText(item.getSubwaySearchDate());
    }

    public class SubwaySearchHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvSubwaySearchText;
        TextView tvSubwaySearchDate;
        ImageView ivSubwaySearchHistoryDelete;

        public SubwaySearchHistoryViewHolder(View itemView) {
            super(itemView);
            tvSubwaySearchText = (TextView) itemView.findViewById(R.id.tv_subway_search_history_item_text);
            tvSubwaySearchDate = (TextView) itemView.findViewById(R.id.tv_subway_search_history_item_date);
            ivSubwaySearchHistoryDelete = (ImageView) itemView.findViewById(R.id.iv_subway_search_history_item);
            ivSubwaySearchHistoryDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(ivSubwaySearchHistoryDelete)) {
                removeAt(getPosition());
            }
        }
    }

    @Override
    public SubwaySearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway_search_history, null);
        view.setOnClickListener(mOnClickListener);
        SubwaySearchHistoryViewHolder subwaySearchHistoryViewHolder = new SubwaySearchHistoryViewHolder(view);
        return subwaySearchHistoryViewHolder;
    }

    public SearchSubwaySearchHistoryAdapter(Context context, List<SubwaySearchHistoryItem> subwaySearchHistoryItemList, int item_layout
            , View.OnClickListener mOnClickListener, SharedPreferences sharedPreferences) {

        this.context = context;
        this.subwaySearchHistoryItemList = subwaySearchHistoryItemList;
        this.item_layout = item_layout;
        this.mOnClickListener = mOnClickListener;
        this.mSharedPreferences = sharedPreferences;
    }

    public void removeAt(int position) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Set beforeList = mSharedPreferences.getStringSet(context.getString(R.string.shared_subway_list), null);
        Set afterList = new HashSet();
        for (int j = beforeList.size() - 1; j >= 0; j--) {
            if (j != position)
                afterList.add(beforeList.toArray()[j]);
        }
        editor.putStringSet(context.getString(R.string.shared_subway_list),afterList);
        editor.commit();
        subwaySearchHistoryItemList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return this.subwaySearchHistoryItemList.size();
    }


}
