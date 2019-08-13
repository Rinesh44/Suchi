package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Network;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.StockAdapter;
import com.treeleaf.suchi.api.Endpoints;

import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.NetworkUtils;
import com.treeleaf.suchi.viewmodel.InventoryListViewModel;


import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import io.realm.RealmChangeListener;

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


    private StockAdapter mStockAdapter;
    private StockPresenter presenter;
    private String token;
    private SharedPreferences preferences;
    private RealmResults<Inventory> inventoryListOffline;
    private InventoryListViewModel inventoryListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        ButterKnife.bind(this);

        init();

        token = preferences.getString(Constants.TOKEN, "");
        mRecyclerViewStock.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewStock.setHasFixedSize(true);

        mStockAdapter = new StockAdapter(this);
        mRecyclerViewStock.setAdapter(mStockAdapter);

        inventoryListViewModel = ViewModelProviders.of(this).get(InventoryListViewModel.class);

        inventoryListViewModel.getInventories().observe(this, new Observer<RealmResults<Inventory>>() {
            @Override
            public void onChanged(RealmResults<Inventory> inventories) {
                Toast.makeText(StockActivity.this, "on changed", Toast.LENGTH_SHORT).show();
                mStockAdapter.submitList(inventories);

            }
        });


        mAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockActivity.this, SearchStock.class));
            }
        });

        mStockAdapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Inventory inventory) {
                //TODO
                //show stock details activity
                Toast.makeText(StockActivity.this, "item clicked", Toast.LENGTH_SHORT).show();
            }
        });

        hideFabWhenScrolled();

        if (NetworkUtils.isNetworkConnected(this)) {
            if (token != null) {
                showLoading();
                presenter.getStockItems(token);
            } else Toast.makeText(this, "Unable to get token", Toast.LENGTH_SHORT).show();
        } else {
            loadOfflineData();
        }

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
    }

    private void loadOfflineData() {
        AppUtils.showLog(TAG, "loadOfflineData()");

//        if (!inventoryListOffline.isEmpty()) mStockAdapter.submitList(inventoryListOffline);

    }

    @Override
    public void getStockItemsSuccess(List<Inventory> inventoryList) {
        AppUtils.showLog(TAG, "get stocks success");

        InventoryRepo.getInstance().saveInventoryList(inventoryList, new Repo.Callback() {
            @Override
            public void success(Object o) {
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save inventory list");
            }
        });


    }

    @Override
    public void getStockItemsFail(String msg) {
        showMessage(msg);
        loadOfflineData();
    }

    @Override
    public void addUnsyncedInventoriesSuccess() {
        AppUtils.showLog(TAG, "add unsynced inventories success");
        Toast.makeText(this, "Items synced", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.DATA_REMAINING_TO_SYNC, false);
        editor.apply();

    }


    @Override
    public void addUnsyncedInventoriesFail(String msg) {
        showMessage(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
