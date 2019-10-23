package com.treeleaf.suchi.activities.forgotpassword;


import androidx.appcompat.widget.Toolbar;
import androidx.databinding.Bindable;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class ForgotPassword extends BaseActivity implements ForgotPasswordView {
    @Inject
    Endpoints endpoints;
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
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;


    private ForgotPasswordPresenter mPresenter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);
        getMyApplication(this).getAppComponent().inject(this);

        initialize();
        mPresenter = new ForgotPasswordPresenterImpl(endpoints, this);

        userId = getUserId();

        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = Objects.requireNonNull(mCode.getText()).toString().trim();
                String password = Objects.requireNonNull(mPassword.getText()).toString().trim();

                SuchiProto.PasswordReset passwordReset = SuchiProto.PasswordReset.newBuilder()
                        .setCode(Integer.valueOf(code))
                        .setNewPassword(password)
                        .setUserId(userId)
                        .build();

                showLoading();
                mPresenter.resetPassword(passwordReset);
            }
        });
    }

    private String getUserId() {
        Intent i = getIntent();
        return  i.getStringExtra("user_id");
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.forgot_password));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void resetPasswordSuccess() {
        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void resetPasswordFail(String msg) {
        showMessage(msg);
    }
}
