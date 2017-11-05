package com.sk392.kr.carmony.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sk392.kr.carmony.Adapter.ImageViewPagerAdapter;
import com.sk392.kr.carmony.Library.BackPressClosehHandler;
import com.sk392.kr.carmony.R;

import java.util.ArrayList;
import java.util.List;

public class IntroTutorialActivity extends AppCompatActivity {
    ViewPager viewpager;
    Button introLogin, introSigngup;
    List<Integer> imageList;
    private static final String TAG = "IntroTutorialActivity";

    BackPressClosehHandler backPressClosehHandler;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_tutorial);
        backPressClosehHandler = new BackPressClosehHandler(this);
        viewpager = (ViewPager) findViewById(R.id.intropager);
        viewpager.setClipToPadding(false);
        viewpager.setPageMargin(12);
        imageList = new ArrayList<>();

        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        int int1 = R.drawable.intro;
        imageList.add(R.drawable.test1);
        imageList.add(R.drawable.test1);
        imageList.add(R.drawable.test1);
        imageList.add(R.drawable.test2);
        imageList.add(R.drawable.test2);
        imageList.add(R.drawable.test2);
        ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(getLayoutInflater(), imageList);
        //ViewPager에 Adapter 설정
        viewpager.setAdapter(adapter);

        introLogin = (Button) findViewById(R.id.intrologin);
        introSigngup = (Button) findViewById(R.id.introsignup);

        introLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IntroLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        introSigngup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), IntroSignupActivity.class);
                intent.putExtra("user_type", "0");
                intent.putExtra("is_intro", "y");
                intent.putExtra("login_type", "회원가입");
                startActivity(intent);
                finish();


            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onBackPressed() {
        backPressClosehHandler.onBackPressed();

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Intro Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
