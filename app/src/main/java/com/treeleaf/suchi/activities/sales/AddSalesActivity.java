package com.treeleaf.suchi.activities.sales;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.AppCompatSpinner;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;

import com.treeleaf.suchi.adapter.CartAdapter;
import com.treeleaf.suchi.adapter.InventorySalesAdapter;
import com.treeleaf.suchi.adapter.StockSalesAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
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
    public static final String EXTRA_TITLE = "updated_sale_object";
    private static final String TAG = "AddSalesActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_search)
    AutoCompleteTextView mSearch;
    /*    @BindView(R.id.camera_view)
        CameraView cameraView;*/
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
    @BindView(R.id.rv_sale_stocks)
    RecyclerView mSaleStocksRecycler;
    @BindView(R.id.scroll_view)
    ScrollView mScroll;
    @BindView(R.id.fab_confirm)
    FloatingActionButton mConfirm;
    @BindView(R.id.rl_recycler_holder)
    RelativeLayout mRecyclerHolder;
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
    @BindView(R.id.rv_cart_items)
    RecyclerView mCartItems;
    @BindView(R.id.tv_cart_items_text)
    TextView mItemsInCart;
    @BindView(R.id.rv_cart_holder)
    RelativeLayout mCartHolder;
    @BindView(R.id.barcode_view)
    DecoratedBarcodeView mBarcodeView;


    private Fotoapparat fotoapparat;
    private InventoryDto selectedItem;

    private List<Inventory> inventoryList = new ArrayList<>();
    private List<String> unitItems = new ArrayList<>();
    private ArrayAdapter<String> unitItemsAdapter;
    private AddSalesPresenter presenter;
    private String token, userId;
    private SharedPreferences sharedPreferences;
    private boolean toggleCamera = true;
    private boolean isFront = true;
    private int quantityLimit;
    private String sellingPrice, id, inventoryId, qty, unit, photoUrl, name;
    private boolean checked = false;
    private List<SalesStock> cartItemList = new ArrayList<>();
    private CartAdapter cartAdapter;
    private InventoryStocksDto defaultInventoryStock;
    private boolean isScanDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_for_keyboard_glitch));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_sales);

        ButterKnife.bind(this);

        initialize();
        requestPermission();
        setUpSearch();
        setUpBarcodeScanner();

        token = sharedPreferences.getString(Constants.TOKEN, "");
        userId = sharedPreferences.getString(Constants.USER_ID, "");

        mSell.setOnClickListener(this);
        mIncrease.setOnClickListener(this);
        mDecrease.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mCameraSwitch.setOnClickListener(this);
        mTotalAmount.setOnClickListener(this);
        mConfirm.setOnClickListener(this);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("selected_stock_qty"));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                cartAdapter.deleteItem(position);
                if (cartAdapter.getItemCount() == 0) mCartHolder.setVisibility(View.GONE);
                AppUtils.showLog(TAG, "cartlistsize: " + cartItemList.size());

            }
        }).attachToRecyclerView(mCartItems);


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
                            if (toggleCamera) {
                                mCameraHolder.setVisibility(View.GONE);
                                toggleCamera = false;
                                mSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_camera_disabled, 0);
                            } else {
                                mCameraHolder.setVisibility(View.VISIBLE);
                                toggleCamera = true;
                                mSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_camera_green, 0);
                            }

                            return true;
                        }
                    }

                return false;
            }
        });


        mSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
//                mSell.setVisibility(View.VISIBLE);
                selectedItem = (InventoryDto) adapterView.getItemAtPosition(i);
                setUpViewForSkuDetails(selectedItem);

            }
        });

    }

    public void setUpViewForSkuDetails(InventoryDto selectedItem) {
        mRecyclerHolder.setVisibility(View.GONE);

        mSearch.setText(selectedItem.getSku().getName());
        mQuantity.setText("1");

        mSelectedSKUName.setText(selectedItem.getSku().getName());

        StringBuilder amountBuilder = new StringBuilder();
        amountBuilder.append("Rs. ");
        defaultInventoryStock = getHighestQuantityInventoryStock(selectedItem.getInventoryStocks());
//                defaultInventoryStock = selectedItem.getInventoryStocks().get(0);
        amountBuilder.append(defaultInventoryStock.getSalesPrice());
        quantityLimit = Integer.valueOf(defaultInventoryStock.getQuantity());

        mTotalAmount.setText(amountBuilder);

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

        AppUtils.showLog(TAG, "defaultUnit: " + selectedItem.getSku().getDefaultUnit());

        //set default unit as default selection
        int defaultUnitPosition = unitItemsAdapter.getPosition(selectedItem.getSku().getDefaultUnit());
        mUnitSpinner.setSelection(defaultUnitPosition);

        mSkuDetails.setVisibility(View.VISIBLE);

        mQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {

                    if (sellingPrice != null) {
                        String formatedSellingPrice = sellingPrice.replace("Rs. ", "");
                        double totalAmount = Double.valueOf(formatedSellingPrice) *
                                Double.valueOf(charSequence.toString());

                        StringBuilder mTotalAmountBuilder = new StringBuilder();
                        mTotalAmountBuilder.append("Rs. ");
                        mTotalAmountBuilder.append(totalAmount);

                        mTotalAmount.setText(mTotalAmountBuilder);
                    } else {

                        double totalAmount = Double.valueOf(defaultInventoryStock.getSalesPrice()) *
                                Double.valueOf(charSequence.toString());

                        StringBuilder mTotalAmountBuilder = new StringBuilder();
                        mTotalAmountBuilder.append("Rs. ");
                        mTotalAmountBuilder.append(totalAmount);

                        mTotalAmount.setText(mTotalAmountBuilder);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setUpBarcodeScanner() {
        mBarcodeView.setStatusText("");
        mBarcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                beepSound();
                checkMatchingSkuForResult(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    private void checkMatchingSkuForResult(String text) {
        List<Inventory> inventoryModelList = InventoryRepo.getInstance().getAllInventoryList();
        List<InventoryDto> inventoryDtoList = mapInventoriesToInventoryDto(inventoryModelList);

        for (InventoryDto inventoryDto : inventoryDtoList
        ) {
            if (inventoryDto.getSku().getCode().equalsIgnoreCase(text)) {
                setUpViewForSkuDetails(inventoryDto);
            }
        }
    }


    private InventoryStocksDto getHighestQuantityInventoryStock(List<InventoryStocksDto> inventoryStocks) {
        int maxValue = 0;
        InventoryStocksDto highestQuantityStock = new InventoryStocksDto();
        for (InventoryStocksDto inventoryStockDto : inventoryStocks
        ) {
            if (Integer.valueOf(inventoryStockDto.getQuantity()) > maxValue) {
                maxValue = Integer.valueOf(inventoryStockDto.getQuantity());
                highestQuantityStock = inventoryStockDto;
            }
        }

        return highestQuantityStock;
    }

    private void setUpUnitSpinner(List<Units> units) {
        AppUtils.showLog(TAG, "setupunitSpinner()");
        for (Units unit : units
        ) {
            AppUtils.showLog(TAG, "unitItems: " + unit.getName());
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


/*    private Fotoapparat createFotoapparat() {
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
    }*/

    @Override
    protected void onStart() {
        super.onStart();
 /*       if (!Objects.equals(fotoapparat, null)) {
            fotoapparat.start();
        }*/

    }

    @Override
    protected void onStop() {
        super.onStop();
 /*       if (!Objects.equals(fotoapparat, null)) {
            fotoapparat.stop();
        }*/

    }

    private void initialize() {
//        fotoapparat = createFotoapparat();
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        getMyApplication(this).getAppComponent().inject(this);

        presenter = new AddSalesPresenterImpl(endpoints, this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mQuantity.setText("1");

            qty = intent.getStringExtra("quantity");
            sellingPrice = intent.getStringExtra("selling_price");
            unit = intent.getStringExtra("unit");
            id = intent.getStringExtra("inventory_stock_id");
            inventoryId = intent.getStringExtra("inventory_id");
            name = intent.getStringExtra("name");
            photoUrl = intent.getStringExtra("photo_url");
            checked = intent.getBooleanExtra("checked", false);

            quantityLimit = Integer.valueOf(qty);

            StringBuilder totalAmountBuilder = new StringBuilder();
            totalAmountBuilder.append(sellingPrice);
            mTotalAmount.setText(totalAmountBuilder);

            if (checked) mRecyclerHolder.setVisibility(View.GONE);
            else mRecyclerHolder.setVisibility(View.VISIBLE);

        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_sell:
                inflateCart();
                break;


            case R.id.ib_increase:
                if (!mQuantity.getText().toString().isEmpty()) {
                    int value = Integer.valueOf(mQuantity.getText().toString());
                    if (value < quantityLimit) {
                        value++;
                        mQuantity.setText(String.valueOf(value));
                    }
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

            case R.id.tv_total_amount:
                setUpRecyclerView();
                mRecyclerHolder.setVisibility(View.VISIBLE);
                scrollToBottom();
                break;

            case R.id.fab_confirm:
                saveSalesData();
                break;

        }
    }

    private void saveSalesData() {

     /*   Intent data = new Intent();
        data.putParcelableArrayListExtra(EXTRA_TITLE, (ArrayList<? extends Parcelable>) cartItemList);
        setResult(RESULT_OK, data);*/

        Intent sendData = new Intent(AddSalesActivity.this, SalesBill.class);
        sendData.putParcelableArrayListExtra(EXTRA_TITLE, (ArrayList<? extends Parcelable>) cartItemList);
        startActivity(sendData);

//        List<Sales> unSyncedSalesList = mapCartItemDataToSalesModel();


    }

  /*  private List<Sales> mapCartItemDataToSalesModel() {
        RealmList<Sales> salesList = new RealmList<>();

        for (SalesStock salesStock : cartItemList
        ) {
            Sales sales = new Sales();

        }
    }*/

    private void postSalesData() {

        List<SuchiProto.SaleInventory> saleInventoryList = new ArrayList<>();
        for (SalesStock salesStock : cartItemList
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

        double totalAmount = 0;
        for (SalesStock salesStock : cartItemList
        ) {
            totalAmount += Double.valueOf(salesStock.getAmount());
        }

        SuchiProto.Sale sale = SuchiProto.Sale.newBuilder()
                .addAllSaleInventories(saleInventoryList)
                .setAmount(totalAmount)
                .build();

        showLoading();
        presenter.addSales(token, sale);

    }

    private void inflateCart() {
        mRecyclerHolder.setVisibility(View.GONE);
        mSkuDetails.setVisibility(View.GONE);
        mSearch.getText().clear();
        mCartHolder.setVisibility(View.VISIBLE);

        cartAdapter = new CartAdapter(this, cartItemList);
        mCartItems.setLayoutManager(new LinearLayoutManager(this));
        mCartItems.setItemAnimator(new DefaultItemAnimator());
        mCartItems.setAdapter(cartAdapter);

        prepareCartItems();
    }

    private void prepareCartItems() {
        if (mTotalAmount.getText().toString().contains("Rs. ")) {
            String formatedAmount = mTotalAmount.getText().toString().replace("Rs. ", "");

            if (checked) {
                boolean isInArray = false;
                //temporary lists to prevent concurrent modification
                List<SalesStock> tempListRemove = new ArrayList<>();
                List<SalesStock> tempListAdd = new ArrayList<>();

                //check if arraylist is empty before checking common id
                if (cartItemList.size() != 0) {

                    //check if item is already in arraylist
                    for (SalesStock salesStock : cartItemList
                    ) {
                        if (salesStock.getId().equalsIgnoreCase(id)) {
                            isInArray = true;
                        }
                    }

                    if (!isInArray) {
                        SalesStock salesStockNew = new SalesStock(id, inventoryId, formatedAmount, mQuantity.getText().toString(), unit, name, photoUrl, sellingPrice, false);
                        cartItemList.add(salesStockNew);

                        cartAdapter.notifyDataSetChanged();
                    } else {
                        for (SalesStock salesStock : cartItemList
                        ) {
                            if (salesStock.getId().equalsIgnoreCase(id)) {
                                tempListRemove.add(salesStock);

                                int updatedQuantity = Integer.valueOf(salesStock.getQuantity()) + Integer.valueOf(mQuantity.getText().toString().trim());
                                double updatedAmount = Double.valueOf(salesStock.getAmount()) + Double.valueOf(formatedAmount);

                                SalesStock replaceSalesStock = new SalesStock(id, inventoryId, String.valueOf(updatedAmount), String.valueOf(updatedQuantity), unit, name, photoUrl, sellingPrice, false);

                                tempListAdd.add(replaceSalesStock);
                            }
                        }
                    }

                    cartItemList.removeAll(tempListRemove);
                    cartItemList.addAll(tempListAdd);
                    cartAdapter.notifyDataSetChanged();

                } else {

                    SalesStock salesStockNew = new SalesStock(id, inventoryId, formatedAmount, mQuantity.getText().toString(), unit, name, photoUrl, sellingPrice, false);
                    cartItemList.add(salesStockNew);

                    cartAdapter.notifyDataSetChanged();
                }
            } else {
                boolean isInArray = false;
                Units units = UnitRepo.getInstance().getUnitById(defaultInventoryStock.getUnitId());

                //temporary lists to prevent concurrent modification
                List<SalesStock> tempListRemove = new ArrayList<>();
                List<SalesStock> tempListAdd = new ArrayList<>();
                if (cartItemList.size() != 0) {
                    //check for same id
                    for (SalesStock salesStock : cartItemList) {
                        if (salesStock.getId().equalsIgnoreCase(defaultInventoryStock.getId())) {
                            isInArray = true;
                        }
                    }

                    if (!isInArray) {
                        // add new item if id not same
                        AppUtils.showLog(TAG, "under else");

                        SalesStock salesStockNew = new SalesStock(defaultInventoryStock.getId(), defaultInventoryStock.getInventory_id()
                                , formatedAmount, mQuantity.getText().toString(), units.getName(), selectedItem.getSku().getName(), selectedItem.getSku().getPhoto_url(),
                                defaultInventoryStock.getSalesPrice(), false);

                        tempListAdd.add(salesStockNew);
                    } else {
                        for (SalesStock salesStock : cartItemList
                        ) {
                            if (salesStock.getId().equalsIgnoreCase(defaultInventoryStock.getId())) {
                                tempListRemove.add(salesStock);
                                int updatedQuantity = Integer.valueOf(salesStock.getQuantity()) + Integer.valueOf(mQuantity.getText().toString().trim());
                                double updatedAmount = Double.valueOf(salesStock.getAmount()) + Double.valueOf(formatedAmount);

                                SalesStock replaceSalesStock = new SalesStock(defaultInventoryStock.getId(), defaultInventoryStock.getInventory_id()
                                        , String.valueOf(updatedAmount), String.valueOf(updatedQuantity), units.getName(),
                                        selectedItem.getSku().getName(), selectedItem.getSku().getPhoto_url(),
                                        defaultInventoryStock.getSalesPrice(), false);

                                tempListAdd.add(replaceSalesStock);

                            }
                        }
                    }

                    cartItemList.removeAll(tempListRemove);
                    cartItemList.addAll(tempListAdd);
                    cartAdapter.notifyDataSetChanged();

                } else {
                    // add for first item in arraylist
                    SalesStock salesStockNew = new SalesStock(defaultInventoryStock.getId(), defaultInventoryStock.getInventory_id()
                            , formatedAmount, mQuantity.getText().toString(), units.getName(), selectedItem.getSku().getName(), selectedItem.getSku().getPhoto_url(),
                            defaultInventoryStock.getSalesPrice(), false);
                    cartItemList.add(salesStockNew);

                    cartAdapter.notifyDataSetChanged();
                }


            }

            checked = false;
            sellingPrice = null;
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

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mSaleStocksRecycler.setLayoutManager(linearLayoutManager);
        StockSalesAdapter stockSalesAdapter = new StockSalesAdapter(AddSalesActivity.this, selectedItem, mSaleStocksRecycler);
        mSaleStocksRecycler.setAdapter(stockSalesAdapter);


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
        inventoryList = InventoryRepo.getInstance().getAllInventoryList();
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
            RealmList<InventoryStocksDto> inventoryStocksDtoList = mapInventoryStocksToDto(inventory.getInventoryStocks(), inventory.getInventory_id());
            inventoryDto.setInventoryStocks(inventoryStocksDtoList);

            inventoryDtoList.add(inventoryDto);
        }
        return inventoryDtoList;
    }


    private RealmList<InventoryStocksDto> mapInventoryStocksToDto(List<InventoryStocks> inventoryStocks, String inventoryId) {
        RealmList<InventoryStocksDto> inventoryStockDtoList = new RealmList<>();
        for (InventoryStocks inventoryStock : inventoryStocks
        ) {
            InventoryStocksDto inventoryStocksDto = new InventoryStocksDto();
            inventoryStocksDto.setId(inventoryStock.getId());
            inventoryStocksDto.setInventory_id(inventoryId);
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
        skuDto.setDefaultUnit(sku.getDefaultUnit());

        return skuDto;
    }

    @Override
    public void addSalesSuccess() {
        AppUtils.showLog(TAG, "sales add success");
        Toast.makeText(this, "Sale items added", Toast.LENGTH_SHORT).show();

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

    void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length < 1) {
            requestPermission();
        } else {
            mBarcodeView.resume();
        }
    }

    protected void beepSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeScanner();
    }

    protected void resumeScanner() {
        isScanDone = false;
        if (!mBarcodeView.isActivated())
            mBarcodeView.resume();
        Log.d("peeyush-pause", "paused: false");
    }


    protected void pauseScanner() {
        mBarcodeView.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseScanner();
    }
}
