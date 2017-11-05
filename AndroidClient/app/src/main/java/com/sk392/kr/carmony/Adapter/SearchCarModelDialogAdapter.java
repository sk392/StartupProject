package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sk392.kr.carmony.Item.CarModelItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by sk392 on 2016-10-12.
 */

public class SearchCarModelDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context context;
    List<CarModelItem> modelsItemList;
    int item_layout;
    View.OnClickListener mOnClickListener;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CarModelViewholder carModelViewholder = (CarModelViewholder)holder;
        final CarModelItem item = modelsItemList.get(position);

        carModelViewholder.tvCarModel.setText(item.getModel());
        carModelViewholder.cbIsCheck.setChecked(item.getIscheck());
    }

    public final static class CarModelViewholder extends RecyclerView.ViewHolder{
        CheckBox cbIsCheck;
        TextView tvCarModel;
        public CarModelViewholder(View itemView){
            super(itemView);
            cbIsCheck = (CheckBox) itemView.findViewById(R.id.cb_car_model_dialog_item);
            tvCarModel = (TextView)itemView.findViewById(R.id.tv_modelsitem_car_model);

        }
    }
    @Override
    public CarModelViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car_model,null);

        view.setOnClickListener(mOnClickListener);
        CarModelViewholder  carModelViewholder= new CarModelViewholder(view);
        return carModelViewholder;
    }

    public SearchCarModelDialogAdapter(Context context, List<CarModelItem> modelsItemList, int item_layout, View.OnClickListener mOnClickListener){
        this.context = context;
        this.modelsItemList = modelsItemList;
        this.item_layout = item_layout;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.modelsItemList.size();
    }
}