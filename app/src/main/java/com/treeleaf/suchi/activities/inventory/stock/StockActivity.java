package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.StockAdapter;
import com.treeleaf.suchi.api.Endpoints;

import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.NetworkUtils;
import com.treeleaf.suchi.viewmodel.InventoryListViewModel;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.realm.RealmList;
import io.realm.RealmResults;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class StockActivity extends BaseActivity implements StockView {
    private static final String TAG = "StockActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.rv_stock)
    RecyclerView mRecyclerViewStock;
    @BindView(R.id.fab_add)
    FloatingActionButton mAddStock;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_no_stocks)
    TextView mNoStocks;
    @BindView(R.id.et_search)
    EditText mSearch;


    private StockAdapter mStockAdapter;
    private StockPresenter presenter;
    private String token;
    private SharedPreferences preferences;
    private InventoryListViewModel inventoryListViewModel;
    private List<InventoryDto> inventoryDtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        ButterKnife.bind(this);

        init();

        token = preferences.getString(Constants.TOKEN, "");
        mRecyclerViewStock.setLayoutManager(new LinearLayoutManager(this));
        mStockAdapter = new StockAdapter(StockActivity.this, inventoryDtoList);

        inventoryListViewModel = ViewModelProviders.of(this).get(InventoryListViewModel.class);

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mStockAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inventoryListViewModel.getInventories().observe(this, new Observer<RealmResults<Inventory>>() {
            @Override
            public void onChanged(RealmResults<Inventory> inventories) {
                Toast.makeText(StockActivity.this, "on changed", Toast.LENGTH_SHORT).show();
                AppUtils.showLog(TAG, "inventory size: " + inventories.size());
                if (inventories.size() == 0) {
                    mNoStocks.setVisibility(View.VISIBLE);
                } else {
                    mNoStocks.setVisibility(View.GONE);
                    inventoryDtoList = mapInventoriesToInventoryDp(inventories);
                    mStockAdapter = new StockAdapter(StockActivity.this, inventoryDtoList);
                    mRecyclerViewStock.setAdapter(mStockAdapter);
                    mStockAdapter.submitList(inventoryDtoList);

                    mStockAdapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(InventoryDto inventory) {
                            if (!inventory.isSynced()) {
                                Toast.makeText(StockActivity.this, "Please sync item to view details", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(StockActivity.this, StockDetails.class);
                                i.putExtra("inventory_object", inventory);
                                startActivity(i);
                            }
                        }
                    });

                }

            }
        });


        mAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockActivity.this, SearchStock.class));
            }
        });


        hideFabWhenScrolled();

    /*    if (NetworkUtils.isNetworkConnected(this)) {
            if (token != null) {
                showLoading();
                presenter.getStockItems(token);
            } else Toast.makeText(this, "Unable to get token", Toast.LENGTH_SHORT).show();
        } else {
            loadOfflineData();
        }*/

    }

    private List<InventoryDto> mapInventoriesToInventoryDp(RealmResults<Inventory> inventories) {
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


    private void hideFabWhenScrolled() {

        mRecyclerViewStock.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && mAddStock.isShown())
                    mAddStock.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mAddStock.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }


    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Stocks");

        getMyApplication(this).getAppComponent().inject(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        presenter = new StockPresenterImpl(this, endpoints);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidateOptionsMenu();

    }


    @Override
    public void addUnsyncedInventoriesSuccess(List<Inventory> inventoryList) {
        AppUtils.showLog(TAG, "add unsynced inventories success");
        Toast.makeText(this, "Items synced", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.DATA_REMAINING_TO_SYNC, false);
        editor.apply();

        invalidateOptionsMenu();
        mStockAdapter.notifyDataSetChanged();

        InventoryRepo.getInstance().saveInventoryList(inventoryList, new Repo.Callback() {
            @Override
            public void success(Object o) {

            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save synced inventory list");
            }
        });

    }


    @Override
    public void addUnsyncedInventoriesFail(String msg) {
        showMessage(msg);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (preferences.getBoolean(Constants.DATA_REMAINING_TO_SYNC, false))
            getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (NetworkUtils.isNetworkConnected(this)) {
                    List<Inventory> unsyncedInventories = InventoryRepo.getInstance().getUnsyncedInventories();
                    if (token != null) {
                        if (!unsyncedInventories.isEmpty()) {
                            showLoading();
                            presenter.addUnsyncedInventories(token, unsyncedInventories);
                        } else
                            Toast.makeText(this, "No data found to sync", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Please connect to internet.", Toast.LENGTH_SHORT).show();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
