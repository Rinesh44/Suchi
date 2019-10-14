package com.treeleaf.suchi.activities.credit;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.btn_credit_history)
    MaterialCardView mHistory;
    @BindView(R.id.btn_creditors)
    MaterialCardView mCreditors;
    @BindView(R.id.btn_add_creditors)
    MaterialCardView mAddCreditors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        ButterKnife.bind(this);

        init();

        mHistory.setOnClickListener(this);
        mAddCreditors.setOnClickListener(this);
        mCreditors.setOnClickListener(this);
    }

    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbarTitle.setText(getResources().getString(R.string.credit));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_credit_history:
                startActivity(new Intent(CreditActivity.this, CreditHistory.class));
                break;

            case R.id.btn_creditors:

                break;

            case R.id.btn_add_creditors:
                startActivity(new Intent(CreditActivity.this, AddCreditor.class));
                break;
        }
    }
}