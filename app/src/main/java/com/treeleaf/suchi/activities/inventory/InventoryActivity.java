package com.treeleaf.suchi.activities.inventory;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.inventory.stock.StockActivity;
import com.treeleaf.suchi.activities.report.ReportActivity;
import com.treeleaf.suchi.activities.sales.AddSalesActivity;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "InventoryActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.btn_sales)
    MaterialCardView mSales;
    @BindView(R.id.btn_stocks)
    MaterialCardView mStock;
    @BindView(R.id.btn_reports)
    MaterialCardView mReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        ButterKnife.bind(this);

        init();

        mSales.setOnClickListener(this);
        mStock.setOnClickListener(this);
        mReports.setOnClickListener(this);

        List<SalesStock> salesStockList = SalesStockRepo.getInstance().getAllSalesStockList();
        for (SalesStock salesStock : salesStockList
        ) {
            AppUtils.showLog(TAG, "salesStockId: " + salesStock.getId());
        }
    }

    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbarTitle.setText(getResources().getString(R.string.inventory));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sales:
                startActivity(new Intent(InventoryActivity.this, AddSalesActivity.class));
                break;
            case R.id.btn_stocks:
                startActivity(new Intent(InventoryActivity.this, StockActivity.class));
                break;

            case R.id.btn_reports:
                startActivity(new Intent(InventoryActivity.this, ReportActivity.class));
                break;
        }
    }
}
