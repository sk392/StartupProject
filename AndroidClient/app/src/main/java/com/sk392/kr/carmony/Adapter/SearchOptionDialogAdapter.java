package com.sk392.kr.carmony.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sk392.kr.carmony.Item.OptionItem;
import com.sk392.kr.carmony.R;

import java.util.List;

/**
 * Created by sk392 on 2016-10-07.
 */

public class SearchOptionDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<OptionItem> items;
    int item_layout;
    boolean clickable;
    View.OnClickListener mOnClickListener;

    public final static class DialogHolder extends RecyclerView.ViewHolder {
        CheckBox cbOption;
        ImageView ivOption;
        TextView title;

        public DialogHolder(View itemView) {
            super(itemView);
            cbOption = (CheckBox) itemView.findViewById(R.id.cb_option_dialog_item);
            title = (TextView) itemView.findViewById(R.id.tv_option_dialog_item);
            ivOption = (ImageView) itemView.findViewById(R.id.iv_option_dialog_item);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_option,null);
        view.setOnClickListener(mOnClickListener);
        DialogHolder  dialogHolder= new DialogHolder(view);
        return dialogHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DialogHolder dialogHolder = (DialogHolder) holder;
        final OptionItem item = items.get(position);
        dialogHolder.cbOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setIsChecked(isChecked);
            }
        });
        dialogHolder.cbOption.setChecked(item.getIsChecked());

        Glide.with(context).load(item.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().into(dialogHolder.ivOption);
        dialogHolder.title.setText(item.getTitleKo());

        //클릭이 불가능하면 체크박스 출력 해제.
        if(!clickable)
            dialogHolder.cbOption.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public SearchOptionDialogAdapter(Context context, List<OptionItem> items, int item_layout, boolean clickable, View.OnClickListener onClickListener) {
        this.context = context;
        this.item_layout = item_layout;
        this.items = items;
        this.clickable = clickable;
        this.mOnClickListener = onClickListener;
    }
}