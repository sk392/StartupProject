package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sk392.kr.carmony.Item.ReviewsItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by SK392 on 2016-11-07.
 */

public class UserReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ReviewsItem> items;
    int item_layout;

    public final static class ListViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        RatingBar ratingBar;
        TextView content,date,name;
        public ListViewHolder(View itemView){
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.iv_review_item_profile);
            content = (TextView)itemView.findViewById(R.id.tv_review_item_content);
            date = (TextView)itemView.findViewById(R.id.tv_review_item_date);
            ratingBar = (RatingBar)itemView.findViewById(R.id.rb_reviews_item);
            name = (TextView)itemView.findViewById(R.id.tv_review_item_name);
        }
    }

    public UserReviewsAdapter(Context context, List<ReviewsItem> items, int item_layout){
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserReviewsAdapter.ListViewHolder listholder =(UserReviewsAdapter.ListViewHolder)holder;
        final ReviewsItem item = items.get(position);
        Glide.with(context).load(item.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().into(listholder.image);
        listholder.content.setText(item.getContent());
        listholder.date.setText(item.getDate());
        listholder.ratingBar.setRating(item.getScore());
        listholder.name.setText(item.getName());

    }

    @Override
    public UserReviewsAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_reviews, null);

        return new UserReviewsAdapter.ListViewHolder(v);
    }

    public void setItems(List<ReviewsItem> items) {
        this.items = items;
    }

    public ReviewsItem getItem(int position){

        return  items.get(position);


    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }


}
