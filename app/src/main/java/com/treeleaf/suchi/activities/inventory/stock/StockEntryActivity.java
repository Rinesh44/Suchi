package com.treeleaf.suchi.activities.inventory.stock;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.BrandRepo;
import com.treeleaf.suchi.realm.repo.CategoryRepo;
import com.treeleaf.suchi.realm.repo.SubBrandRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class StockEntryActivity extends BaseActivity implements StockEntryView {
    private static final String TAG = "StockEntryActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.il_name)
    TextInputLayout mNameLayout;
    @BindView(R.id.et_name)
    TextInputEditText mName;
    @BindView(R.id.sp_brand)
    SearchableSpinner mBrand;
    @BindView(R.id.sp_sub_brand)
    SearchableSpinner mSubBrand;
    @BindView(R.id.sp_category)
    SearchableSpinner mCategory;
    @BindView(R.id.et_desc)
    TextInputEditText mDesc;
    @BindView(R.id.il_desc)
    TextInputLayout mDescLayout;
    @BindView(R.id.sp_unit)
    SearchableSpinner mUnit;
    @BindView(R.id.et_unit_price)
    TextInputEditText mUnitPrice;
    @BindView(R.id.il_unit_price)
    TextInputLayout mUnitPriceLayout;
    @BindView(R.id.iv_sku)
    ImageView mSku;
    @BindView(R.id.tv_add_sku_image)
    TextView mAddSkuImageText;
    @BindView(R.id.mcv_add_sku_image)
    MaterialCardView mAddSkuImage;
    @BindView(R.id.btn_add)
    MaterialButton mAdd;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    private List<Brands> brandItems = new ArrayList<>();
    private List<SubBrands> subBrandItems = new ArrayList<>();
    private List<Categories> categoryItems = new ArrayList<>();
    private List<Units> unitItems = new ArrayList<>();

    private SharedPreferences preferences;
    private StockEntryPresenter presenter;
    private String selectedBrandId, selectedSubBrandId, selectedCategoryId, selectedUnitId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_entry);

        ButterKnife.bind(this);

        getMyApplication(this).getAppComponent().inject(this);
        initialize();

        presenter = new StockEntryPresenterImpl(endpoints, this);

        brandItems = BrandRepo.getInstance().getAllBrands();
        categoryItems = CategoryRepo.getInstance().getAllCategories();
        unitItems = UnitRepo.getInstance().getAllUnits();

        setUpBrandSpinner();
        setUpCategorySpinner();
        setUpUnitSpinner();

        mBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Brands selectedBrand = brandItems.get(i);
                selectedBrandId = selectedBrand.getId();

                AppUtils.showLog(TAG, "brandId: " + selectedBrandId);

                subBrandItems = SubBrandRepo.getInstance().getSubBrandsWithRespectToBrandId(selectedBrandId);
                AppUtils.showLog(TAG, "subBrandItems: " + subBrandItems.size());
                setUpSubBrandSpinner(subBrandItems);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSubBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SubBrands subBrand = subBrandItems.get(i);
                selectedSubBrandId = subBrand.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Categories category = categoryItems.get(i);
                selectedCategoryId = category.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Units units = unitItems.get(i);
                selectedUnitId = units.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAddSkuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAllowFlipping(true)
                        .setAllowRotation(true)
                        .setRequestedSize(1024, 768, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(StockEntryActivity.this);
            }
        });


        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mName.getText().toString().trim();
                String unitPrice = mUnitPrice.getText().toString().trim();
                String description = mDesc.getText().toString().trim();

                String randomId = UUID.randomUUID().toString();
                String skuId = randomId.replace("-", "");

                InventoryProto.StockKeepingUnit stockKeepingUnit = InventoryProto.StockKeepingUnit.newBuilder()
                        .setName(name)
//                        .setSkuId(Long.valueOf(skuId))
                        .setDescription(description)
                        .setUnitPrice(Double.valueOf(unitPrice))
                        .setBrandId(selectedBrandId)
                        .setSubBrandId(selectedSubBrandId)
                        .setCategoryId(selectedCategoryId)
                        .setUnitId(selectedUnitId)
                        .build();

                String token = preferences.getString(Constants.TOKEN, "");

                if (token != null) {
                    showLoading();
                    presenter.makeStockEntry(token, stockKeepingUnit);
                } else
                    Toast.makeText(StockEntryActivity.this, "Unable to fetch token", Toast.LENGTH_SHORT).show();


            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    mSku.setVisibility(View.VISIBLE);
                    mSku.setImageBitmap(bitmap);
                    mAddSkuImageText.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                AppUtils.showLog(TAG, "error:  " + error);
            }
        }
    }

    private void setUpSubBrandSpinner(List<SubBrands> subBrandItems) {
        List<String> subBrandNameList = new ArrayList<>();
        for (SubBrands subBrands : subBrandItems
        ) {
            subBrandNameList.add(subBrands.getName());
        }

        ArrayAdapter<String> subBrandAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, subBrandNameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };
        mSubBrand.setAdapter(subBrandAdapter);
        mSubBrand.setPositiveButton("");

    }

    private void setUpUnitSpinner() {
        List<String> unitNameList = new ArrayList<>();
        for (Units unit : unitItems
        ) {
            unitNameList.add(unit.getName());
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, unitNameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };
        mUnit.setAdapter(unitAdapter);
        mUnit.setPositiveButton("");
    }

    private void setUpCategorySpinner() {
        List<String> categoryNameList = new ArrayList<>();
        for (Categories category : categoryItems
        ) {
            categoryNameList.add(category.getName());
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryNameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };
        mCategory.setAdapter(categoryAdapter);
        mCategory.setPositiveButton("");
    }

    private void setUpBrandSpinner() {
        List<String> brandNameList = new ArrayList<>();
        for (Brands brand : brandItems
        ) {
            brandNameList.add(brand.getName());
        }

        ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, brandNameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
//                TextView name = (TextView) view.findViewById(android.R.id.text1);
                return view;
            }
        };
        mBrand.setAdapter(brandAdapter);
        mBrand.setPositiveButton("");
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mToolbarTitle.setText("SKU Entry Form");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void stockEntrySuccess() {
        AppUtils.showLog(TAG, "stock entry success");
        finish();
    }

    @Override
    public void stockEntryFail(String msg) {
        showMessage(msg);

    }
}
