package com.treeleaf.suchi.activities.enterkey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EnterKeyActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.btn_start)
    MaterialButton mStart;
    @BindView(R.id.et_key)
    TextInputEditText mEnterKey;
    @BindView(R.id.btn_free_trial)
    TextView mFreeTrail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_key);

        ButterKnife.bind(this);

        mStart.setOnClickListener(this);
        mFreeTrail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                break;

            case R.id.btn_free_trial:
                Intent i = new Intent(EnterKeyActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
                break;
        }
    }
}
