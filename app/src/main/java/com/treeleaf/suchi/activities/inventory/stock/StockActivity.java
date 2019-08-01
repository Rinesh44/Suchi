package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.StockAdapter;
import com.treeleaf.suchi.realm.models.Stock;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockActivity extends BaseActivity {
    @BindView(R.id.rv_stock)
    RecyclerView mRecyclerViewStock;
    @BindView(R.id.fab_add)
    FloatingActionButton mAddStock;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    private StockAdapter mStockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        ButterKnife.bind(this);

        init();

        mRecyclerViewStock.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewStock.setHasFixedSize(true);

        mStockAdapter = new StockAdapter();
        mRecyclerViewStock.setAdapter(mStockAdapter);


        Stock stock = new Stock("1", "Kolin", "4", "250");
        List<Stock> stockList = new ArrayList<>();
        stockList.add(stock);

        mStockAdapter.setStocks(stockList);

        mAddStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StockActivity.this, StockEntryActivity.class));
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
