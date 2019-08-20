package com.treeleaf.suchi.activities.sales;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.inventory.stock.SearchStock;
import com.treeleaf.suchi.adapter.AutocompleteSearchAdapter;
import com.treeleaf.suchi.adapter.InventorySalesAdapter;
import com.treeleaf.suchi.dto.InventoryDto;
import com.treeleaf.suchi.dto.InventoryStocksDto;
import com.treeleaf.suchi.dto.StockKeepingUnitDto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.StockKeepingUnitRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class AddSalesActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddSalesActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.et_search)
    AppCompatAutoCompleteTextView mSearch;
    @BindView(R.id.iv_barcode)
    ImageView mBarcode;
    @BindView(R.id.tv_item_title)
    TextView mItemName;
    @BindView(R.id.tv_item_cost)
    TextView mItemCost;
    @BindView(R.id.tv_quantity)
    EditText mQuantity;
    @BindView(R.id.sp_unit)
    AppCompatSpinner mUnit;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.btn_sell)
    MaterialButton mSell;
    @BindView(R.id.btn_increment)
    ImageButton mIncrement;
    @BindView(R.id.btn_decrement)
    ImageButton mDecrement;
    @BindView(R.id.ll_sale_detail_holder)
    LinearLayout mSaleDetails;
    @BindView(R.id.iv_item)
    CircleImageView mItemImage;

    private List<Inventory> inventoryList = new ArrayList<>();
    private List<Units> unitList = new ArrayList<>();
    private String selectedItemId, selectedItemUnitId;
    private List<String> unitItems = new ArrayList<>();
    private ArrayAdapter<String> unitItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales);


        ButterKnife.bind(this);

        initialize();
        setUpSearch();
        setUpUnitSpinner();
        updateTotalAmount();

        mBarcode.setOnClickListener(this);
        mIncrement.setOnClickListener(this);
        mDecrement.setOnClickListener(this);
        mSell.setOnClickListener(this);

        mSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
                InventoryDto selectedItem = (InventoryDto) adapterView.getItemAtPosition(i);
                mSearch.setText(selectedItem.getSku().getName());


                int defaultUnitPosition = unitItemsAdapter.getPosition(selectedItem.getSku().getDefaultUnit());
                mUnit.setSelection(defaultUnitPosition);


                mItemName.setText(selectedItem.getSku().getName());
                StringBuilder itemCostBuilder = new StringBuilder();
                itemCostBuilder.append("Rs. ");
                itemCostBuilder.append(selectedItem.getSku().getUnitPrice());
                mItemCost.setText(itemCostBuilder);

                String imageUrl = selectedItem.getSku().getPhoto_url();
                AppUtils.showLog(TAG, "ImageUrl: " + imageUrl);
                if (!imageUrl.isEmpty()) {
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.ic_stock)
                            .error(R.drawable.ic_stock);

                    Glide.with(AddSalesActivity.this).load(imageUrl).apply(options).into(mItemImage);
                }

                mSaleDetails.setVisibility(View.VISIBLE);
            }
        });

        mUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                int itemPosition = unitItems.indexOf(item);

                AppUtils.showLog(TAG, "itemPosition: " + itemPosition);

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

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Items Sales");

        getMyApplication(this).getAppComponent().inject(this);

    }

    private void setUpUnitSpinner() {
        unitList = UnitRepo.getInstance().getAllUnits();
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

        mUnit.setAdapter(unitItemsAdapter);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_barcode:
                break;

            case R.id.btn_increment:
                int value = Integer.valueOf(mQuantity.getText().toString());
                value++;
                mQuantity.setText(String.valueOf(value));
                updateTotalAmount();
                break;

            case R.id.btn_decrement:
                int value1 = Integer.valueOf(mQuantity.getText().toString());
                if (value1 != 1) {
                    value1--;
                    mQuantity.setText(String.valueOf(value1));
                }
                updateTotalAmount();
                break;

            case R.id.btn_sell:
                break;

        }
    }

    private void updateTotalAmount() {
        String itemCost = mItemCost.getText().toString().substring(4, mItemCost.getText().length());
        double sum = Double.valueOf(mQuantity.getText().toString().trim()) *
                Double.valueOf(itemCost);

        StringBuilder totalAmountBuilder = new StringBuilder();
        totalAmountBuilder.append("Rs. ");
        totalAmountBuilder.append(String.valueOf(sum));
        mTotalAmount.setText(totalAmountBuilder);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void setUpSearch() {
        inventoryList = InventoryRepo.getInstance().getSyncedInventories();
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
}
