package com.treeleaf.suchi.activities.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.activities.enterkey.EnterKeyActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.AccountProto;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.UserRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class LoginActivity extends BaseActivity implements LoginView {
    private static final String TAG = "LoginActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.il_username)
    TextInputLayout mUsernameLayout;
    @BindView(R.id.et_username)
    TextInputEditText mUsername;
    @BindView(R.id.il_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.et_password)
    TextInputEditText mPassword;
    @BindView(R.id.btn_login)
    MaterialButton mLogin;

    private LoginPresenter presenter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_for_keyboard_glitch));
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        getMyApplication(this).getAppComponent().inject(this);

        presenter = new LoginPresenterImpl(endpoints, this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndLogin();
            }
        });


    }

    private void validateAndLogin() {
        if (Objects.requireNonNull(mUsername.getText()).toString().isEmpty()) {
            mUsernameLayout.setErrorEnabled(true);
            mUsernameLayout.setError("This field is required");
            mUsername.requestFocus();
            return;
        } else {
            mUsernameLayout.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(mPassword.getText()).toString().isEmpty()) {
            mPasswordLayout.setErrorEnabled(true);
            mPasswordLayout.setError("This field is required");
            mPassword.requestFocus();
            return;
        } else {
            mPasswordLayout.setErrorEnabled(false);
        }

        showLoading();
        presenter.login(mUsername.getText().toString().trim(), mPassword.getText().toString().trim());
    }

    @Override
    public void loginSuccess(AccountProto.LoginResponse loginResponse) {
        AppUtils.showLog(TAG, "login success");

        int loginStatus = loginResponse.getUser().getStatus().getNumber();

        switch (loginStatus) {
            case 1:
                gotoActivity(loginResponse, loginStatus);
                break;

            case 2:
                showMessage("Sorry, your account has been suspended");
                break;

            case 3:
                showMessage("Sorry, your account has been deleted");
                break;

            case 4:
                gotoActivity(loginResponse, loginStatus);
                break;

        }


    }

    private void gotoActivity(AccountProto.LoginResponse loginResponse, int status) {

        if (status == 1) {
            UserRepo.getInstance().saveUser(loginResponse, new Repo.Callback() {
                @Override
                public void success(Object o) {
                    AppUtils.showLog(TAG, "successfully saved user");
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Constants.LOGGED_IN, true);
                    editor.apply();

                    Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "User save failed");
                }
            });
        } else {
            Intent i = new Intent(LoginActivity.this, EnterKeyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    @Override
    public void loginFail(String msg) {
        showMessage(msg);
    }
}
