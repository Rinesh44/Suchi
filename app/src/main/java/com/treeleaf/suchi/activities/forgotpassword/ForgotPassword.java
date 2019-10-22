package com.treeleaf.suchi.activities.forgotpassword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class ForgotPassword extends BaseActivity implements ForgotPasswordView {
    @BindView(R.id.il_code)
    TextInputLayout mCodeLayout;
    @BindView(R.id.et_code)
    TextInputEditText mCode;
    @BindView(R.id.il_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.et_password)
    TextInputEditText mPassword;
    @BindView(R.id.btn_reset_password)
    MaterialButton mResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
        getMyApplication(this).getAppComponent().inject(this);


        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    @Override
    public void resetPasswordSuccess() {

    }

    @Override
    public void resetPasswordFail(String msg) {

    }
}
