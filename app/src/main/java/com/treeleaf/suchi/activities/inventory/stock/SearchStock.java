package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.realm.models.Items;
import com.treeleaf.suchi.realm.repo.ItemsRepo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchStock extends BaseActivity {
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sp_sku)
    SearchableSpinner mSkuSearch;
    @BindView(R.id.fab_add_sku)
    FloatingActionButton mAddSku;

    private List<Items> mSkuItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stock);

        ButterKnife.bind(this);

        initialize();

        mAddSku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchStock.this, StockEntryActivity.class));
            }
        });

        mSkuItems = ItemsRepo.getInstance().getAllItems();
        List<String> skuList = new ArrayList<>();
        for (Items item : mSkuItems
        ) {
            skuList.add(item.getName());
        }

        ArrayAdapter<String> skyListAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, skuList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };

        mSkuSearch.setAdapter(skyListAdapter);
        mSkuSearch.setPositiveButton("");
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Select SKU");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
