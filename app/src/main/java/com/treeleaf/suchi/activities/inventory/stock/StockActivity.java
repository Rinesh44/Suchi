package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.StockAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.realm.models.Items;
import com.treeleaf.suchi.realm.models.Stock;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        ButterKnife.bind(this);

        init();

        token = preferences.getString(Constants.TOKEN, "");
        mRecyclerViewStock.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewStock.setHasFixedSize(true);

        mStockAdapter = new StockAdapter();
        mRecyclerViewStock.setAdapter(mStockAdapter);

        mAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockActivity.this, SearchStock.class));
            }
        });

        mStockAdapter.setOnItemClickListener(new StockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Items items) {
                //TODO
                //show stock details activity
                Toast.makeText(StockActivity.this, "item clicked", Toast.LENGTH_SHORT).show();
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

        if (token != null) {
            showLoading();
            presenter.getStockItems(token);
        } else Toast.makeText(this, "Unable to get token", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void getStockItemsSuccess(List<Items> inventoryList) {
        AppUtils.showLog(TAG, "get stocks success");
        mStockAdapter.submitList(inventoryList);

    }

    @Override
    public void getStockItemsFail(String msg) {
        showMessage(msg);
    }
}
