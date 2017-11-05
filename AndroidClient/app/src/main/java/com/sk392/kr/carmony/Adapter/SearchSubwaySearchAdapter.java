package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sk392.kr.carmony.Item.SubwaySearchItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by sk392 on 2016-10-12.
 */

public class SearchSubwaySearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<SubwaySearchItem> subwaySearchItemList;
    int item_layout;
    View.OnClickListener mOnClickListener;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SubwaySearchViewHolder subwaySearchViewHolder = (SubwaySearchViewHolder)holder;
        SubwaySearchItem item = subwaySearchItemList.get(position);
        String lineName;
        try{
            lineName = Integer.parseInt(item.getSubwayLine())+"호선";
        }catch (NumberFormatException e){
            lineName = item.getSubwayLine();
        }
        subwaySearchViewHolder.tvSubwayLine.setText(lineName);
        subwaySearchViewHolder.tvSubwayName.setText(item.getSubwayName());
        subwaySearchViewHolder.tvSubwayId.setText(item.getSubwayCode());
    }

    public final static class SubwaySearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvSubwayName;
        TextView tvSubwayLine;
        TextView tvSubwayId;
        public SubwaySearchViewHolder(View itemView){
            super(itemView);
            tvSubwayName = (TextView)itemView.findViewById(R.id.tv_subway_item_subwayname);
            tvSubwayLine = (TextView)itemView.findViewById(R.id.tv_subway_item_subwayline);
            tvSubwayId = (TextView)itemView.findViewById(R.id.tv_subway_item_subwayid);
        }
    }
    @Override
    public SubwaySearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subway_search,null);
        view.setOnClickListener(mOnClickListener);
        SubwaySearchViewHolder  subwaySearchViewHolder= new SubwaySearchViewHolder(view);
        return subwaySearchViewHolder;
    }
    public SearchSubwaySearchAdapter(Context context, List<SubwaySearchItem> subwaySearchItemList, int item_layout, View.OnClickListener mOnClickListener){
        this.context = context;
        this.subwaySearchItemList = subwaySearchItemList;
        this.item_layout = item_layout;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.subwaySearchItemList.size();
    }
}
