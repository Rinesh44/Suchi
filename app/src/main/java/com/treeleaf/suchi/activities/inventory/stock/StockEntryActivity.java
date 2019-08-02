package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockEntryActivity extends BaseActivity {
    @BindView(R.id.il_name)
    TextInputLayout mNameLayout;
    @BindView(R.id.et_name)
    TextInputEditText mName;
    @BindView(R.id.sp_brand)
    Spinner mBrand;
    @BindView(R.id.sp_sub_brand)
    Spinner mSubBrand;
    @BindView(R.id.sp_category)
    Spinner mCategory;
    @BindView(R.id.et_desc)
    TextInputEditText mDesc;
    @BindView(R.id.il_desc)
    TextInputLayout mDescLayout;
    @BindView(R.id.sp_unit)
    Spinner mUnit;
    @BindView(R.id.et_unit_price)
    TextInputEditText mUnitPrice;
    @BindView(R.id.il_unit_price)
    TextInputLayout mUnitPriceLayout;
    @BindView(R.id.iv_sku)
    ImageView mSku;
    @BindView(R.id.btn_add)
    MaterialButton mAdd;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_entry);


        ButterKnife.bind(this);

        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mToolbarTitle.setText("SKU Entry Form");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
