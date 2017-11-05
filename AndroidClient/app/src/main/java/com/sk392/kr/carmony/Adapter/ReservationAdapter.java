package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sk392.kr.carmony.Item.ReservationItem;
import com.sk392.kr.carmony.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sk392 on 2016-09-23.
 */
public class ReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;
    public static final int NON_CHILD = 11;
    public static final int RES_BEFORE = 300;
    public static final int RES_NOW = 301;
    public static final int RES_AFTER = 302;

    Context context;
    private List<ReservationItem> data;
    View.OnClickListener mOnClickListener;
    public ReservationAdapter(List<ReservationItem> data,Context context, View.OnClickListener onClickListener) {
        this.data = data;
        this.context = context;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater;
        switch (type) {

            case HEADER:
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_reservation_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;

            case CHILD:
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_reservation_child, parent, false);
                view.setOnClickListener(mOnClickListener);
                ResChildViewholder child = new ResChildViewholder(view,context);
                return child;
            case NON_CHILD:
                inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_reservation_non_child, parent, false);
                ResNonChildViewholder resNonChildViewholder = new ResNonChildViewholder(view,context);
                return resNonChildViewholder;
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ReservationItem item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.text);
                if (item.invisibleChildren == null) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_big_navy_up);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_big_navy_down);
                }
                itemController.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 &&( data.get(pos + 1).type == CHILD ||  data.get(pos + 1).type == NON_CHILD) ) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_big_navy_down);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (ReservationItem i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_big_navy_up);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            case CHILD:

                ResChildViewholder viewholder = (ResChildViewholder) holder;
                ReservationItem reservationItem = data.get(position);
                if(item.getReservationType()==RES_BEFORE){
                    viewholder.setBackground(false);
                    //투명화처리
                    viewholder.setBackground(true);
                    //텍스트 설정 변경, 23버전 이후로 바뀌었다.
                    if(Build.VERSION.SDK_INT<23){
                        viewholder.tvCarModel.setTextAppearance(context,R.style.Blackhelveregular15);
                        viewholder.tvCarYear.setTextAppearance(context,R.style.Blackappleregular15);
                    }else{
                        viewholder.tvCarModel.setTextAppearance(R.style.Blackhelveregular15);
                        viewholder.tvCarYear.setTextAppearance(R.style.Blackappleregular15);
                    }

                }else if(item.getReservationType()==RES_AFTER){
                    viewholder.setBackground(false);
                    //텍스트 설정 변경, 23버전 이후로 바뀌었다.
                    if(Build.VERSION.SDK_INT<23){
                        viewholder.tvCarModel.setTextAppearance(context,R.style.Whitehelveregular15);
                        viewholder.tvCarYear.setTextAppearance(context,R.style.WhiteappleRegular15);
                    }else{
                        viewholder.tvCarModel.setTextAppearance(R.style.Whitehelveregular15);
                        viewholder.tvCarYear.setTextAppearance(R.style.WhiteappleRegular15);
                    }
                    viewholder.setBackground(R.color.slate);
                } else{
                    //텍스트 설정 변경, 23버전 이후로 바뀌었다.
                    if(Build.VERSION.SDK_INT<23){
                        viewholder.tvCarModel.setTextAppearance(context,R.style.Blackhelveregular15);
                        viewholder.tvCarYear.setTextAppearance(context,R.style.Blackappleregular15);
                    }else{
                        viewholder.tvCarModel.setTextAppearance(R.style.Blackhelveregular15);
                        viewholder.tvCarYear.setTextAppearance(R.style.Blackappleregular15);
                    }

                    viewholder.setBackground(false);
                }
                viewholder.tvCarModel.setText(reservationItem.getCarModel());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    Date startDate = dateFormat.parse(reservationItem.getResStart());
                    Date endDate = dateFormat.parse(reservationItem.getResEnd());

                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String strStartDate = transFormat.format(startDate);
                    String strEndDate = transFormat.format(endDate);
                    viewholder.tvResStart.setText(strStartDate);
                    viewholder.tvResEnd.setText(strEndDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewholder.tvCarYear.setText(reservationItem.getCarYear());
                Glide.with(context).load(item.getImageCar())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter().into(viewholder.imageCar);
                break;
            case NON_CHILD:
                ResNonChildViewholder nonChildViewholder = (ResNonChildViewholder) holder;
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public ReservationItem refferalItem;
        public RelativeLayout relativeLayout;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.tv_res_header);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.iv_res_header);
            relativeLayout = (RelativeLayout)itemView;
        }
    }
    private static class ResChildViewholder extends RecyclerView.ViewHolder {
        ImageView imageCar;
        View mItemView;
        TextView tvCarModel, tvResEnd, tvCarYear, tvResStart;
        Context context;

        public ResChildViewholder(View itemView,Context context) {
            super(itemView);
            this.mItemView = itemView;
            this.context= context;
            imageCar = (ImageView) itemView.findViewById(R.id.iv_res_item_car);
            tvCarModel = (TextView) itemView.findViewById(R.id.tv_res_item_car_model);
            tvResEnd = (TextView) itemView.findViewById(R.id.tv_res_item_res_end);
            tvCarYear = (TextView) itemView.findViewById(R.id.tv_res_item_car_year);
            tvResStart = (TextView) itemView.findViewById(R.id.tv_res_item_res_start);
        }
        public void setBackground(boolean isbackground){
            if(isbackground)
                ((RelativeLayout)mItemView.findViewById(R.id.rl_reservation_item_child)).setVisibility(View.VISIBLE);
            else {
                ((RelativeLayout) mItemView.findViewById(R.id.rl_reservation_item_child)).setVisibility(View.GONE);
                mItemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

        public void setBackground(int color){
            mItemView.setBackgroundColor(context.getResources().getColor(color));

        }
    }
    private static class ResNonChildViewholder extends RecyclerView.ViewHolder {
        Context context;

        public ResNonChildViewholder(View itemView,Context context) {
            super(itemView);
            this.context= context;
        }
    }


}
