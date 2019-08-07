package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.realm.models.Items;
import com.treeleaf.suchi.realm.models.Token;
import com.treeleaf.suchi.realm.repo.UserRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.DatePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class SearchStock extends BaseActivity implements SearchStockView {
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
    @BindView(R.id.il_quantity)
    TextInputLayout mQuantityLayout;
    @BindView(R.id.et_quantity)
    TextInputEditText mQuantity;
    @BindView(R.id.il_expiry_date)
    TextInputLayout mExpiryDateLayout;
    @BindView(R.id.et_expiry_date)
    TextInputEditText mExpiryDate;
    @BindView(R.id.btn_add)
    MaterialButton mAdd;
    @BindView(R.id.iv_sku)
    ImageView mSkuImage;
    @BindView(R.id.il_marked_price)
    TextInputLayout mMarkedPriceLayout;
    @BindView(R.id.et_marked_price)
    TextInputEditText mMarkedPrice;
    @BindView(R.id.il_selling_price)
    TextInputLayout mSellingPriceLayout;
    @BindView(R.id.et_selling_price)
    TextInputEditText mSellingPrice;


/*    @BindView(R.id.tv_description)
    TextView mDescription;*/

    private SearchStockPresenter presenter;
    private List<Items> mSkuItems = new ArrayList<>();
    private String selectedItemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stock);

        ButterKnife.bind(this);

        initialize();

        presenter = new SearchStockPresenterImpl(endpoints, this);

        final DatePicker datePicker = new DatePicker(this, R.id.et_expiry_date);

        mExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.onClick(view);
            }
        });

        getIntentData();

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateFieldsThenAdd();

            }
        });
    }

    private void getIntentData() {
        Intent i = getIntent();
        Items selectedItem = i.getParcelableExtra("selected_sku");
        selectedItemId = selectedItem.getId();


        mSkuName.setText(selectedItem.getName());
        mBrand.setText(selectedItem.getBrand().getName());
        mSubBrand.setText(selectedItem.getSubBrands().getName());
        mUnitPrice.setText(selectedItem.getUnitPrice());
        mCategory.setText(selectedItem.getCategories().getName());
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

    }

    private void validateFieldsThenAdd() {

        if (mQuantity.getText().toString().isEmpty() || mQuantity.getText().toString().equals("0")) {
            mQuantityLayout.setErrorEnabled(true);
            mQuantityLayout.setError("This field is required");
            mQuantityLayout.requestFocus();
            return;
        } else {
            mQuantityLayout.setErrorEnabled(false);
        }

        if (mMarkedPrice.getText().toString().isEmpty() || mMarkedPrice.getText().toString().equals("0")) {
            mMarkedPriceLayout.setErrorEnabled(true);
            mMarkedPriceLayout.setError("This field is required");
            mMarkedPriceLayout.requestFocus();
            return;
        } else {
            mMarkedPriceLayout.setErrorEnabled(false);
        }

        if (mSellingPrice.getText().toString().isEmpty() || mSellingPrice.getText().toString().equals("0")) {
            mSellingPriceLayout.setErrorEnabled(true);
            mSellingPriceLayout.setError("This field is required");
            mSellingPriceLayout.requestFocus();
            return;
        } else {
            mSellingPriceLayout.setErrorEnabled(false);
        }

        if (mExpiryDate.getText().toString().isEmpty()) {
            mExpiryDateLayout.setErrorEnabled(true);
            mExpiryDateLayout.setError("This field is required");
            mExpiryDateLayout.requestFocus();
            return;
        } else {
            mExpiryDateLayout.setErrorEnabled(false);
        }


        showLoading();
        Token token = UserRepo.getInstance().getToken();

        String randomInventoryId = UUID.randomUUID().toString();

        AppUtils.showLog(TAG, "inventoryId: " + randomInventoryId);
        AppUtils.showLog(TAG, "skuId: " + selectedItemId);

        InventoryProto.Inventory inventory = InventoryProto.Inventory.newBuilder()
                .setInventoryId(randomInventoryId)
                .setSkuId(selectedItemId)
                .setMarkedPrice(Double.valueOf(mMarkedPrice.getText().toString()))
                .setSalesPrice(Double.valueOf(mSellingPrice.getText().toString()))
                .setQuantity(Integer.valueOf(mQuantity.getText().toString()))
                .setExpiryDate(System.currentTimeMillis())
                .build();

        ReqResProto.Request request = ReqResProto.Request.newBuilder()
                .setInventory(inventory)
                .build();

        presenter.addStock(token.getToken(), request);

    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("SKU Details");

        getMyApplication(this).getAppComponent().inject(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void addStockSuccess() {
        AppUtils.showLog(TAG, "Stock add success");
    }

    @Override
    public void addStockFail(String msg) {
        showMessage(msg);
    }
}
