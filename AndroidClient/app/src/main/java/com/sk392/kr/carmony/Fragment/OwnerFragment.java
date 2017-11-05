package com.sk392.kr.carmony.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sk392.kr.carmony.R;

public class OwnerFragment extends Fragment {
    Button btOwnerRegiInfo;
    private static final String TAG = "OwnerFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView =inflater.inflate(R.layout.fragment_owner , container, false);
                Glide.with(getActivity()).load(R.drawable.owner_page_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop().into((ImageView)mView.findViewById(R.id.iv_owner));
        btOwnerRegiInfo = (Button)mView.findViewById(R.id.bt_owner_regi_info);
        btOwnerRegiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.carmony.club/share-1"));
                startActivity(intent);
            }
        });

        return mView;

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
