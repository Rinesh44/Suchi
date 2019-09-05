package com.treeleaf.suchi.activities.sales;

import androidx.annotation.Nullable;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.SalesAdapter;
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.viewmodel.SalesListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class SalesActivity extends BaseActivity {
    public static final int ADD_SALE_REQUEST = 1;
    public static final String EXTRA_TITLE = "updated_sale_object";

    private static final String TAG = "SalesActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_no_sales)
    TextView mNoSales;
    @BindView(R.id.rv_sales)
    RecyclerView mSalesRecycler;
    @BindView(R.id.fab_add_sales)
    FloatingActionButton mAddSales;
    @BindView(R.id.et_search)
    EditText mSearch;

    private SharedPreferences sharedPreferences;
    private SalesAdapter mSalesAdapter;
    private String token;
    private List<SalesStockDto> salesStockDtoList;
    private SalesListViewModel salesListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        ButterKnife.bind(this);

        initialize();

        token = sharedPreferences.getString(Constants.TOKEN, "");
        mSalesRecycler.setLayoutManager(new LinearLayoutManager(this));
        mSalesAdapter = new SalesAdapter(SalesActivity.this, salesStockDtoList);


        salesListViewModel = ViewModelProviders.of(this).get(SalesListViewModel.class);

        salesListViewModel.getSaleItems().observe(this, new Observer<RealmResults<SalesStock>>() {
            @Override
            public void onChanged(RealmResults<SalesStock> salesStocks) {

                AppUtils.showLog(TAG, "sales list size: " + salesStocks.size());
                if (salesStocks.size() == 0) {
                    mNoSales.setVisibility(View.VISIBLE);
                    mSearch.setVisibility(View.GONE);
                } else {
                    mSearch.setVisibility(View.VISIBLE);
                    mNoSales.setVisibility(View.GONE);
                    salesStockDtoList = mapSaleStocksToSalesStockDto(salesStocks);
                    mSalesAdapter = new SalesAdapter(SalesActivity.this, salesStockDtoList);
                    mSalesRecycler.setAdapter(mSalesAdapter);
                    mSalesAdapter.submitList(salesStockDtoList);

                    mSalesAdapter.setOnItemClickListener(new SalesAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(SalesStockDto sales) {
                            Intent i = new Intent(SalesActivity.this, SalesDetailsActivity.class);
                            i.putExtra("sales_object", sales);
                            startActivity(i);
                        }
                    });
                }
            }
        });

        mSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mSearch.setFocusableInTouchMode(true);

                return false;
            }
        });


        hideFabWhenScrolled();


        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSalesAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAddSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SalesActivity.this, AddSalesActivity.class);
                startActivityForResult(intent, ADD_SALE_REQUEST);

            }
        });
    }

    private List<SalesStockDto> mapSaleStocksToSalesStockDto(RealmResults<SalesStock> salesStocks) {
        List<SalesStockDto> salesStockDtoList = new ArrayList<>();
        for (SalesStock salesStock : salesStocks) {
            SalesStockDto salesStockDto = new SalesStockDto();
            salesStockDto.setName(salesStock.getName());
            salesStockDto.setAmount(salesStock.getAmount());
            salesStockDto.setId(salesStock.getId());
            salesStockDto.setInventory_id(salesStock.getInventory_id());
            salesStockDto.setPhotoUrl(salesStock.getPhotoUrl());
            salesStockDto.setQuantity(salesStock.getQuantity());
            salesStockDto.setSynced(salesStock.isSynced());
            salesStockDto.setUnit(salesStock.getUnit());
            salesStockDto.setUnitPrice(salesStock.getUnitPrice());

            salesStockDtoList.add(salesStockDto);
        }

        return salesStockDtoList;
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Sales");

        getMyApplication(this).getAppComponent().inject(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.showLog(TAG, "onResumeCalled()");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hideFabWhenScrolled() {

        mSalesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && mAddSales.isShown())
                    mAddSales.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mAddSales.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppUtils.showLog(TAG, "onActivityResult()");

        if (requestCode == ADD_SALE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                List<SalesStock> updatedSalesStockList = data.getParcelableArrayListExtra(EXTRA_TITLE);

                SalesStockRepo.getInstance().saveSalesStockList(updatedSalesStockList, new Repo.Callback() {
                    @Override
                    public void success(Object o) {
                        Toast.makeText(SalesActivity.this, "Sale item added", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void fail() {
                        AppUtils.showLog(TAG, "failed to save sale item");
                    }
                });
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }*/
}
