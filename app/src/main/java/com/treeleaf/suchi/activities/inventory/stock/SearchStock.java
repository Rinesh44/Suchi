package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.AutocompleteSearchAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.entities.SuchiProto;
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
import com.treeleaf.suchi.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
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
    @BindView(R.id.iv_go)
    public ImageView mGo;
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


    private DatePicker datePicker;
    private String token;
    private String unitPrice;
    private StockKeepingUnitDto selectedItem;

/*    @BindView(R.id.tv_description)
    TextView mDescription;*/

    private SearchStockPresenter presenter;
    private List<StockKeepingUnit> mSkuItems = new ArrayList<>();
    private List<String> skuList = new ArrayList<>();
    private List<Units> unitList = new ArrayList<>();
    private List<String> unitItems = new ArrayList<>();
    private String selectedItemId, selectedItemUnitId;

    private SharedPreferences sharedPreferences;
    private String userId, inventoryId, inventoryStockId;
    private boolean update = false;
    private ArrayAdapter<String> unitItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_for_keyboard_glitch));
        setContentView(R.layout.activity_search_stock);

        ButterKnife.bind(this);

        initialize();

        mAdd.setOnClickListener(this);
        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mGo.setOnClickListener(this);
        mExpiryDate.setOnClickListener(this);
        mAddToInventory.setOnClickListener(this);
        mSellingPriceDecrement.setOnClickListener(this);
        mSellingPriceIncrement.setOnClickListener(this);


        setUpSearch();

        presenter = new SearchStockPresenterImpl(endpoints, this);


        mSearchSku.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();

                selectedItem = (StockKeepingUnitDto) adapterView.getItemAtPosition(i);
                mSearchSku.setText(selectedItem.getName());

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
        });

        datePicker = new DatePicker(this, R.id.et_expiry_date);

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


        List<Inventory> syncedInventories = InventoryRepo.getInstance().getSyncedInventories();
        if (syncedInventories == null || syncedInventories.isEmpty()) {
            String randomInventoryId = UUID.randomUUID().toString();
            inventoryId = randomInventoryId.replace("-", "");
        } else {
            for (Inventory inventory : syncedInventories
            ) {
                if (inventory.getSku().getId().equals(selectedItemId)) {
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

        String randomInventoryStockId = UUID.randomUUID().toString();
        inventoryStockId = randomInventoryStockId.replace("-", "");

        SuchiProto.InventoryStock inventoryStock = SuchiProto.InventoryStock.newBuilder()
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
                .build();

        if (NetworkUtils.isNetworkConnected(this)) {
            showLoading();
            if (token != null) {
                if (!update) presenter.addStock(token, inventory);
                else presenter.updateStock(token, inventory);

            } else Toast.makeText(this, "Unable to get token", Toast.LENGTH_SHORT).show();
        } else {
            //network is not connected, so save to realm as unsynced
            saveToRealm(false);
        }

    }


    private void saveToRealm(boolean syncValue) {

        InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockId, inventoryId, mQuantity.getText().toString().trim(),
                mMarkedPrice.getText().toString().trim(), mSellingPrice.getText().toString().trim(),
                selectedItemUnitId, false);

        RealmList<InventoryStocks> inventoryStocksRealmList = new RealmList<>();
        inventoryStocksRealmList.add(inventoryStocks);

        Inventory inventoryOffline = new Inventory();
        inventoryOffline.setInventory_id(inventoryId);
        inventoryOffline.setInventoryStocks(inventoryStocksRealmList);
        inventoryOffline.setExpiryDate(String.valueOf(System.currentTimeMillis()));
        inventoryOffline.setSkuId(selectedItemId);
        inventoryOffline.setUser_id(userId);
        inventoryOffline.setSynced(syncValue);

        InventoryRepo.getInstance().saveInventory(inventoryOffline, new Repo.Callback() {
            @Override
            public void success(Object o) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.DATA_REMAINING_TO_SYNC, true);
                editor.apply();

                Toast.makeText(SearchStock.this, "Item added to inventory", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save inventory");
            }
        });
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Select Item");

        getMyApplication(this).getAppComponent().inject(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            case R.id.iv_go:
                if (!mSearchSku.isPopupShowing()) {
                    Toast.makeText(SearchStock.this, "Item not found", Toast.LENGTH_SHORT).show();
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

        }
    }
}
