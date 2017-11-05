package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sk392.kr.carmony.Item.OptionItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by sk392 on 2016-09-08.
 */
public class SearchOptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<OptionItem> items;
    int item_layout;

    public final static class ListViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title;
        public ListViewHolder(View itemView){
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.iv_option_item);
            title = (TextView)itemView.findViewById(R.id.tv_option_item);
        }
    }

    public SearchOptionAdapter(Context context, List<OptionItem> items, int item_layout){
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ListViewHolder listholder =(ListViewHolder)holder;
        final OptionItem item = items.get(position);
        listholder.image.setImageResource(item.getImage());
        listholder.title.setText(item.getTitleKo());
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, null);

        return new ListViewHolder(v);
    }

    public void setItems(List<OptionItem> items) {
        this.items = items;
    }

    public OptionItem getItem(int position){

        return  items.get(position);


    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }


}
