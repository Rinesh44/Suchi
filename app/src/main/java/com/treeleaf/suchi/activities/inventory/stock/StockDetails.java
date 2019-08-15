package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.utils.AppUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class StockDetails extends BaseActivity {
    private static final String TAG = "StockDetails";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_sku_name)
    TextView mSkuName;
    @BindView(R.id.tv_marked_price)
    TextView mMarkedPrice;
    @BindView(R.id.tv_selling_price)
    TextView mSellingPrice;
    @BindView(R.id.tv_quantity)
    TextView mQuantity;
    @BindView(R.id.tv_unit)
    TextView mUnit;
    @BindView(R.id.tv_unit_price)
    TextView mUnitPrice;
    @BindView(R.id.tv_brand)
    TextView mBrand;
    @BindView(R.id.tv_sub_brand)
    TextView mSubBrand;
    @BindView(R.id.tv_category)
    TextView mCategory;
    @BindView(R.id.tv_desc)
    TextView mDescription;
    @BindView(R.id.iv_item)
    CircleImageView mItemImage;
    @BindView(R.id.tv_expiry_date)
    TextView mExpiryDate;

    private Inventory inventory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);

        ButterKnife.bind(this);
        initialize();

        getIntentData();

        displayDetails();

    }

    private void displayDetails() {
        mSkuName.setText(inventory.getSku().getName());
        mUnit.setText(inventory.getSku().getUnits().getName());
        mUnitPrice.setText(inventory.getSku().getUnitPrice());
        mBrand.setText(inventory.getSku().getBrand().getName());
        mSubBrand.setText(inventory.getSku().getSubBrands().getName());
        mCategory.setText(inventory.getSku().getCategories().getName());
        mDescription.setText(inventory.getSku().getDesc());

        String expiryDate = getDateSimple(Long.valueOf(inventory.getExpiryDate()));
        mExpiryDate.setText(expiryDate);

        String imageUrl = inventory.getSku().getPhoto_url();
        AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(StockDetails.this).load(imageUrl).apply(options).into(mItemImage);
        }
    }

    private void getIntentData() {
        Intent i = getIntent();
        inventory = i.getParcelableExtra("inventory_object");

    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Stock Details");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
