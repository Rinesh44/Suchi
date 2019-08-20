package com.treeleaf.suchi.activities.sales;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class AddSalesActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.et_search)
    AppCompatAutoCompleteTextView mSearch;
    @BindView(R.id.iv_barcode)
    ImageView mBarcode;
    @BindView(R.id.tv_item_title)
    TextView mItemName;
    @BindView(R.id.tv_item_cost)
    TextView mItemCost;
    @BindView(R.id.tv_quantity)
    EditText mQuantity;
    @BindView(R.id.sp_unit)
    AppCompatSpinner mUnit;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.btn_sell)
    MaterialButton mSell;
    @BindView(R.id.btn_increment)
    ImageButton mIncrement;
    @BindView(R.id.btn_decrement)
    ImageButton mDecrement;
    @BindView(R.id.ll_sale_detail_holder)
    LinearLayout mSaleDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);


        ButterKnife.bind(this);

        initialize();
        mBarcode.setOnClickListener(this);
        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mSell.setOnClickListener(this);
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Items Sales");

        getMyApplication(this).getAppComponent().inject(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_barcode:
                break;

            case R.id.btn_increment:
                break;

            case R.id.btn_decrement:
                break;

            case R.id.btn_sell:
                break;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
