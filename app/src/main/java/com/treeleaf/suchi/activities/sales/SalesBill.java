package com.treeleaf.suchi.activities.sales;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesBill extends BaseActivity {
    private static final String TAG = "SalesBill";
    public static final String EXTRA_TITLE = "updated_sale_object";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.ll_bill_holder)
    LinearLayout mBillHolder;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.btn_pay)
    MaterialButton mPay;
    @BindView(R.id.btn_add_to_credit)
    MaterialButton mAddToCredit;
/*    @BindView(R.id.fab_done)
    FloatingActionButton mDone;*/

    double totalAmount = 0;

    List<SalesStock> updatedSalesStockList;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_bill);

        ButterKnife.bind(this);

        initialize();

        Intent i = getIntent();
        updatedSalesStockList = i.getParcelableArrayListExtra(EXTRA_TITLE);

        inflateListInBillHolder();

        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSalesData();
            }

        });

        mAddToCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SalesBill.this, SalesAddCredit.class);
                AppUtils.showLog(TAG, "amount: " + totalAmount);
                i.putExtra("amount", String.valueOf(totalAmount));
                startActivity(i);
            }
        });


    }

    private void updateExistingSales() {

    }

    private void inflateListInBillHolder() {
        for (SalesStock salesStock : updatedSalesStockList
        ) {
            View view = getLayoutInflater().inflate(R.layout.bill_row, null);
            TextView itemName = view.findViewById(R.id.tv_item_name);
            TextView qty = view.findViewById(R.id.tv_qty);
            TextView price = view.findViewById(R.id.tv_price);
            TextView amount = view.findViewById(R.id.tv_amount);

            itemName.setText(salesStock.getName());

            StringBuilder quantityHolder = new StringBuilder();
            quantityHolder.append(salesStock.getQuantity());
            quantityHolder.append(" ");
            quantityHolder.append(salesStock.getUnit());

            qty.setText(quantityHolder);
            price.setText(salesStock.getUnitPrice());
            amount.setText(salesStock.getAmount());

            totalAmount += Double.valueOf(salesStock.getAmount());
            mBillHolder.addView(view);

            StringBuilder totalAmountBuilder = new StringBuilder();
            totalAmountBuilder.append("Rs. ");
            totalAmountBuilder.append(totalAmount);
            mTotalAmount.setText(totalAmountBuilder);

        }
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Billing Statement");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                saveSalesData();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void saveSalesData() {
        List<SalesStock> allSalesStock = SalesStockRepo.getInstance().getAllSalesStockList();
        //lists to prevent concurrent modification exception
        List<SalesStock> stocksToRemove = new ArrayList<>();
        List<SalesStock> stocksToAdd = new ArrayList<>();
        for (SalesStock salesStockCurrent : updatedSalesStockList
        ) {
            for (SalesStock salesStockDb : allSalesStock
            ) {
                if (salesStockDb.getId().equalsIgnoreCase(salesStockCurrent.getId())) {
                    stocksToRemove.add(salesStockCurrent);

                    String quantity = String.valueOf(Double.valueOf(salesStockDb.getQuantity()) +
                            Double.valueOf(salesStockCurrent.getQuantity()));

                    String amount = String.valueOf(Double.valueOf(quantity) *
                            Double.valueOf(salesStockCurrent.getUnitPrice()));

                    SalesStock salesStock = new SalesStock(salesStockCurrent.getId(), salesStockCurrent.getInventory_id(),
                            amount, quantity, salesStockCurrent.getUnit(), salesStockCurrent.getName(),
                            salesStockCurrent.getPhotoUrl(), salesStockCurrent.getUnitPrice(), false, System.currentTimeMillis());

                    stocksToAdd.add(salesStock);
                }
            }
        }

        updatedSalesStockList.removeAll(stocksToRemove);
        updatedSalesStockList.addAll(stocksToAdd);

        SalesStockRepo.getInstance().saveSalesStockList(updatedSalesStockList, new Repo.Callback() {
            @Override
            public void success(Object o) {
                Toast.makeText(SalesBill.this, "Sale item added", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, true);
                editor.apply();

                gotoSalesActivity();
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save sale item");
            }
        });
    }

    public void gotoSalesActivity() {
        Intent intent = new Intent(SalesBill.this, SalesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
