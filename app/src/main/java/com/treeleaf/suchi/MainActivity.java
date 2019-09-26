package com.treeleaf.suchi;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.activities.login.LoginActivity;
import com.treeleaf.suchi.activities.register.RegisterActivity;
import com.treeleaf.suchi.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final int PERMISSIONS_CODE = 999;
    @BindView(R.id.btn_login)
    MaterialButton mLogin;
    @BindView(R.id.btn_sign_up)
    TextView mSignUp;


    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        checkRequiredPermissions();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLoggedIn = preferences.getBoolean(Constants.LOGGED_IN, false);
        if (isLoggedIn) {
            Intent i = new Intent(MainActivity.this, DashboardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        mLogin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);


    }

    private void checkRequiredPermissions() {
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                && hasPermission((Manifest.permission.WRITE_EXTERNAL_STORAGE))
                && hasPermission(Manifest.permission.CAMERA)) {
            return;
        }

        requestPermissionsSafely(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_CODE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_up:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;

            case R.id.btn_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

        }
    }
}
