package com.sk392.kr.carmony.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sk392.kr.carmony.Adapter.SearchCarModelDialogAdapter;
import com.sk392.kr.carmony.Item.CarModelItem;
import com.sk392.kr.carmony.Library.RecyclerDecoration;
import com.sk392.kr.carmony.R;

import java.util.ArrayList;
import java.util.List;

import static com.sk392.kr.carmony.Fragment.SearchFragment.modelsString;

/**
 * Created by sk392 on 2016-10-12.
 */

public class CarModelDialogFragment extends DialogFragment implements View.OnClickListener {
    RecyclerView rvCarModelDialog;
    List<CarModelItem> listCarModels;
    boolean[] isCheckedList;
    SearchCarModelDialogAdapter carModelAdapter;
    private static final String TAG = "CarModelDialogFragment";



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        listCarModels = new ArrayList<>();
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_car_models, null);
        ((TextView) view.findViewById(R.id.tv_car_model_dialog_title)).setText("차량 모델");

        if (listCarModels.size() == 0) {
            for (int i = 0; i < modelsString.length; i++) {
                listCarModels.add(new CarModelItem(modelsString[i], false));
            }
        }
        carModelAdapter = new SearchCarModelDialogAdapter(getActivity(), listCarModels, R.layout.item_car_model, this);

        rvCarModelDialog = (RecyclerView) view.findViewById(R.id.rv_car_models);
        rvCarModelDialog.addItemDecoration(new RecyclerDecoration(getActivity().getApplicationContext()));

        rvCarModelDialog.setLayoutManager(new LinearLayoutManager
                (getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvCarModelDialog.setAdapter(carModelAdapter);
        for(int i=0; i<listCarModels.size();i++){
            listCarModels.get(i).setIscheck(isCheckedList[i]);
        }
        carModelAdapter.notifyDataSetChanged();

        ((Button) view.findViewById(R.id.bt_car_model_dialog)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "";
                boolean[] isCheckedListAfter = isCheckedList;
                isCheckedList = new boolean[isCheckedListAfter.length];

                for (int i = 0; i < listCarModels.size(); i++) {
                    if (listCarModels.get(i).getIscheck()) {
                        if (!str.equals(""))
                            str += ",";
                        str += listCarModels.get(i).getModel();
                        isCheckedList[i] = true;
                    }else{
                        isCheckedList[i] = false;
                    }
                }
                if (!str.equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra("car_name",str);
                    intent.putExtra("isCheckedList",isCheckedList);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                }else{
                    isCheckedList = isCheckedListAfter;
                }
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();

    }

    public boolean[] getIsCheckedList() {
        return isCheckedList;
    }

    public void setIsCheckedList(boolean[] isCheckedList) {
        this.isCheckedList = isCheckedList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        CarModelItem item;
        CheckBox cbItem = (CheckBox) v.findViewById(R.id.cb_car_model_dialog_item);
        int itemPosition = rvCarModelDialog.getChildLayoutPosition(v);
        if (listCarModels.get(itemPosition).getIscheck()) {
            listCarModels.get(itemPosition).setIscheck(false);
            cbItem.setChecked(false);
        } else {
            listCarModels.get(itemPosition).setIscheck(true);
            cbItem.setChecked(true);
        }
        if (itemPosition == 0 && listCarModels.get(0).getIscheck()) {
            //체크 박스를 하나 체크하면 다른 것들은 체크 박스를 해제한다.
            for (int i = 1; i < listCarModels.size(); i++) {
                item = listCarModels.get(i);
                item.setIscheck(false);

            }
        } else if (listCarModels.get(itemPosition).getIscheck()) {
            listCarModels.get(0).setIscheck(false);
        }
        carModelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels - getResources().getDisplayMetrics().widthPixels / 15;
        int height = getResources().getDimensionPixelSize(R.dimen.dialog_size_height);
        getDialog().getWindow().setLayout(width, height);
    }
}
