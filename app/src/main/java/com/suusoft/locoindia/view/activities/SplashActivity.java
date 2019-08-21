package com.suusoft.locoindia.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.suusoft.locoindia.R;
import com.suusoft.locoindia.utils.AppUtil;


/**
 * Created by Suusoft on 01/03/2018.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash1);

        onContinue();
        AppUtil.getFacebookKeyHash(this);
    }

    private void onContinue() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, SplashLoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }


}
