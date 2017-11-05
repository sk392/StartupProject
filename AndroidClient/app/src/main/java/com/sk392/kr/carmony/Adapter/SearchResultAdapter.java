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
import com.sk392.kr.carmony.Item.ResultItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by sk392 on 2016-09-20.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<ResultItem> resultItemList;
    int item_layout;
    View.OnClickListener mOnClickListener;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ResultViewholder viewholder = (ResultViewholder) holder;
        ResultItem item = resultItemList.get(position);

        viewholder.rbPerson.setRating(item.getRationPerson());
        viewholder.rbCar.setRating(item.getRationgCar());
        viewholder.tvCarModel.setText(item.getTvCarModel());
        viewholder.tvCarYears.setText(item.getTvCarYears());
        viewholder.tvOwnerName.setText(item.getOwnerName());
        viewholder.tvExCost.setText(String.format("%,d",Integer.parseInt(item.getTvExCost()))+"원");
        viewholder.ivCar.setImageResource(R.drawable.carinfo_default_image);
        Glide.with(context).load(item.getImgCar())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fitCenter().into(viewholder.ivCar);
        Glide.with(context).load(item.getImgProfile())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .fitCenter().into(viewholder.ivProfile);

    }


    @Override
    public ResultViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result,null);
        view.setOnClickListener(mOnClickListener);
        ResultViewholder resultViewholder = new ResultViewholder(view);
        return resultViewholder;
    }

    @Override
    public int getItemCount() {
        return this.resultItemList.size();
    }
    public SearchResultAdapter(Context context, List<ResultItem> resultItemList, int item_layout, View.OnClickListener onClickListener){
        this.context = context;
        this.resultItemList = resultItemList;
        this.item_layout = item_layout;
        this.mOnClickListener =onClickListener;
    }
    public final static class ResultViewholder extends RecyclerView.ViewHolder{
        ImageView ivProfile,ivCar;
        TextView tvCarModel,tvCarYears,tvExCost,tvOwnerName;
        RatingBar rbCar,rbPerson;

        public ResultViewholder(View itemView){
            super(itemView);
            ivProfile =(ImageView)itemView.findViewById(R.id.iv_item_profile);
            ivCar =(ImageView)itemView.findViewById(R.id.iv_item_car);
            tvCarModel = (TextView)itemView.findViewById(R.id.tv_resultitem_car_model);
            tvCarYears = (TextView)itemView.findViewById(R.id.tv_resultitem_car_years);
            tvExCost = (TextView)itemView.findViewById(R.id.tv_ex_cost);
            rbCar = (RatingBar)itemView.findViewById(R.id.rb_item_car);
            rbPerson = (RatingBar)itemView.findViewById(R.id.rb_item_person);
            tvOwnerName = (TextView)itemView.findViewById(R.id.tv_resultitem_owner_name);
        }
    }
    public void animate(){
        notifyItemRangeChanged(0,getItemCount());
    }

}
