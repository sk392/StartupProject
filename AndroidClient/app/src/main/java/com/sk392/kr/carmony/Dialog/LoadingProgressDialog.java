package com.sk392.kr.carmony.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.sk392.kr.carmony.R;

/**
 * Created by SK392 on 2016-11-25.
 */

public class LoadingProgressDialog extends Dialog {


    public LoadingProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.dialog_loading_progress);
        // 다이얼로그에 박을 레이아웃
    }
}
