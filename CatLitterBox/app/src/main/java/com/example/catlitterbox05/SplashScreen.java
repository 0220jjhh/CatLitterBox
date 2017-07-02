package com.example.catlitterbox05;

/**
 * Created by 박지현 on 2017-05-25.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withFullScreen()
                .withTargetActivity(AuthActivity.class)
                .withSplashTimeOut(2000)
                .withBackgroundResource(R.drawable.app_start);

        View view = config.create();

        setContentView(view);
    }
}
