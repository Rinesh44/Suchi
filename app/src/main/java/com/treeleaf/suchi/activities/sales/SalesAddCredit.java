package com.treeleaf.suchi.activities.sales;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class SalesAddCredit extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SalesAddCredit";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.btn_create_new_creditor)
    MaterialButton mCreateNewEditor;
    @BindView(R.id.btn_add_to_credit)
    MaterialButton mAddToCredit;
    @BindView(R.id.actv_creditor)
    AutoCompleteTextView mSearchCreditor;
    @BindView(R.id.tv_amount)
    TextView mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_add_credit);

        ButterKnife.bind(this);

        initialize();
        getAmount();
        mAddToCredit.setOnClickListener(this);
        mCreateNewEditor.setOnClickListener(this);
    }

    private void getAmount() {
        Intent i = getIntent();
        String amount = i.getStringExtra("amount");
        AppUtils.showLog(TAG, "amount: " + amount);
        setUpAmount(amount);

    }

    private void setUpAmount(String amount) {
        StringBuilder amountBuilder = new StringBuilder();
        amountBuilder.append("Rs. ");
        amountBuilder.append(amount);
        mAmount.setText(amountBuilder);
    }


    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Add to Credit");

        getMyApplication(this).getAppComponent().inject(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_to_credit:
                Toast.makeText(this, "add to credit clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_create_new_creditor:
                Toast.makeText(this, "create new creditor", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
