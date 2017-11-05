package com.sk392.kr.carmony.Library;

import android.app.Activity;

import com.sk392.kr.carmony.Activity.MainActivity;

/**
 * Created by sk392 on 2016-09-28.
 */

public class BackPressClosehHandler {
    private long backKeyPressedTime = 0;
    private static final String TAG = "BackPressClosehHandler";

    private Activity activity;

    public BackPressClosehHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            MainActivity.showToast(activity,"\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.");
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
        }
    }


}
