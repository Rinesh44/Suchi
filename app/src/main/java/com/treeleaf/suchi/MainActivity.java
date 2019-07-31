package com.treeleaf.suchi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.activities.enterkey.EnterKeyActivity;
import com.treeleaf.suchi.activities.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_login)
    MaterialButton mLogin;
    @BindView(R.id.btn_sign_up)
    MaterialButton mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mLogin.setOnClickListener(this);
        mSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_up:
                startActivity(new Intent(MainActivity.this, EnterKeyActivity.class));
                break;

            case R.id.btn_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

        }
    }
}
