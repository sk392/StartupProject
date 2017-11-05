package com.sk392.kr.carmony.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.sk392.kr.carmony.Adapter.SearchOptionDialogAdapter;
import com.sk392.kr.carmony.Fragment.SearchFragment;
import com.sk392.kr.carmony.Item.OptionItem;
import com.sk392.kr.carmony.Library.RecyclerDecoration;
import com.sk392.kr.carmony.R;

import java.util.ArrayList;
import java.util.List;

import static com.sk392.kr.carmony.Fragment.SearchFragment.optionDrawable;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringEn;
import static com.sk392.kr.carmony.Fragment.SearchFragment.optionStringKo;

/**
 * Created by sk392 on 2016-10-07.
 */

public class OptionDialogFragment extends DialogFragment implements View.OnClickListener {
    RecyclerView rvOption;
    private static final String TAG = "OptionDialogFragment";
    private SearchOptionDialogAdapter dialogAdapter;
    List<OptionItem> items = new ArrayList<>();
    //옵션의 개수만큼 설정해준다. 2017-02-12 : 7개
    boolean[] selectedPosition = {false, false, false, false, false, false, false};
    ;
    boolean clickable = false, islist = false;
    int type;

    public interface OptionDialogListener {
        void checkSelectedItem(DialogFragment dialog, List<OptionItem> selectedOptionItems, boolean[] selectedPosition);

    }

    OptionDialogListener dialogListener;


    public OptionDialogFragment() {

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            dialogListener = (OptionDialogListener) getTargetFragment();

        } catch (ClassCastException e) {

            FirebaseCrash.logcat(Log.ERROR, TAG, "ClassCastException Fail");
            FirebaseCrash.report(e);
            throw new ClassCastException();

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_option, null);
        ((TextView) view.findViewById(R.id.tv_optiondialog_title)).setText("차량 옵션");
        type = getArguments().getInt("type");

        if (getArguments().getBooleanArray("selectedPosition") != null) {
            selectedPosition = getArguments().getBooleanArray("selectedPosition");
        }
        String str = "";
        for (int i = 0; i < selectedPosition.length; i++) {
            str += i + " = " + selectedPosition[i];

        }

        //이전의 선택후 옵션추가를 누른 경우에 해당 옵션 선택을 저장한다.
        //초기 호출시엔 새로 만든다.
        //리스트를 전송받은 경우 새로 만들지 않는다.
        if (items.size() == 0) {
            Log.d("TTATA", "size=0");
            for (int i = 0; i < optionStringKo.length; i++) {
                Log.d("TTATA", optionDrawable.getResourceId(i, -1) + " // ");

                items.add(new OptionItem(optionDrawable.getResourceId(i, -1), optionStringKo[i], optionStringEn[i], false));
                if (selectedPosition[i]) {
                    items.get(i).setIsChecked(true);
                } else {
                    items.get(i).setIsChecked(false);
                }
            }
        } else {//옵션이 일부만 오면 일부만 출력
            for (int i = 0; i < items.size(); i++) {
                if (selectedPosition[i]) {
                    items.get(i).setIsChecked(true);
                } else {
                    items.get(i).setIsChecked(false);
                }
            }

        }

        if (type == SearchFragment.CLICKABLE_YES) {
            clickable = true;
        } else if (type == SearchFragment.CLICKABLE_NO) {
            clickable = false;
        }


        dialogAdapter = new SearchOptionDialogAdapter(getActivity().getBaseContext(), items, R.layout.item_dialog_option, clickable, this);
        rvOption = (RecyclerView) view.findViewById(R.id.rv_option_alert);
        dialogAdapter.notifyDataSetChanged();

        rvOption.addItemDecoration(new RecyclerDecoration(getActivity().getApplicationContext()));

        rvOption.setLayoutManager(new LinearLayoutManager
                (getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvOption.setAdapter(dialogAdapter);
      //  Log.d("TTTTest", "is checked" + clickable);

        if (type == SearchFragment.CLICKABLE_YES) {
            //옵션 추가 클릭시 아이템 리스트를 모두 확인하여 눌려있는(ispressed) 아이템을 모두 배열로 만든 후 반환한다.
            ((Button) view.findViewById(R.id.bt_option_dialog)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<OptionItem> sendSelectedItems = new ArrayList<>();
                    for (int i = 0; i < items.size(); i++) {
                        OptionItem item = items.get(i);
                        Log.d("optionadd", item.getIsChecked() + "");
                        if (item.getIsChecked()) {
                            selectedPosition[i] = true;
                            sendSelectedItems.add(item);
                        } else {
                            selectedPosition[i] = false;
                        }
                    }

                    dialogListener.checkSelectedItem(OptionDialogFragment.this, sendSelectedItems, selectedPosition);
                    dismiss();
                }
            });
        } else if (type == SearchFragment.CLICKABLE_NO) {

            ((Button) view.findViewById(R.id.bt_option_dialog)).setText("확인");
            ((Button) view.findViewById(R.id.bt_option_dialog)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

        }
        builder.setView(view);

        return builder.create();
    }

    public List<OptionItem> getItems() {
        return items;
    }

    public void setItems(List<OptionItem> items) {
        this.items = items;
        islist = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().widthPixels / 15;
        int height = getResources().getDimensionPixelSize(R.dimen.dialog_size_height);
        getDialog().getWindow().setLayout(width, height);

    }

    @Override
    public void onClick(View view) {
        OptionItem item;

        int itemPosition = rvOption.getChildLayoutPosition(view);
        item = items.get(itemPosition);

        if(item.getIsChecked())
            item.setIsChecked(false);
        else
            item.setIsChecked(true);
        dialogAdapter.notifyDataSetChanged();
    }
}
