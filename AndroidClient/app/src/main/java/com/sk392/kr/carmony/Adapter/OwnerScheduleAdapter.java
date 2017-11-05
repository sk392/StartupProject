package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sk392.kr.carmony.Item.OwnerScheduleItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by SK392 on 2016-11-08.
 */

public class OwnerScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<OwnerScheduleItem> items;
    int item_layout;

    public final static class ListViewHolder extends RecyclerView.ViewHolder{
        TextView dateMonth,dateDay;
        View viewHour8,viewHour9,viewHour10,viewHour11,viewHour12,viewHour13
                ,viewHour14,viewHour15,viewHour16,viewHour17,viewHour18,viewHour19,viewHour20
                ,viewHour21,viewHour22,viewHour23;
        public ListViewHolder(View itemView){
            super(itemView);
            dateMonth = (TextView)itemView.findViewById(R.id.tv_schedule_item_date_month);
            dateDay = (TextView)itemView.findViewById(R.id.tv_schedule_item_date_day);
            viewHour8 = itemView.findViewById(R.id.hour_8);
            viewHour9 = itemView.findViewById(R.id.hour_9);
            viewHour10 = itemView.findViewById(R.id.hour_10);
            viewHour11 = itemView.findViewById(R.id.hour_11);
            viewHour12 = itemView.findViewById(R.id.hour_12);
            viewHour13 = itemView.findViewById(R.id.hour_13);
            viewHour14 = itemView.findViewById(R.id.hour_14);
            viewHour15 = itemView.findViewById(R.id.hour_15);
            viewHour16 = itemView.findViewById(R.id.hour_16);
            viewHour17 = itemView.findViewById(R.id.hour_17);
            viewHour18 = itemView.findViewById(R.id.hour_18);
            viewHour19 = itemView.findViewById(R.id.hour_19);
            viewHour20 = itemView.findViewById(R.id.hour_20);
            viewHour21 = itemView.findViewById(R.id.hour_21);
            viewHour22 = itemView.findViewById(R.id.hour_22);
            viewHour23 = itemView.findViewById(R.id.hour_23);
        }
    }

    public OwnerScheduleAdapter(Context context, List<OwnerScheduleItem> items, int item_layout){
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OwnerScheduleAdapter.ListViewHolder listholder = (OwnerScheduleAdapter.ListViewHolder) holder;
        //리사이클하지말라거
        listholder.setIsRecyclable(false);
        //아이템이 널이 아닐 경우
        if (items.get(position) != null) {
            final OwnerScheduleItem item = items.get(position);
            switch (item.getType()){
                //일정이 없는 날
                case "1":
                    listholder.dateMonth.setText(item.getDateMonth());
                    listholder.dateDay.setText(item.getDateDay());
                    break;

                //일정이 있는 날
                case "2":
                    for(int i=item.getStartHour(); i<=item.getEndHour(); i++){
                        switch(i){
                            case 8:
                                listholder.viewHour8.setVisibility(View.VISIBLE);

                                listholder.viewHour8.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 9:
                                listholder.viewHour9.setVisibility(View.VISIBLE);

                                listholder.viewHour9.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 10:
                                listholder.viewHour10.setVisibility(View.VISIBLE);

                                listholder.viewHour10.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 11:
                                listholder.viewHour11.setVisibility(View.VISIBLE);

                                listholder.viewHour11.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 12:
                                listholder.viewHour12.setVisibility(View.VISIBLE);

                                listholder.viewHour12.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 13:
                                listholder.viewHour13.setVisibility(View.VISIBLE);

                                listholder.viewHour13.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 14:
                                listholder.viewHour14.setVisibility(View.VISIBLE);

                                listholder.viewHour14.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 15:
                                listholder.viewHour15.setVisibility(View.VISIBLE);

                                listholder.viewHour15.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 16:
                                listholder.viewHour16.setVisibility(View.VISIBLE);

                                listholder.viewHour16.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 17:
                                listholder.viewHour17.setVisibility(View.VISIBLE);

                                listholder.viewHour17.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 18:
                                listholder.viewHour18.setVisibility(View.VISIBLE);

                                listholder.viewHour18.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 19:
                                listholder.viewHour19.setVisibility(View.VISIBLE);

                                listholder.viewHour19.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 20:
                                listholder.viewHour20.setVisibility(View.VISIBLE);

                                listholder.viewHour20.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 21:
                                listholder.viewHour21.setVisibility(View.VISIBLE);

                                listholder.viewHour21.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 22:
                                listholder.viewHour22.setVisibility(View.VISIBLE);

                                listholder.viewHour22.setBackgroundResource(R.color.scheduleBlock);
                                break;
                            case 23:
                                listholder.viewHour23.setVisibility(View.VISIBLE);

                                listholder.viewHour23.setBackgroundResource(R.color.scheduleBlock);
                                break;
                        }
                    }

                    listholder.dateMonth.setText(item.getDateMonth());
                    listholder.dateDay.setText(item.getDateDay());

                    break;
            }

        }
    }

    @Override
    public OwnerScheduleAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, null);
        return new OwnerScheduleAdapter.ListViewHolder(v);
    }

    public void setItems(List<OwnerScheduleItem> items) {
        this.items = items;
    }

    public OwnerScheduleItem getItem(int position){

        return  items.get(position);


    }
    @Override
    public int getItemCount() {
        return this.items.size();
    }


}
