package com.treeleaf.suchi.activities.register;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class RegisterActivity extends BaseActivity implements RegisterView {
    private static final String TAG = "RegisterActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.et_store_name)
    TextInputEditText mStoreName;
    @BindView(R.id.il_store_name)
    TextInputLayout mStoreNameLayout;
    @BindView(R.id.et_address)
    TextInputEditText mAddress;
    @BindView(R.id.il_address)
    TextInputLayout mAddressLayout;
    @BindView(R.id.et_phone)
    TextInputEditText mPhone;
    @BindView(R.id.il_phone)
    TextInputLayout mPhoneLayout;
    @BindView(R.id.et_owner_name)
    TextInputEditText mOwnerName;
    @BindView(R.id.il_owner_name)
    TextInputLayout mOwnerNameLayout;
    @BindView(R.id.et_username)
    TextInputEditText mUsername;
    @BindView(R.id.il_username)
    TextInputLayout mUserNameLayout;
    @BindView(R.id.et_password)
    TextInputEditText mPassword;
    @BindView(R.id.il_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.btn_register)
    MaterialButton mRegister;

    private RegisterPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_for_keyboard_glitch));
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        init();
        getMyApplication(this).getAppComponent().inject(this);
        presenter = new RegisterPresenterImpl(endpoints, this);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateFieldsAndRegister();

            }
        });
    }

    private void validateFieldsAndRegister() {
        if (Objects.requireNonNull(mStoreName.getText()).toString().isEmpty()) {
            mStoreNameLayout.setErrorEnabled(true);
            mStoreNameLayout.setError("This field is required");
            mStoreName.requestFocus();
            return;
        } else {
            mStoreNameLayout.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(mAddress.getText()).toString().isEmpty()) {
            mAddressLayout.setErrorEnabled(true);
            mAddressLayout.setError("This field is required");
            mAddress.requestFocus();
            return;
        } else {
            mAddressLayout.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(mPhone.getText()).toString().isEmpty()) {
            mPhoneLayout.setErrorEnabled(true);
            mPhoneLayout.setError("This field is required");
            mPhone.requestFocus();
            return;
        } else {
            mPhoneLayout.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(mOwnerName.getText()).toString().isEmpty()) {
            mOwnerNameLayout.setErrorEnabled(true);
            mOwnerNameLayout.setError("This field is required");
            mOwnerName.requestFocus();
            return;
        } else {
            mOwnerNameLayout.setErrorEnabled(false);
        }

        if (Objects.requireNonNull(mUsername.getText()).toString().isEmpty()) {
            mUserNameLayout.setErrorEnabled(true);
            mUserNameLayout.setError("This field is required");
            mUsername.requestFocus();
            return;
        } else {
            mUserNameLayout.setErrorEnabled(false);
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
        presenter.register(mStoreName.getText().toString().trim(), mAddress.getText().toString().trim(),
                mPhone.getText().toString().trim(), mOwnerName.getText().toString().trim(),
                mUsername.getText().toString().trim(), mPassword.getText().toString().trim());

    }

    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mToolbarTitle.setText("Registration");
    }

    @Override
    public void registerSuccess() {
        AppUtils.showLog(TAG, "Registered successfully");
        Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void registerFail(String msg) {
        showMessage(msg);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
