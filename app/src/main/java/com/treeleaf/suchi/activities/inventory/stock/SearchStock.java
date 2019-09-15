package com.treeleaf.suchi.activities.inventory.stock;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.AutocompleteSearchAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.StockKeepingUnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.DatePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class SearchStock extends BaseActivity implements SearchStockView, View.OnClickListener {
    private static final String TAG = "SearchStock";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_name)
    TextView mSkuName;
    @BindView(R.id.tv_brand)
    TextView mBrand;
    @BindView(R.id.tv_sub_brand)
    TextView mSubBrand;
    @BindView(R.id.tv_unit_price)
    TextView mUnitPrice;
    @BindView(R.id.tv_category)
    TextView mCategory;
    @BindView(R.id.btn_increment)
    ImageButton mIncrement;
    @BindView(R.id.btn_decrement)
    ImageButton mDecrement;
    @BindView(R.id.tv_quantity)
    EditText mQuantity;
    @BindView(R.id.il_expiry_date)
    TextInputLayout mExpiryDateLayout;
    @BindView(R.id.et_expiry_date)
    TextInputEditText mExpiryDate;
    @BindView(R.id.btn_add)
    MaterialButton mAdd;
    @BindView(R.id.iv_sku)
    ImageView mSkuImage;
    @BindView(R.id.tv_sp_default_unit)
    TextView mSellingPriceDefUnit;
    @BindView(R.id.et_selling_price)
    EditText mSellingPrice;
    @BindView(R.id.tv_marked_price)
    TextView mMarkedPrice;
    @BindView(R.id.actv_sku)
    AutoCompleteTextView mSearchSku;
    @BindView(R.id.iv_camera)
    public ImageView mToggleCamera;
    @BindView(R.id.mcv_sku_details)
    MaterialCardView mSkuDetails;
    @BindView(R.id.add_inventory_holder)
    RelativeLayout mAddInventoryHolder;
    @BindView(R.id.btn_add_to_inventory)
    MaterialButton mAddToInventory;
    @BindView(R.id.sp_unit)
    AppCompatSpinner mUnits;
    @BindView(R.id.tv_default_unit)
    TextView mDefaultUnit;
    @BindView(R.id.btn_increment_sp)
    ImageButton mSellingPriceIncrement;
    @BindView(R.id.btn_decrement_sp)
    ImageButton mSellingPriceDecrement;
    @BindView(R.id.tv_qty_unit)
    TextView mQtyUnit;
    @BindView(R.id.ll_camera_holder)
    RelativeLayout mCameraHolder;
    @BindView(R.id.barcode_view)
    DecoratedBarcodeView mBarcodeView;
    @BindView(R.id.fab_camera_switch)
    FloatingActionButton mCameraSwitch;

    private boolean toggleCamera = false;
    private boolean isScanDone;
    private DatePicker datePicker;
    private String token;
    private String unitPrice;
    private StockKeepingUnitDto selectedItem;
    private final int REQ_CODE_SPEECH_INPUT = 100;

/*    @BindView(R.id.tv_description)
    TextView mDescription;*/

    private SearchStockPresenter presenter;
    private List<StockKeepingUnit> mSkuItems = new ArrayList<>();
    private List<String> skuList = new ArrayList<>();
    private List<Units> unitList = new ArrayList<>();
    private List<String> unitItems = new ArrayList<>();
    private String selectedItemId, selectedItemUnitId;

    private SharedPreferences sharedPreferences;
    private String userId, inventoryId, inventoryStockIdReplaced, inventoryStockId;
    private boolean update = false;
    private ArrayAdapter<String> unitItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_for_keyboard_glitch));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search_stock);

        ButterKnife.bind(this);

        initialize();

        mAdd.setOnClickListener(this);
        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mToggleCamera.setOnClickListener(this);
        mExpiryDate.setOnClickListener(this);
        mAddToInventory.setOnClickListener(this);
        mSellingPriceDecrement.setOnClickListener(this);
        mCameraSwitch.setOnClickListener(this);
        mSellingPriceIncrement.setOnClickListener(this);

        setUpSearch();
        setUpBarcodeScanner();

        presenter = new SearchStockPresenterImpl(endpoints, this);

        mSearchSku.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();

                selectedItem = (StockKeepingUnitDto) adapterView.getItemAtPosition(i);

                setUpViewForSkuDetails(selectedItem);

            }
        });

        datePicker = new DatePicker(this, R.id.et_expiry_date);

        mSearchSku.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mSearchSku.setFocusableInTouchMode(true);

                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (mSearchSku.getCompoundDrawables()[DRAWABLE_RIGHT] != null)
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (mSearchSku.getRight() - mSearchSku.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            promptSpeechInput();
                            return true;
                        }
                    }

                return false;
            }
        });

    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mSearchSku.setText(result.get(0));
                }
                break;
            }

        }
    }

    private void setUpViewForSkuDetails(StockKeepingUnitDto selectedItem) {

        mSearchSku.setText(selectedItem.getName());
        mSearchSku.dismissDropDown();

        setUpUnitSpinner(selectedItem.getUnits());
        setUpdefaultUnit(selectedItem.getDefaultUnit());

        int defaultUnitPosition = unitItemsAdapter.getPosition(selectedItem.getDefaultUnit());
        AppUtils.showLog(TAG, "defaultUnit: " + selectedItem.getDefaultUnit());
        AppUtils.showLog(TAG, "defaultUnitPos: " + defaultUnitPosition);
        mUnits.setSelection(defaultUnitPosition);

        selectedItemId = selectedItem.getId();
        unitPrice = selectedItem.getUnitPrice();
        mQuantity.setText("1");
        mSkuName.setText(selectedItem.getName());
        mBrand.setText(selectedItem.getBrand().getName());
        mSubBrand.setText(selectedItem.getSubBrands().getName());
        mSellingPrice.setText(selectedItem.getUnitPrice());
        setUpUnitPrice(selectedItem.getUnitPrice());
        mCategory.setText(selectedItem.getCategories().getName());
        setUpMarkedPrice(selectedItem.getUnitPrice(), selectedItem.getDefaultUnit());
        setUpSellingPriceUnit(selectedItem.getDefaultUnit());


//        mDescription.setText(selectedItem.getDesc());

        String imageUrl = selectedItem.getPhoto_url();
        AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
        if (!imageUrl.isEmpty()) {
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_stock)
                    .error(R.drawable.ic_stock);

            Glide.with(SearchStock.this).load(imageUrl).apply(options).into(mSkuImage);
        }

        mSkuDetails.setVisibility(View.VISIBLE);
        mAddToInventory.setVisibility(View.VISIBLE);
        mAddInventoryHolder.setVisibility(View.GONE);
    }

    private void setUpBarcodeScanner() {
        mBarcodeView.setStatusText("");
        mBarcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
//                beepSound();
                Toast.makeText(SearchStock.this, result.getText(), Toast.LENGTH_SHORT).show();
                getSkuMatchingResult(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }


    //get stockKeeping unit from database where scanned barcode matches db data
    private void getSkuMatchingResult(String text) {
        List<StockKeepingUnit> skuModelList = StockKeepingUnitRepo.getInstance().getAllSkuList();
        List<StockKeepingUnitDto> skuDtoList = mapSKUModelToDto(skuModelList);

        for (StockKeepingUnitDto skuDto : skuDtoList
        ) {
            if (skuDto.getCode().equalsIgnoreCase(text)) {
                selectedItem = skuDto;
                setUpViewForSkuDetails(selectedItem);
            }
        }

    }

    protected void resumeScanner() {
        isScanDone = false;
        if (!mBarcodeView.isActivated())
            mBarcodeView.resume();
        Log.d("peeyush-pause", "paused: false");
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

    private void setUpQuantityUnit(String item) {
        StringBuilder quantityUnitBuilder = new StringBuilder();
        quantityUnitBuilder.append(" (");
        quantityUnitBuilder.append(item);
        quantityUnitBuilder.append(")");

        mQtyUnit.setText(quantityUnitBuilder);
    }

    private void setUpSellingPriceUnit(String defUnit) {
        StringBuilder sellingPriceUnitBuilder = new StringBuilder();
        sellingPriceUnitBuilder.append("per ");
        sellingPriceUnitBuilder.append(defUnit);

        mSellingPriceDefUnit.setText(sellingPriceUnitBuilder);
    }

    private void setUpMarkedPrice(String unitPrice, String defUnit) {
        StringBuilder markedPriceBuilder = new StringBuilder();
        markedPriceBuilder.append("Rs. ");
        markedPriceBuilder.append(unitPrice);
        markedPriceBuilder.append(" per ");
        markedPriceBuilder.append(defUnit);

        mMarkedPrice.setText(markedPriceBuilder);
    }

    private void setUpUnitPrice(String unitPrice) {
        StringBuilder unitPriceBuilder = new StringBuilder();
        unitPriceBuilder.append("Rs. ");
        unitPriceBuilder.append(unitPrice);

        mUnitPrice.setText(unitPriceBuilder);
    }

    private void setUpdefaultUnit(String defaultUnit) {
        StringBuilder defaultUnitBuilder = new StringBuilder();
        defaultUnitBuilder.append(" (");
        defaultUnitBuilder.append(defaultUnit);
        defaultUnitBuilder.append(")");

        mDefaultUnit.setText(defaultUnitBuilder);
    }

    private void setUpSearch() {
        mSkuItems = StockKeepingUnitRepo.getInstance().getAllSkuList();
        List<StockKeepingUnitDto> stockKeepingUnitDtoList = new ArrayList<>();
        stockKeepingUnitDtoList = mapSKUModelToDto(mSkuItems);

        AutocompleteSearchAdapter skuListAdapter = new AutocompleteSearchAdapter(this, R.layout.autocomplete_search_item, stockKeepingUnitDtoList);

        mSearchSku.setAdapter(skuListAdapter);
    }

    private List<StockKeepingUnitDto> mapSKUModelToDto(List<StockKeepingUnit> mSkuItems) {
        List<StockKeepingUnitDto> SKUDtoList = new ArrayList<>();
        for (StockKeepingUnit sku : mSkuItems
        ) {
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

            SKUDtoList.add(skuDto);
        }

        return SKUDtoList;

    }

    private StockKeepingUnit mapSKUDTOToModel(StockKeepingUnitDto skuDto) {
        StockKeepingUnit sku = new StockKeepingUnit();
        sku.setDefaultUnit(skuDto.getDefaultUnit());
        sku.setSynced(skuDto.isSynced());
        sku.setDesc(skuDto.getDesc());
        sku.setUnitPrice(skuDto.getUnitPrice());
        sku.setCode(skuDto.getCode());
        sku.setPhoto_url(skuDto.getPhoto_url());
        sku.setName(skuDto.getName());
        sku.setId(skuDto.getId());
        sku.setBrand(skuDto.getBrand());
        sku.setCategories(skuDto.getCategories());
        sku.setSubBrands(skuDto.getSubBrands());
        sku.setUnits((RealmList<Units>) skuDto.getUnits());

        return sku;
    }

    private void setUpUnitSpinner(List<Units> unitList) {
        for (Units unit : unitList
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

        mUnits.setAdapter(unitItemsAdapter);

        mUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                int itemPosition = unitItems.indexOf(item);

                AppUtils.showLog(TAG, "itemPosition: " + itemPosition);
                setUpQuantityUnit(item);
                Units units = unitList.get(itemPosition);
                selectedItemUnitId = units.getId();
                AppUtils.showLog(TAG, "selectedUnint: " + units.getName());
                AppUtils.showLog(TAG, "selectedUnintId: " + units.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_new_sku, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new_sku:
                startActivity(new Intent(SearchStock.this, StockEntryActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validateFieldsThenAdd() {

        if (mQuantity.getText().toString().isEmpty() || mQuantity.getText().toString().equals("0")) {
            showMessage("Quantity is required");
            return;
        }


        if (mSellingPrice.getText().toString().isEmpty() || mSellingPrice.getText().toString().equals("0")) {
            showMessage("Selling price is required");
            return;
        }

     /*   if (mMarkedPrice.getText().toString().isEmpty() || mMarkedPrice.getText().toString().equals("0")) {
            mMarkedPriceLayout.setErrorEnabled(true);
            mMarkedPriceLayout.setError("This field is required");
            mMarkedPriceLayout.requestFocus();
            return;
        } else {
            mMarkedPriceLayout.setErrorEnabled(false);
        }*/

  /*      if (mExpiryDate.getText().toString().isEmpty()) {
            mExpiryDateLayout.setErrorEnabled(true);
            mExpiryDateLayout.setError("This field is required");
            mExpiryDateLayout.requestFocus();
            return;
        } else {
            mExpiryDateLayout.setErrorEnabled(false);
        }*/


        token = sharedPreferences.getString(Constants.TOKEN, "");
        userId = sharedPreferences.getString(Constants.USER_ID, "");

        Inventory currentInventory = new Inventory();

        List<Inventory> allInventories = InventoryRepo.getInstance().getAllInventoryList();
        if (allInventories == null || allInventories.isEmpty()) {
            AppUtils.showLog(TAG, "all inventories null");
            String randomInventoryId = UUID.randomUUID().toString();
            inventoryId = randomInventoryId.replace("-", "");
        } else {

            for (Inventory inventory : allInventories
            ) {
                if (inventory.getSku().getId().equals(selectedItemId)) {
                    AppUtils.showLog(TAG, "id match");
                    currentInventory = inventory;
                    inventoryId = inventory.getInventory_id();
                    update = true;
                } /*else {
                    String randomInventoryId = UUID.randomUUID().toString();
                    inventoryId = randomInventoryId.replace("-", "");
                }*/
            }
        }


        if (!update) {
            String randomInventoryId = UUID.randomUUID().toString();
            inventoryId = randomInventoryId.replace("-", "");
        }

        AppUtils.showLog(TAG, "inventoryId: " + inventoryId);
        AppUtils.showLog(TAG, "skuId: " + selectedItemId);

 /*       SuchiProto.InventoryStock inventoryStock = SuchiProto.InventoryStock.newBuilder()
                .setInventoryStockId(inventoryStockId)
                .setMarkedPrice(Double.valueOf(unitPrice))
                .setSalesPrice(Double.valueOf(mSellingPrice.getText().toString().trim()))
                .setQuantity(Integer.valueOf(mQuantity.getText().toString().trim()))
                .setUnitId(selectedItemUnitId)
                .build();

        SuchiProto.Inventory inventory = SuchiProto.Inventory.newBuilder()
                .setInventoryId(inventoryId)
                .setSkuId(selectedItemId)
                .setUserId(userId)
                .addInventoryStocks(inventoryStock)
                .setStatus(SuchiProto.SKUStatus.AVAILABLE)
                .setExpiryDate(System.currentTimeMillis())
                .build();*/

        showLoading();
/*        if (token != null) {
            if (!update) presenter.addStock(token, inventory);
            else presenter.updateStock(token, inventory);

        } else Toast.makeText(this, "Unable to get token", Toast.LENGTH_SHORT).show();*/

        //network is not connected, so save to realm as unsynced
        saveToRealm(false, currentInventory);


    }


    private void saveToRealm(boolean syncValue, Inventory currentInventory) {
        RealmList<InventoryStocks> inventoryStocksRealmList = new RealmList<>();

        //variables depending upon same sales price
        String addedQuantity = "";
        String quantity = "";

        //boolean to check if sales price is same
        //add quantity if same sales price, keep as it is if not
        boolean addQuantity = false;
//        AppUtils.showLog(TAG, "current inventory stock size: " + currentInventory.getInventoryStocks().size());
        if (currentInventory.getInventory_id() != null) {
            if (!currentInventory.getInventoryStocks().isEmpty()) {
                for (InventoryStocks stocks : currentInventory.getInventoryStocks()
                ) {
                    if (mSellingPrice.getText().toString().trim().equals(stocks.getSalesPrice())) {
                        AppUtils.showLog(TAG, "selling price matched");
                        AppUtils.showLog(TAG, "a: " + mQuantity.getText().toString().trim());
                        AppUtils.showLog(TAG, "b: " + stocks.getQuantity());
                        addQuantity = true;
                        addedQuantity = String.valueOf(Integer.valueOf(stocks.getQuantity()) + Integer.valueOf(mQuantity.getText().toString().trim()));
                        inventoryStockIdReplaced = stocks.getId();

                    } else {
                        inventoryStocksRealmList.add(stocks);
                        AppUtils.showLog(TAG, "selling price not matched");
                        AppUtils.showLog(TAG, "current price: " + mSellingPrice.getText().toString());
                        AppUtils.showLog(TAG, "old price: " + stocks.getSalesPrice());

                    }
                }
            }
        }
        //use same inventorystockId and add quantity if same sales price
        if (addQuantity) {
            InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockIdReplaced, inventoryId, addedQuantity,
                    selectedItem.getUnitPrice(), mSellingPrice.getText().toString().trim(),
                    selectedItemUnitId, syncValue);
            inventoryStocksRealmList.add(inventoryStocks);

        } else {
            //create new inventory stock if sales price not matched with new stock id
            String randomInventoryStockId = UUID.randomUUID().toString();
            inventoryStockId = randomInventoryStockId.replace("-", "");

            quantity = mQuantity.getText().toString().trim();
            if (selectedItem != null) {
                InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockId, inventoryId, quantity,
                        selectedItem.getUnitPrice(), mSellingPrice.getText().toString().trim(),
                        selectedItemUnitId, syncValue);
                inventoryStocksRealmList.add(inventoryStocks);
            } else {
                InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockId, inventoryId, quantity,
                        "100", mSellingPrice.getText().toString().trim(),
                        selectedItemUnitId, syncValue);
                inventoryStocksRealmList.add(inventoryStocks);
            }
        }


        Inventory inventoryOffline = new Inventory();
        inventoryOffline.setInventory_id(inventoryId);
        inventoryOffline.setInventoryStocks(inventoryStocksRealmList);
        inventoryOffline.setExpiryDate(String.valueOf(System.currentTimeMillis()));
        inventoryOffline.setSku(mapSKUDTOToModel(selectedItem));
        inventoryOffline.setSkuId(selectedItemId);
        inventoryOffline.setUser_id(userId);
        inventoryOffline.setSynced(syncValue);

        InventoryRepo.getInstance().saveInventory(inventoryOffline, new Repo.Callback() {
            @Override
            public void success(Object o) {
                hideLoading();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.STOCK_DATA_REMAINING_TO_SYNC, true);
                editor.apply();

                Toast.makeText(SearchStock.this, "Item added to inventory", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void fail() {
                hideLoading();
                AppUtils.showLog(TAG, "failed to save inventory");
            }
        });


    }


    private void initialize() {
        setUpToolbar(mToolbar);
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Add stocks");

        getMyApplication(this).getAppComponent().inject(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void pauseScanner() {
        mBarcodeView.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseScanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeScanner();
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
    }

    @Override
    public void addStockSuccess() {
        AppUtils.showLog(TAG, "Stock add success");
        Toast.makeText(this, "Inventory added", Toast.LENGTH_SHORT).show();

        presenter.getStockItems(token);

        finish();
    }

    @Override
    public void addStockFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void updateStockSuccess() {
        AppUtils.showLog(TAG, "update stock success");
        Toast.makeText(this, "inventory updated", Toast.LENGTH_SHORT).show();

        presenter.getStockItems(token);
        finish();

    }

    @Override
    public void updateStockFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_camera:
                /*if (!mSearchSku.isPopupShowing()) {
                    Toast.makeText(SearchStock.this, "Item not found", Toast.LENGTH_SHORT).show();
                }*/
                if (toggleCamera) {
                    mToggleCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_green));
                    mCameraHolder.setVisibility(View.VISIBLE);
                    toggleCamera = false;
                } else {
                    mToggleCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_disabled));
                    mCameraHolder.setVisibility(View.GONE);
                    toggleCamera = true;

                }

                break;

            case R.id.btn_add:
                validateFieldsThenAdd();
                break;

            case R.id.btn_increment:
                if (!mQuantity.getText().toString().isEmpty()) {
                    int value = Integer.valueOf(mQuantity.getText().toString());
                    value++;
                    mQuantity.setText(String.valueOf(value));
                }
                break;

            case R.id.btn_decrement:
                if (!mQuantity.getText().toString().isEmpty()) {
                    int value1 = Integer.valueOf(mQuantity.getText().toString());
                    if (value1 > 1) {
                        value1--;
                        mQuantity.setText(String.valueOf(value1));
                    }
                }
                break;

            case R.id.btn_increment_sp:
                if (!mSellingPrice.getText().toString().isEmpty()) {
                    if (mSellingPrice.getText().toString().contains(".")) {
                        String trimmedSellingPrice = mSellingPrice.getText().toString().substring(0, mSellingPrice.getText().length() - 2);
                        double price = Integer.valueOf(trimmedSellingPrice);
                        price++;
                        mSellingPrice.setText(String.valueOf(price));
                    } else {
                        double price = Double.valueOf(mSellingPrice.getText().toString());
                        price++;
                        mSellingPrice.setText(String.valueOf(price));
                    }

                }
                break;

            case R.id.btn_decrement_sp:
                if (!mSellingPrice.getText().toString().isEmpty()) {
                    if (mSellingPrice.getText().toString().contains(".")) {
                        String trimmedSellingPrice2 = mSellingPrice.getText().toString().substring(0, mSellingPrice.getText().length() - 2);
                        double price2 = Integer.valueOf(trimmedSellingPrice2);
                        if (price2 > 1) {
                            price2--;
                            mSellingPrice.setText(String.valueOf(price2));
                        }
                    } else {
                        double price2 = Double.valueOf(mSellingPrice.getText().toString());
                        if (price2 > 1) {
                            price2--;
                            mSellingPrice.setText(String.valueOf(price2));
                        }
                    }
                }
                break;

            case R.id.et_expiry_date:
                datePicker.onClick(view);
                break;

            case R.id.btn_add_to_inventory:
                mAddToInventory.setVisibility(View.GONE);
                mAddInventoryHolder.setVisibility(View.VISIBLE);
                break;

            case R.id.fab_camera_switch:
                switchCamera();
                break;

        }
    }

    private void switchCamera() {
        CameraSettings cameraSettings = mBarcodeView.getBarcodeView().getCameraSettings();

        if (mBarcodeView.getBarcodeView().isPreviewActive()) {
            mBarcodeView.pause();
        }


        if (cameraSettings.getRequestedCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraSettings.setRequestedCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            cameraSettings.setAutoFocusEnabled(true);
        } else {
            cameraSettings.setRequestedCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        mBarcodeView.getBarcodeView().setCameraSettings(cameraSettings);

        mBarcodeView.resume();

    }
}
