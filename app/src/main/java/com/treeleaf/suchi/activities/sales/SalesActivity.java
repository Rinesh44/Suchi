package com.treeleaf.suchi.activities.sales;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.inventory.InventoryActivity;
import com.treeleaf.suchi.activities.inventory.stock.SearchStock;
import com.treeleaf.suchi.adapter.SalesAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.NetworkUtils;
import com.treeleaf.suchi.viewmodel.SalesListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

import static com.treeleaf.suchi.SuchiApp.CHANNEL_ID;
import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class SalesActivity extends BaseActivity implements SalesView {
    public static final int ADD_SALE_REQUEST = 1;
    public static final String EXTRA_TITLE = "updated_sale_object";

    private static final String TAG = "SalesActivity";
    @Inject
    Endpoints endpoints;
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
    private String token, userId;
    private List<SalesStockDto> salesStockDtoList;
    private SalesListViewModel salesListViewModel;
    private SalesPresenter presenter;
    private NotificationManagerCompat notificationManagerCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        ButterKnife.bind(this);

        initialize();

        token = sharedPreferences.getString(Constants.TOKEN, "");
        userId = sharedPreferences.getString(Constants.USER_ID, "");
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
        mToolbarTitle.setText(getResources().getString(R.string.sales));

        getMyApplication(this).getAppComponent().inject(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        presenter = new SalesPresenterImpl(endpoints, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.showLog(TAG, "onResumeCalled()");
        invalidateOptionsMenu();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (sharedPreferences.getBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, false))
            getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (NetworkUtils.isNetworkConnected(this)) {
                    List<SalesStock> unSyncedSalesStockList = SalesStockRepo.getInstance().getUnsyncedSalesStockList();
                    if (token != null) {
                        if (!unSyncedSalesStockList.isEmpty()) {
                            showLoading();
                            postSalesDataToServer(unSyncedSalesStockList);
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

    private void postSalesDataToServer(List<SalesStock> unsyncedSalesList) {
        List<SuchiProto.Sale> saleListProto = mapSalesStockToProto(unsyncedSalesList);
        SuchiProto.SyncRequest syncRequest = SuchiProto.SyncRequest.newBuilder()
                .addAllSales(saleListProto)
                .build();

        presenter.syncSalesData(token, syncRequest);

    }

    private List<SuchiProto.Sale> mapSalesStockToProto(List<SalesStock> allSalesStock) {
        List<SuchiProto.Sale> saleListProto = new ArrayList<>();
        for (SalesStock saleStock : allSalesStock
        ) {

            String unitId = UnitRepo.getInstance().getUnitIdByUnitName(saleStock.getUnit());
            AppUtils.showLog(TAG, "quantity: " + saleStock.getQuantity());
            String formattedQuantity = "";
            if (saleStock.getQuantity().contains(".")) {
                formattedQuantity = saleStock.getQuantity().substring
                        (0, saleStock.getQuantity().length() - 2);
            } else formattedQuantity = saleStock.getQuantity();

            AppUtils.showLog(TAG, "formatedQwuantitY; " + formattedQuantity);

            SuchiProto.SaleInventory saleInventoryProto = SuchiProto.SaleInventory.newBuilder()
                    .setUnitId(unitId)
                    .setAmount(Double.valueOf(saleStock.getUnitPrice()))
                    .setQuantity(Integer.valueOf(formattedQuantity))
                    .setInventoryStockId(saleStock.getId())
                    .setInventoryId(saleStock.getInventory_id())
                    .build();


            String saleId = UUID.randomUUID().toString();
            String formattedSaleId = saleId.replace("-", "");

            SuchiProto.Sale saleProto = SuchiProto.Sale.newBuilder()
                    .setAmount(Double.valueOf(saleStock.getAmount()))
                    .setCreatedAt(System.currentTimeMillis())
                    .setSaleId(formattedSaleId)
                    .setUserId(userId)
                    .addSaleInventories(saleInventoryProto)
                    .build();

            saleListProto.add(saleProto);

        }

        return saleListProto;
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

    @Override
    public void syncSalesDataSuccess() {
        AppUtils.showLog(TAG, "sync sales data success");

        Toast.makeText(this, "Sale items synced", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, false);
        editor.apply();

        invalidateOptionsMenu();
        mSalesAdapter.notifyDataSetChanged();

        List<Inventory> allInventoryList = InventoryRepo.getInstance().getAllInventoryList();
        StringBuilder scarceItems = new StringBuilder();
        for (Inventory inventory : allInventoryList
        ) {
            for (InventoryStocks inventoryStocks : inventory.getInventoryStocks()
            ) {
                if (Double.valueOf(inventoryStocks.getQuantity()) < 5) {
                    scarceItems.append(inventory.getSku().getName());
                    scarceItems.append(", ");
                }

            }
        }

        String modifiedScarceItems = scarceItems.toString().substring(0, scarceItems.length() - 2);
        pushNotification(modifiedScarceItems);

    }

    private void pushNotification(String skuNames) {
        Intent resultIntent = new Intent(this, SearchStock.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        notificationManagerCompat = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("Insufficient Stocks")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(skuNames + " are low on quantity"))
                .setContentText(skuNames + " are low on quantity")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManagerCompat.notify(1, notification);
    }

    @Override
    public void sycnSalesDataFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SalesActivity.this, InventoryActivity.class));
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
