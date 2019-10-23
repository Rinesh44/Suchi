package com.treeleaf.suchi.activities.login;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.activities.enterkey.EnterKeyActivity;
import com.treeleaf.suchi.activities.forgotpassword.ForgotPassword;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.TreeleafProto;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.UserRepo;

import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.CustomProgress;

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
    @BindView(R.id.tv_forgot_password)
    TextView mForgotPassword;

    private LoginPresenter presenter;
    private SharedPreferences preferences;
    private CustomProgress customProgress;

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


        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(mUsername.getText()).toString().isEmpty()) {
                    mUsernameLayout.setErrorEnabled(true);
                    mUsernameLayout.setError("This field is required");
                    mUsername.requestFocus();
                    return;
                } else {
                    mUsernameLayout.setErrorEnabled(false);
                }

                showLoading();
                presenter.forgotPassword(mUsername.getText().toString());
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
    public void loginSuccess(SuchiRpcProto.SuchiBaseResponse baseResponse) {
        AppUtils.showLog(TAG, "login success");
        TreeleafProto.LoginResponse loginResponse = baseResponse.getLoginResponse();

        AppUtils.showLog(TAG, "KeyId: " + baseResponse.getSuchiKey().getKeyId());
        if (baseResponse.getSuchiKey().getKeyId().isEmpty()) {
            AppUtils.showLog(TAG, "key null");
            Intent i = new Intent(LoginActivity.this, EnterKeyActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("token", loginResponse.getToken());
            i.putExtra("user_id", loginResponse.getUser().getUserId());
            startActivity(i);
        } else {
            if (baseResponse.getError()) {
                showMessage(baseResponse.getMsg());
                return;
            }

            if (!baseResponse.getMsg().isEmpty()) showMessage(baseResponse.getMsg());

            gotoActivity(loginResponse);

        }

    }

    private void gotoActivity(TreeleafProto.LoginResponse loginResponse) {
        //show another progress dialog for fetching data
        customProgress = CustomProgress.getInstance();
        customProgress.showProgress(this, "Fetching data, please wait...", false);

        UserRepo.getInstance().saveUser(loginResponse, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "successfully saved user");

                //save to shared prefs

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constants.TOKEN, loginResponse.getToken());
                editor.putString(Constants.USER_ID, loginResponse.getUser().getUserId());

                editor.putString(Constants.USERNAME, loginResponse.getUser().getUsername());
                editor.putString(Constants.STORENAME, loginResponse.getUser().getStoreName());
                editor.putString(Constants.ADDRESS, loginResponse.getUser().getAddress());
                editor.putString(Constants.OWNERNAME, loginResponse.getUser().getOwnerName());
                editor.apply();


                String token = preferences.getString(Constants.TOKEN, "");
                if (token != null) presenter.getAllData(token);
                else {
                    customProgress.hideProgress();
                    Toast.makeText(LoginActivity.this, "Unable to get token", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "User save failed");
                customProgress.hideProgress();
                hideLoading();
            }
        });

    }

    @Override
    public void loginFail(String msg) {
        showMessage(msg);
//        customProgress.hideProgress();
        hideLoading();
    }

    @Override
    public void getAllDataSuccess() {
        AppUtils.showLog(TAG, "get all data succeeded");
        customProgress.hideProgress();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGGED_IN, true);
        editor.apply();

        Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void getAllDataFail(String msg) {
        customProgress.hideProgress();
        showMessage(msg);
    }

    @Override
    public void forgotPasswordSuccess() {
        Toast.makeText(this, "Please wait for code...", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(LoginActivity.this, ForgotPassword.class);
        startActivity(i);
    }

    @Override
    public void forgotPasswordFail(String msg) {
        showMessage(msg);
    }
}
