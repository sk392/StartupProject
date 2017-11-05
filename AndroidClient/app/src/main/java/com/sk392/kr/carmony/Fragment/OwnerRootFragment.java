package com.sk392.kr.carmony.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sk392.kr.carmony.R;

public class OwnerRootFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private static final String TAG = "OwnerRootFragment";

    String type;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_owner_root, container, false);
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.shared_main_name), Context.MODE_PRIVATE);
        type = sharedPreferences.getString(getString(R.string.shared_type),"");
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */
        //if 여기서 조건을 통해 오너인지 아닌지 판단 후 바꿔줌
        if(type.equals("0")){
            //차주가 아닌경우
            transaction.replace(R.id.fm_owner_root,  new OwnerFragment());
        }else{
            //차주 인경우
            transaction.replace(R.id.fm_owner_root,  new OwnerRegisteredFragment());

        }

        transaction.commit();
        return mView;
    }
}
