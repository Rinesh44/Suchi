package com.treeleaf.suchi.activities.sales;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import com.treeleaf.suchi.adapter.InventorySalesAdapter;
import com.treeleaf.suchi.adapter.StockSalesAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.configuration.CameraConfiguration;
import io.fotoapparat.log.LoggersKt;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import io.fotoapparat.view.CameraView;
import io.realm.RealmList;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class AddSalesActivity extends BaseActivity implements View.OnClickListener, AddSalesView {
    private static final String TAG = "AddSalesActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_search)
    AutoCompleteTextView mSearch;
    @BindView(R.id.camera_view)
    CameraView cameraView;
    @BindView(R.id.tv_sku_name)
    TextView mSelectedSKUName;
    @BindView(R.id.iv_sku_image)
    CircleImageView mSkuImage;
    @BindView(R.id.rl_camera_holder)
    RelativeLayout mCameraHolder;
    @BindView(R.id.fab_sell)
    FloatingActionButton mSell;
    @BindView(R.id.rl_sku_details)
    RelativeLayout mSkuDetails;
    /*    @BindView(R.id.rv_sale_stocks)
        RecyclerView mSaleStocksRecycler;*/
    @BindView(R.id.scroll_view)
    ScrollView mScroll;
    /*    @BindView(R.id.fab_confirm)
        FloatingActionButton mConfirm;*/
  /*  @BindView(R.id.rl_recycler_holder)
    RelativeLayout mRecyclerHolder;*/
    @BindView(R.id.sp_units)
    AppCompatSpinner mUnitSpinner;
    @BindView(R.id.ib_increase)
    ImageButton mIncrease;
    @BindView(R.id.ib_decrease)
    ImageButton mDecrease;
    @BindView(R.id.et_quantity)
    EditText mQuantity;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.iv_back)
    ImageView mBack;
    @BindView(R.id.fab_camera_switch)
    FloatingActionButton mCameraSwitch;


    private Fotoapparat fotoapparat;
    private InventoryDto selectedItem;

    private List<Inventory> inventoryList = new ArrayList<>();
    private List<Units> unitList = new ArrayList<>();
    private String selectedItemId, selectedItemUnitId;
    private List<String> unitItems = new ArrayList<>();
    private ArrayAdapter<String> unitItemsAdapter;
    private AddSalesPresenter presenter;
    List<SalesStock> salesStockList = new ArrayList<>();
    private String token, userId;
    private SharedPreferences sharedPreferences;
    private boolean toggleCamera = true;
    private boolean isFront = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_sales);

        ButterKnife.bind(this);

        initialize();
        setUpSearch();

        token = sharedPreferences.getString(Constants.TOKEN, "");
        userId = sharedPreferences.getString(Constants.USER_ID, "");

        mSell.setOnClickListener(this);
        mIncrease.setOnClickListener(this);
        mDecrease.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mCameraSwitch.setOnClickListener(this);

//        setUpUnitSpinner();
//        updateTotalAmount();

        mSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mSearch.setFocusableInTouchMode(true);

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (mSearch.getCompoundDrawables()[DRAWABLE_RIGHT] != null)
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (mSearch.getRight() - mSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                            mSearch.getText().clear();
                            return true;
                        }
                    }

                return false;
            }
        });


        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 1) {
                    mSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
                } else {
                    mSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
          /*      mSell.setVisibility(View.VISIBLE);
                mRecyclerHolder.setVisibility(View.GONE);*/
                selectedItem = (InventoryDto) adapterView.getItemAtPosition(i);
                mSearch.setText(selectedItem.getSku().getName());
                mQuantity.setText("1");


                mSelectedSKUName.setText(selectedItem.getSku().getName());
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

                    Glide.with(AddSalesActivity.this).load(imageUrl).apply(options).into(mSkuImage);
                }


                unitItems.clear();
                setUpUnitSpinner(selectedItem.getSku().getUnits());

                //set default unit as default selection
                int defaultUnitPosition = unitItemsAdapter.getPosition(selectedItem.getSku().getDefaultUnit());
                mUnitSpinner.setSelection(defaultUnitPosition);

                mSkuDetails.setVisibility(View.VISIBLE);

                mTotalAmount.setText(itemCostBuilder);

                mQuantity.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 0) {
                            double totalAmount = Double.valueOf(selectedItem.getSku().getUnitPrice()) *
                                    Double.valueOf(charSequence.toString());

                            StringBuilder mTotalAmountBuilder = new StringBuilder();
                            mTotalAmountBuilder.append("Rs. ");
                            mTotalAmountBuilder.append(totalAmount);

                            mTotalAmount.setText(mTotalAmountBuilder);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });


    }

    private void setUpUnitSpinner(List<Units> units) {
        for (Units unit : units
        ) {
            unitItems.add(unit.getName());
        }

        unitItemsAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, unitItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };

        mUnitSpinner.setAdapter(unitItemsAdapter);
    }


    private Fotoapparat createFotoapparat() {
        return Fotoapparat
                .with(this)
                .into(cameraView)
//                .focusView(focusView)
                .previewScaleType(ScaleType.CenterCrop)
                .focusMode(SelectorsKt.firstAvailable(  // (optional) use the first focus mode which is supported by device
                        FocusModeSelectorsKt.continuousFocusPicture(),
                        FocusModeSelectorsKt.autoFocus(),        // in case if continuous focus is not available on device, auto focus will be used
                        FocusModeSelectorsKt.fixed()             // if even auto focus is not available - fixed focus mode will be used
                ))
//                .photoResolution(ResolutionSelectorsKt.lowestResolution())
                .lensPosition(LensPositionSelectorsKt.front())
                .logger(LoggersKt.loggers(            // (optional) we want to log camera events in two places at once
                        LoggersKt.logcat(),           // ... in logcat
                        LoggersKt.fileLogger(this)
                ))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Objects.equals(fotoapparat, null)) {
            fotoapparat.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Objects.equals(fotoapparat, null)) {
            fotoapparat.stop();
        }

    }

    private void initialize() {
        fotoapparat = createFotoapparat();
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
           /* getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);*/
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

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

//            mTotalAmount.setText(String.valueOf(sum));
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_sell:
//                setUpRecyclerView();
//                scrollToBottom();
//                mSell.setVisibility(View.GONE);
                break;


            case R.id.ib_increase:
                if (!mQuantity.getText().toString().isEmpty()) {
                    int value = Integer.valueOf(mQuantity.getText().toString());
                    value++;
                    mQuantity.setText(String.valueOf(value));
                }
                break;

            case R.id.ib_decrease:
                if (!mQuantity.getText().toString().isEmpty()) {
                    int value1 = Integer.valueOf(mQuantity.getText().toString());
                    if (value1 > 1) {
                        value1--;
                        mQuantity.setText(String.valueOf(value1));
                    }
                }
                break;


            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.fab_camera_switch:
                switchCamera();
                break;


        /*    case R.id.iv_barcode:
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
                break;*/

        }
    }

    private void scrollToBottom() {
        mScroll.post(new Runnable() {
            @Override
            public void run() {
                mScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    private void switchCamera() {
        isFront = !isFront;
        if (isFront) {
            fotoapparat.switchTo(LensPositionSelectorsKt.front(), new CameraConfiguration());
        } else {
            fotoapparat.switchTo(LensPositionSelectorsKt.back(), new CameraConfiguration());
        }
    }

/*    private void setUpRecyclerView() {
        mSaleStocksRecycler.setLayoutManager(new LinearLayoutManager(AddSalesActivity.this, RecyclerView.HORIZONTAL, false));
        StockSalesAdapter stockSalesAdapter = new StockSalesAdapter(AddSalesActivity.this, selectedItem);
        mSaleStocksRecycler.setAdapter(stockSalesAdapter);
    }*/

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toggle_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_camera:
                if (toggleCamera) {
                    mCameraHolder.setVisibility(View.GONE);
                    toggleCamera = false;

                    item.setIcon(R.drawable.ic_camera_disabled);
                } else {
                    mCameraHolder.setVisibility(View.VISIBLE);
                    toggleCamera = true;
                    item.setIcon(R.drawable.ic_camera_green);
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setUpSearch() {
        inventoryList = InventoryRepo.getInstance().getSyncedInventories();
        AppUtils.showLog(TAG, "inventoryDtoListSize: " + inventoryList.size());

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
    public void getSalesSuccess(List<Sales> salesList) {
        AppUtils.showLog(TAG, "get sales success");

        SalesRepo.getInstance().saveSalesList(salesList, new Repo.Callback() {
            @Override
            public void success(Object o) {

            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save sales to db");
            }
        });
    }
}
