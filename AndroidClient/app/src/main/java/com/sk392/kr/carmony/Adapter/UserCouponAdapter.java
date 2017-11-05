package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sk392.kr.carmony.Item.CouponItem;
import com.sk392.kr.carmony.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SK392 on 2016-12-10.
 */

public class UserCouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<CouponItem> couponItemList;
    View.OnClickListener mOnclicListener;
    int item_layout;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CouponViewholder viewholder = (CouponViewholder) holder;
        CouponItem item = couponItemList.get(position);
        String carShape = "";
        HashMap<String, String> carShapeList = item.getCarShapeList();
        String[] mcarShapeList = context.getResources().getStringArray(R.array.car_models);

        List list = new ArrayList();
        if (carShapeList.get("car_light").equals("y")) {
            list.add(mcarShapeList[1]);
        }
        if (carShapeList.get("car_compact").equals("y")) {
            list.add(mcarShapeList[2]);
        }
        if (carShapeList.get("car_semi_medium").equals("y")) {
            list.add(mcarShapeList[3]);
        }
        if (carShapeList.get("car_medium").equals("y")) {
            list.add(mcarShapeList[4]);
        }
        if (carShapeList.get("car_large").equals("y")) {
            list.add(mcarShapeList[5]);
        }
        if (carShapeList.get("car_suv").equals("y")) {
            list.add(mcarShapeList[6]);
        }

        for(int i=0; i<list.size(); i++){
            if(i==0)
                carShape += list.get(i);
            else
                carShape +=", " +list.get(i) ;

        }
        viewholder.tvCarShape.setText(carShape);
        if (item.getType().equals("1")) {
            //금액할인 일 경우
            viewholder.tvdisCredit.setText(item.getDisCredit());
            viewholder.rlCouponBack.setBackgroundResource(R.drawable.coupon_credit_type);

        } else {
            //퍼센트 할인일 경우
            viewholder.tvdisCredit.setText(item.getDisCredit() + "% ");
            viewholder.rlCouponBack.setBackgroundResource(R.drawable.coupon_per_type);

        }
        viewholder.tvlimitDate.setText(item.getLimitDate() + "까지");
        if (item.getwEnd().equals("y")) {
            //주중 이용 가능인지
            viewholder.tvwDay.setText("주중, 주말");
        } else if (item.getwDay().equals("y")) {
            //주말까지 이용 가능인지.
            viewholder.tvwDay.setText("주중");
        }


    }


    @Override
    public CouponViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, null);
        view.setOnClickListener(mOnclicListener);
        CouponViewholder couponViewholder = new CouponViewholder(view);
        return couponViewholder;
    }

    @Override
    public int getItemCount() {
        return this.couponItemList.size();
    }

    public UserCouponAdapter(Context context, List<CouponItem> couponItemList, int item_layout, View.OnClickListener onClickListener) {
        this.context = context;
        this.couponItemList = couponItemList;
        this.item_layout = item_layout;
        this.mOnclicListener = onClickListener;
    }

    public final static class CouponViewholder extends RecyclerView.ViewHolder {
        TextView tvCarShape, tvdisCredit, tvwDay, tvlimitDate;
        RelativeLayout rlCouponBack;
        public CouponViewholder(View itemView) {
            super(itemView);

            tvCarShape = (TextView) itemView.findViewById(R.id.tv_coupon_item_car_shape);
            tvdisCredit = (TextView) itemView.findViewById(R.id.tv_coupon_item_dis_credit);
            tvwDay = (TextView) itemView.findViewById(R.id.tv_coupon_item_day);
            rlCouponBack = (RelativeLayout) itemView.findViewById(R.id.rl_coupon_item_background);
            tvlimitDate = (TextView) itemView.findViewById(R.id.tv_coupon_item_date);
        }
    }

}
