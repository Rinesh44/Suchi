package com.treeleaf.suchi.activities.sales;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.credit.CreditEntry;
import com.treeleaf.suchi.activities.inventory.InventoryActivity;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.InventoryStocksRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesRepo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

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

    private double totalAmount = 0;

    List<SalesStock> updatedSalesStockList;
    private SharedPreferences preferences;
    private String userId;

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
                Intent i = new Intent(SalesBill.this, CreditEntry.class);
                i.putParcelableArrayListExtra("sales_stock_list", (ArrayList<? extends Parcelable>) updatedSalesStockList);
                startActivity(i);
            }
        });


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
        mToolbarTitle.setText(getResources().getString(R.string.billing_statement));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = preferences.getString(Constants.USER_ID, "");

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
        deductInventoryStockQuantity();

        RealmList<SalesStock> realmUpdatedList = new RealmList<>();
        realmUpdatedList.addAll(updatedSalesStockList);

        String saleId = UUID.randomUUID().toString().replace("-", "");
        Sales sales = new Sales(saleId, String.valueOf(totalAmount), System.currentTimeMillis(), 0,
                false, userId, false, "0", realmUpdatedList);

        SalesRepo.getInstance().saveSales(sales, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "sales saved");
                Toast.makeText(SalesBill.this, "Items sold", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, true);
                editor.apply();

                gotoSalesActivity();

            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save sales");
            }
        });


   /*     SalesStockRepo.getInstance().saveSalesStockList(updatedSalesStockList, new Repo.Callback() {
            @Override
            public void success(Object o) {
                Toast.makeText(SalesBill.this, "Sale item added", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, true);
                editor.apply();

                gotoInventoryActivity();
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save sale item");
            }
        });*/


    }

    private void deductInventoryStockQuantity() {
        List<InventoryStocks> updateInventoryStockList = new ArrayList<>();
        List<InventoryStocks> allInventoryStock = InventoryStocksRepo.getInstance().getAllInventoryStocks();
        if (allInventoryStock != null) {
            for (InventoryStocks inventoryStocks : allInventoryStock
            ) {
                for (SalesStock salesStock : updatedSalesStockList
                ) {
                    if (inventoryStocks.getId().equalsIgnoreCase(salesStock.getId())) {
                        int remainingQuantity = Integer.valueOf(inventoryStocks.getQuantity()) -
                                Integer.valueOf(salesStock.getQuantity());


                        InventoryStocks updatedInventoryStock = new InventoryStocks(inventoryStocks.getId(),
                                inventoryStocks.getInventory_id(), String.valueOf(remainingQuantity),
                                inventoryStocks.getMarkedPrice(), inventoryStocks.getSalesPrice(),
                                inventoryStocks.getUnitId(), inventoryStocks.isSynced());

                        updateInventoryStockList.add(updatedInventoryStock);
                    }
                }
            }
        } else AppUtils.showLog(TAG, "list is null ");

        InventoryStocksRepo.getInstance().saveInventoryStocks(updateInventoryStockList, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "inventoryStock Updated");
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to update inventory stocks");
            }
        });
    }

    public void gotoSalesActivity() {
        Intent intent = new Intent(SalesBill.this, AddSalesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
