package com.treeleaf.suchi.activities.credit;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.btn_credit_entry)
    MaterialButton mEntry;
    @BindView(R.id.btn_credit_history)
    MaterialButton mHistory;
    @BindView(R.id.btn_creditors)
    MaterialButton mAddCreditors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        ButterKnife.bind(this);

        init();

        mEntry.setOnClickListener(this);
        mHistory.setOnClickListener(this);
        mAddCreditors.setOnClickListener(this);
    }

    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbarTitle.setText("Credit");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_credit_entry:
                break;

            case R.id.btn_credit_history:
                break;

            case R.id.btn_creditors:
                startActivity(new Intent(CreditActivity.this, AddCreditor.class));
                break;
        }
    }
}