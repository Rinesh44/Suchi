package com.treeleaf.suchi.activities.sales;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SalesDetailsActivity extends BaseActivity {
    private static final String TAG = "SalesDetailsActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.iv_item_image)
    CircleImageView mItemImage;
    @BindView(R.id.tv_item_name)
    TextView mItemName;
    @BindView(R.id.tv_item_id)
    TextView mItemId;
    @BindView(R.id.tv_unit_price)
    TextView mUnitPrice;
    @BindView(R.id.tv_qty)
    TextView mQuantity;
    @BindView(R.id.tv_selling_price)
    TextView mSellingPrice;
    @BindView(R.id.tv_unit)
    TextView mUnit;
    @BindView(R.id.tv_amount)
    TextView mAmount;

    private SalesStockDto saleItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_details);

        ButterKnife.bind(this);
        initialize();
        getIntentData();
        displayDetails();

    }

    private void displayDetails() {
        mItemName.setText(saleItems.getName());
        mItemId.setText(saleItems.getId());

        StringBuilder unitPriceBuilder = new StringBuilder();
        unitPriceBuilder.append("Rs. ");
        unitPriceBuilder.append(saleItems.getUnitPrice());
        mUnitPrice.setText(unitPriceBuilder);

        String imageUrl = saleItems.getPhotoUrl();
        AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(SalesDetailsActivity.this).load(imageUrl).apply(options).into(mItemImage);
        }

        mQuantity.setText(saleItems.getQuantity());

        StringBuilder sellingPriceBuilder = new StringBuilder();
        sellingPriceBuilder.append("Rs. ");
        sellingPriceBuilder.append(saleItems.getUnitPrice());
        mSellingPrice.setText(sellingPriceBuilder);

        StringBuilder amountBuilder = new StringBuilder();
        amountBuilder.append("Rs. ");
        amountBuilder.append(saleItems.getAmount());
        mAmount.setText(amountBuilder);

    }

    private void getIntentData() {
        Intent i = getIntent();
        saleItems = i.getParcelableExtra("sales_object");
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Sales Details");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
