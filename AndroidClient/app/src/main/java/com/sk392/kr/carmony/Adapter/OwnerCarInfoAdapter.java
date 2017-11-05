package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sk392.kr.carmony.Item.OwnerCarInfoItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by sk392 on 2016-09-23.
 */
public class OwnerCarInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<OwnerCarInfoItem> carInfoItems;
    int item_layout;
    View.OnClickListener mOnClickListener;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CarInfoViewHolder viewholder = (CarInfoViewHolder) holder;
        OwnerCarInfoItem item = carInfoItems.get(position);

        viewholder.tvCarModel.setText(item.getCarModel());
        viewholder.tvCarYear.setText(item.getCarYear());

        Glide.with(context).load(item.getCarImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().into(viewholder.imageCar);

    }


    @Override
    public CarInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_owner_car_info, null);
        view.setOnClickListener(mOnClickListener);
        CarInfoViewHolder resultViewholder = new CarInfoViewHolder(view);
        return resultViewholder;
    }

    @Override
    public int getItemCount() {
        return this.carInfoItems.size();
    }

    public OwnerCarInfoAdapter(Context context,List<OwnerCarInfoItem> ownerCarInfoItems, int item_layout,View.OnClickListener onClickListener) {
        this.context = context;
        this.item_layout = item_layout;
        this.mOnClickListener = onClickListener;
        this.carInfoItems = ownerCarInfoItems;

    }
    public void setResItemList(List<OwnerCarInfoItem> resList){
        this.carInfoItems = resList;
    }

    public final static class CarInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCar;
        TextView tvCarModel, tvCarYear;

        public CarInfoViewHolder(View itemView) {
            super(itemView);

            imageCar = (ImageView) itemView.findViewById(R.id.iv_owner_car_info_item);
            tvCarModel = (TextView) itemView.findViewById(R.id.tv_owner_car_info_item_model);
            tvCarYear = (TextView) itemView.findViewById(R.id.tv_owner_car_info_item_year);
        }
    }

}
