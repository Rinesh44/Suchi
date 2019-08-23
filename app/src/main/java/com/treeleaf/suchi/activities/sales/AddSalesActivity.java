package com.treeleaf.suchi.activities.sales;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import com.treeleaf.suchi.adapter.InventorySalesAdapter;
import com.treeleaf.suchi.adapter.StockSalesAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;


import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class AddSalesActivity extends BaseActivity implements View.OnClickListener, AddSalesView {
    private static final String TAG = "AddSalesActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.et_search)
    AppCompatAutoCompleteTextView mSearch;
    @BindView(R.id.iv_barcode)
    ImageView mBarcode;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.btn_sell)
    MaterialButton mSell;
    @BindView(R.id.ll_sale_detail_holder)
    LinearLayout mSaleDetails;
    @BindView(R.id.iv_item)
    CircleImageView mItemImage;
    @BindView(R.id.tv_sku_name)
    TextView mSkuName;
    @BindView(R.id.rv_sale_stocks)
    RecyclerView mSaleStocksRecycler;

    private List<Inventory> inventoryList = new ArrayList<>();
    private List<Units> unitList = new ArrayList<>();
    private String selectedItemId, selectedItemUnitId;
    private List<String> unitItems = new ArrayList<>();
    private ArrayAdapter<String> unitItemsAdapter;
    private AddSalesPresenter presenter;
    List<SalesStock> salesStockList = new ArrayList<>();
    private String token, userId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);


        ButterKnife.bind(this);

        initialize();
        setUpSearch();

        token = sharedPreferences.getString(Constants.TOKEN, "");
        userId = sharedPreferences.getString(Constants.USER_ID, "");
//        setUpUnitSpinner();
//        updateTotalAmount();

        mBarcode.setOnClickListener(this);
        mSell.setOnClickListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("item_details"));

        mSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
                InventoryDto selectedItem = (InventoryDto) adapterView.getItemAtPosition(i);
                mSearch.setText(selectedItem.getSku().getName());

//                int defaultUnitPosition = unitItemsAdapter.getPosition(selectedItem.getSku().getDefaultUnit());
//                mUnit.setSelection(defaultUnitPosition);


                mSkuName.setText(selectedItem.getSku().getName());
                StringBuilder itemCostBuilder = new StringBuilder();
                itemCostBuilder.append("Rs. ");
                itemCostBuilder.append(selectedItem.getSku().getUnitPrice());


                String imageUrl = selectedItem.getSku().getPhoto_url();
                AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
                if (!imageUrl.isEmpty()) {
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.ic_stock)
                            .error(R.drawable.ic_stock);

                    Glide.with(AddSalesActivity.this).load(imageUrl).apply(options).into(mItemImage);
                }

                mSaleStocksRecycler.setLayoutManager(new LinearLayoutManager(AddSalesActivity.this, RecyclerView.HORIZONTAL, false));
                StockSalesAdapter stockSalesAdapter = new StockSalesAdapter(AddSalesActivity.this, selectedItem);
                mSaleStocksRecycler.setAdapter(stockSalesAdapter);

                mSaleDetails.setVisibility(View.VISIBLE);
            }
        });

    }


    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Item Sales");


        getMyApplication(this).getAppComponent().inject(this);

        presenter = new AddSalesPresenterImpl(endpoints, this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String amount = intent.getStringExtra("amount");
            String qty = intent.getStringExtra("quantity");
            String unit = intent.getStringExtra("unit");
            String id = intent.getStringExtra("inventory_stock_id");
            String inventoryId = intent.getStringExtra("inventory_id");
            AppUtils.showLog(TAG, "data: " + amount + qty + unit + id);

            if (salesStockList.isEmpty()) {
                SalesStock salesStock = new SalesStock(id, inventoryId, amount, qty, unit);
                salesStockList.add(salesStock);
            } else {
                // to prevent concurrent modification exception
                List<SalesStock> newSalesStock = new ArrayList<SalesStock>();
                for (int i = 0; i < salesStockList.size(); i++) {
                    if (salesStockList.get(i).getId().equals(id)) {
                        AppUtils.showLog(TAG, "if");
                        salesStockList.remove(salesStockList.get(i));
                        newSalesStock.add(new SalesStock(id, inventoryId, amount, qty, unit));
//                        newSalesStockList.add(new SalesStock(id, amount, qty, unit));
//                        salesStockList.set(salesStockList.indexOf(salesStock), new SalesStock(id, amount, qty, unit));
                    } else {
                        AppUtils.showLog(TAG, "else");
                        SalesStock salesStockModel = new SalesStock(id, inventoryId, amount, qty, unit);
                        newSalesStock.add(salesStockModel);
                    }
                }

                salesStockList.addAll(newSalesStock);

//                salesStockList.addAll(newSalesStockList);

              /*  mElements.addAll(thingsToBeAdd);


                for (SalesStock salesStock : salesStockList
                ) {
                    if (salesStock.getId().equals(id)) {
                        salesStockList.set(salesStockList.indexOf(salesStock), new SalesStock(id, amount, qty, unit));
                    } else {
                        SalesStock salesStockModel = new SalesStock(id, amount, qty, unit);
                        salesStockList.add(salesStockModel);
                    }
                }*/
            }

            double sum = 0;
            AppUtils.showLog(TAG, "listSize: " + salesStockList.size());
            for (SalesStock salesStock : salesStockList
            ) {
                sum = sum + Double.valueOf(salesStock.getAmount());
            }

            mTotalAmount.setText(String.valueOf(sum));
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_barcode:
                break;


            case R.id.btn_sell:

                List<SuchiProto.SaleInventory> saleInventoryList = new ArrayList<>();
                for (SalesStock salesStock : salesStockList
                ) {
                    SuchiProto.SaleInventory saleInventory = SuchiProto.SaleInventory.newBuilder()
                            .setInventoryId(salesStock.getInventory_id())
                            .setInventoryStockId(salesStock.getId())
                            .setQuantity(Integer.valueOf(salesStock.getQuantity()))
                            .setAmount(Double.valueOf(salesStock.getAmount()))
                            .setUnitId(salesStock.getUnit())
                            .build();

                    saleInventoryList.add(saleInventory);
                }

                SuchiProto.Sale sale = SuchiProto.Sale.newBuilder()
                        .addAllSaleInventories(saleInventoryList)
                        .setAmount(Double.valueOf(mTotalAmount.getText().toString().trim()))
                        .build();

                showLoading();
                presenter.addSales(token, sale);
                break;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {

            unregisterReceiver(mMessageReceiver);
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        }

    }

    private void setUpSearch() {
        inventoryList = InventoryRepo.getInstance().getSyncedInventories();
        List<InventoryDto> inventoryDtoList = new ArrayList<>();
        inventoryDtoList = mapInventoriesToInventoryDto(inventoryList);

        InventorySalesAdapter inventoryListAdapter = new InventorySalesAdapter(this, R.layout.autocomplete_search_item, inventoryDtoList);

        mSearch.setAdapter(inventoryListAdapter);
    }

    private List<InventoryDto> mapInventoriesToInventoryDto(List<Inventory> inventories) {
        List<InventoryDto> inventoryDtoList = new ArrayList<>();
        for (Inventory inventory : inventories
        ) {
            InventoryDto inventoryDto = new InventoryDto();
            inventoryDto.setInventory_id(inventory.getInventory_id());
            inventoryDto.setExpiryDate(inventory.getExpiryDate());
            inventoryDto.setSkuId(inventory.getSkuId());
            inventoryDto.setUser_id(inventory.getUser_id());
            inventoryDto.setSynced(inventory.isSynced());
            StockKeepingUnitDto skuDto = mapSkuToDto(inventory.getSku());
            inventoryDto.setSku(skuDto);
            RealmList<InventoryStocksDto> inventoryStocksDtoList = mapInventoryStocksToDto(inventory.getInventoryStocks());
            inventoryDto.setInventoryStocks(inventoryStocksDtoList);

            inventoryDtoList.add(inventoryDto);
        }
        return inventoryDtoList;
    }


    private RealmList<InventoryStocksDto> mapInventoryStocksToDto(List<InventoryStocks> inventoryStocks) {
        RealmList<InventoryStocksDto> inventoryStockDtoList = new RealmList<>();
        for (InventoryStocks inventoryStock : inventoryStocks
        ) {
            InventoryStocksDto inventoryStocksDto = new InventoryStocksDto();
            inventoryStocksDto.setId(inventoryStock.getId());
            inventoryStocksDto.setMarkedPrice(inventoryStock.getMarkedPrice());
            inventoryStocksDto.setSalesPrice(inventoryStock.getSalesPrice());
            inventoryStocksDto.setQuantity(inventoryStock.getQuantity());
            inventoryStocksDto.setSynced(inventoryStock.isSynced());
            inventoryStocksDto.setUnitId(inventoryStock.getUnitId());

            inventoryStockDtoList.add(inventoryStocksDto);
        }

        return inventoryStockDtoList;
    }

    private StockKeepingUnitDto mapSkuToDto(StockKeepingUnit sku) {
        StockKeepingUnitDto skuDto = new StockKeepingUnitDto();
        skuDto.setId(sku.getId());
        skuDto.setName(sku.getName());
        skuDto.setPhoto_url(sku.getPhoto_url());
        skuDto.setCode(sku.getCode());
        skuDto.setDesc(sku.getDesc());
        skuDto.setUnitPrice(sku.getUnitPrice());
        skuDto.setSynced(sku.isSynced());
        skuDto.setBrand(sku.getBrand());
        skuDto.setSubBrands(sku.getSubBrands());
        skuDto.setUnits(sku.getUnits());
        skuDto.setCategories(sku.getCategories());

        return skuDto;
    }

    @Override
    public void addSalesSuccess() {
        AppUtils.showLog(TAG, "sales add success");
        Toast.makeText(this, "Sales added", Toast.LENGTH_SHORT).show();

        presenter.getSales(token);

        finish();
    }

    @Override
    public void addSalesFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void getSalesFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void getSalesSuccess() {
        AppUtils.showLog(TAG, "get sales success");
    }
}
