package com.treeleaf.suchi.activities.enterkey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.esewa.android.sdk.payment.ESewaConfiguration;
import com.esewa.android.sdk.payment.ESewaPayment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.activities.payment.PaymentActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class EnterKeyActivity extends BaseActivity implements EnterKeyView, View.OnClickListener {
    @Inject
    Endpoints endpoints;
    private static final String TAG = "EnterKeyActivity";
    @BindView(R.id.btn_start)
    MaterialButton mStart;
    @BindView(R.id.et_key)
    TextInputEditText mEnterKey;
    @BindView(R.id.btn_free_trial)
    TextView mFreeTrail;
    @BindView(R.id.btn_buy)
    MaterialButton mPurchase;

    private EnterKeyPresenter presenter;
    private String token, freeTrailKey, userId, keyId;
    private long createdDate, expiryDate;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_key);

        ButterKnife.bind(this);

        getMyApplication(this).getAppComponent().inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        presenter = new EnterKeyPresenterImpl(endpoints, this);

        token = getToken();
        userId = getUserId();
        mStart.setOnClickListener(this);
        mFreeTrail.setOnClickListener(this);
        mPurchase.setOnClickListener(this);

    }


    private String getToken() {
        Intent i = getIntent();
        return i.getStringExtra("token");
    }

    private String getUserId() {
        Intent i = getIntent();
        return i.getStringExtra("user_id");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                break;

            case R.id.btn_free_trial:
                createdDate = System.currentTimeMillis();
                freeTrailKey = UUID.randomUUID().toString().replace("-", "");
                keyId = UUID.randomUUID().toString().replace("-", "");

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, 10);

                expiryDate = cal.getTimeInMillis();

                AppUtils.showLog(TAG, "createdDate:" + createdDate);
                AppUtils.showLog(TAG, "expiryDate: " + expiryDate);


                SuchiProto.SuchiKey suchiKey = SuchiProto.SuchiKey.newBuilder()
                        .setKeyId(keyId)
                        .setKey(freeTrailKey)
                        .setCreatedAt(createdDate)
                        .setExpiresAt(expiryDate)
                        .setUserId(userId)
                        .build();

                presenter.freeTrail(token, suchiKey);

            case R.id.btn_buy:
                startActivity(new Intent(EnterKeyActivity.this, PaymentActivity.class));
                break;

        }
    }


    @Override
    public void freeTrialSuccess() {
        AppUtils.showLog(TAG, "free trail success");

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.FREE_TRIAL_KEY, freeTrailKey);
        editor.putLong(Constants.FREE_TRIAL_EXPIRY_DATE, expiryDate);
        editor.putLong(Constants.FREE_TRIAL_CREATED_DATE, createdDate);
        editor.putString(Constants.KEY_ID, keyId);
        editor.apply();

        Intent i = new Intent(EnterKeyActivity.this, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("free_trial", true);
        startActivity(i);
        finish();
    }

    @Override
    public void freeTrialFail(String msg) {
        AppUtils.showLog(TAG, msg);
    }
}
