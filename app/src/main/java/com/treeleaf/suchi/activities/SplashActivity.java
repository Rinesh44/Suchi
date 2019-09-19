package com.treeleaf.suchi.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;


import androidx.annotation.Nullable;

import com.treeleaf.suchi.MainActivity;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.utils.LocaleHelper;


public class SplashActivity extends BaseActivity {
    private String TAG = SplashActivity.class.getSimpleName();

    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }

}
