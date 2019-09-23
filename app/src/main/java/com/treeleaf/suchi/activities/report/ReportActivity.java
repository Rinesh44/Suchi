package com.treeleaf.suchi.activities.report;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.report.fragments.Chart;
import com.treeleaf.suchi.activities.report.fragments.Table;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.BrandRepo;
import com.treeleaf.suchi.realm.repo.CategoryRepo;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.realm.repo.SubBrandRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends BaseActivity {
    private static final String TAG = "ReportActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.bottom_sheet)
    LinearLayout mBottomSheet;

    private SharedPreferences sharedPreferences;
    private String token, userId;
    private BottomSheetBehavior sheetBehavior;
    private ViewPagerAdapter viewPagerAdapter;
    private OnListReceiveListener onListReceiveListener;
    private int selectedFilter = 1;
    private AppCompatSpinner brandSpinner, subBrandSpinner, categorySpinner, itemSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        ButterKnife.bind(this);
        initialize();

        setupViewPager(mViewpager);
        mTabs.setupWithViewPager(mViewpager);

        EditText mFromDate = mBottomSheet.findViewById(R.id.et_from_date);
        EditText mToDate = mBottomSheet.findViewById(R.id.et_to_date);
        MaterialButton mDone = mBottomSheet.findViewById(R.id.btn_done);
        AppCompatSpinner mFilterSpinner = mBottomSheet.findViewById(R.id.sp_filter);

        setUpFilterAdapter(mFilterSpinner);

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = new DatePicker(ReportActivity.this, mFromDate.getId());
                datePicker.onClick(view);
            }
        });


        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = new DatePicker(ReportActivity.this, mToDate.getId());
                datePicker.onClick(view);
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectedFilter) {
                    case 1:
                        filterByBrand();
                        mViewpager.setCurrentItem(1);
                        break;

                    case 2:
                        filterBySubBrand();
                        mViewpager.setCurrentItem(1);
                        break;

                    case 3:
                        filterByCategories();
                        mViewpager.setCurrentItem(1);
                        break;

                    case 4:
                        filterByDates(mFromDate, mToDate);
                        mViewpager.setCurrentItem(1);
                        break;

                    case 5:
                        filterByItems();
                        mViewpager.setCurrentItem(1);
                        break;
                }


            }
        });

    }

    private void filterByItems() {
        List<SalesStock> stocksByItems = SalesStockRepo.getInstance().getSalesStockByItem((String) itemSpinner.getSelectedItem());
        toggleBottomSheet();
        onListReceiveListener.onListReceive(stocksByItems);
    }

    private void filterByCategories() {
        List<SalesStock> stocksByCategory = SalesStockRepo.getInstance().getSalesStockByCategory((String) categorySpinner.getSelectedItem());
        toggleBottomSheet();
        onListReceiveListener.onListReceive(stocksByCategory);
    }

    private void filterBySubBrand() {
        List<SalesStock> stocksBySubBrand = SalesStockRepo.getInstance().getSalesStockBySubBrand((String) subBrandSpinner.getSelectedItem());
        toggleBottomSheet();
        onListReceiveListener.onListReceive(stocksBySubBrand);

    }

    private void filterByBrand() {
        List<SalesStock> stocksByBrand = SalesStockRepo.getInstance().getSalesStockByBrand((String) brandSpinner.getSelectedItem());
        toggleBottomSheet();
        onListReceiveListener.onListReceive(stocksByBrand);
    }

    private void filterByDates(EditText mFromDate, EditText mToDate) {
        if (mFromDate.getText().toString().isEmpty() ||
                mToDate.getText().toString().isEmpty()) {
            Toast.makeText(ReportActivity.this, "Please enter the dates", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromDate = mFromDate.getText().toString().trim();
        String tillDate = mToDate.getText().toString().trim();

        Date startDate = getTimeStampFromDate(fromDate);
        startDate.setHours(6);
        startDate.setMinutes(0);

        Date endDate = getTimeStampFromDate(tillDate);
        endDate.setHours(23);
        endDate.setMinutes(59);

        long startDateTimeStamp = startDate.getTime();
        long endDateTimeStamp = endDate.getTime();

        AppUtils.showLog(TAG, "startDate: " + startDateTimeStamp);
        AppUtils.showLog(TAG, "endDate: " + endDateTimeStamp);

        AppUtils.showLog(TAG, "postStartDate: " + AppUtils.getDate(startDateTimeStamp));
        AppUtils.showLog(TAG, "postEndDate: " + AppUtils.getDate(endDateTimeStamp));

        List<SalesStock> salesStockListByDate = SalesStockRepo.getInstance().
                getSalesStockByDate(startDateTimeStamp, endDateTimeStamp);

        if (salesStockListByDate != null && !salesStockListByDate.isEmpty()) {
            AppUtils.showLog(TAG, "salesStockByDate: " + salesStockListByDate.size());

            toggleBottomSheet();
            onListReceiveListener.onListReceive(salesStockListByDate);
        } else {
            toggleBottomSheet();
            Toast.makeText(ReportActivity.this, "Data not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpFilterAdapter(AppCompatSpinner filterSpinner) {
        String[] filterItems = new String[]{"Brand", "Sub-Brand", "Category", "Date", "Item"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, filterItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getSelectedItem();
                RelativeLayout brandLayout = mBottomSheet.findViewById(R.id.rl_brand);
                RelativeLayout subBrandLayout = mBottomSheet.findViewById(R.id.rl_sub_brand);
                RelativeLayout categoryLayout = mBottomSheet.findViewById(R.id.rl_category);
                RelativeLayout dateLayout = mBottomSheet.findViewById(R.id.rl_date);
                RelativeLayout itemLayout = mBottomSheet.findViewById(R.id.rl_item);


                switch (selectedItem) {
                    case "Brand":
                        brandSpinner = mBottomSheet.findViewById(R.id.sp_brand);
                        setUpBrandSpinner(brandSpinner);
                        brandLayout.setVisibility(View.VISIBLE);
                        subBrandLayout.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                        itemLayout.setVisibility(View.GONE);
                        selectedFilter = 1;
                        break;

                    case "Sub-Brand":
                        subBrandSpinner = mBottomSheet.findViewById(R.id.sp_sub_brand);
                        setUpSubBrandSpinner(subBrandSpinner);
                        brandLayout.setVisibility(View.GONE);
                        subBrandLayout.setVisibility(View.VISIBLE);
                        categoryLayout.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                        itemLayout.setVisibility(View.GONE);
                        selectedFilter = 2;
                        break;

                    case "Category":
                        categorySpinner = mBottomSheet.findViewById(R.id.sp_category);
                        setUpCategorySpinner(categorySpinner);
                        brandLayout.setVisibility(View.GONE);
                        subBrandLayout.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.VISIBLE);
                        dateLayout.setVisibility(View.GONE);
                        itemLayout.setVisibility(View.GONE);
                        selectedFilter = 3;
                        break;

                    case "Date":
                        brandLayout.setVisibility(View.GONE);
                        subBrandLayout.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.VISIBLE);
                        itemLayout.setVisibility(View.GONE);
                        selectedFilter = 4;
                        break;

                    case "Item":
                        itemSpinner = mBottomSheet.findViewById(R.id.sp_item);
                        setUpItemSpinner(itemSpinner);
                        brandLayout.setVisibility(View.GONE);
                        subBrandLayout.setVisibility(View.GONE);
                        categoryLayout.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                        itemLayout.setVisibility(View.VISIBLE);
                        selectedFilter = 5;
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpItemSpinner(AppCompatSpinner itemSpinner) {
        List<String> allStockNames = InventoryRepo.getInstance().getAllStockNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allStockNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(adapter);
    }

    private void setUpBrandSpinner(AppCompatSpinner brandSpinner) {
        List<String> brandNamesList = BrandRepo.getInstance().getAllBrandNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, brandNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(adapter);

    }

    private void setUpSubBrandSpinner(AppCompatSpinner subBrandSpinner) {
        List<String> subBrandNameList = SubBrandRepo.getInstance().getAllSubBrandNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, subBrandNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subBrandSpinner.setAdapter(adapter);

    }

    private void setUpCategorySpinner(AppCompatSpinner categorySpinner) {
        List<String> categoryNameList = CategoryRepo.getInstance().getAllCategoryNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

    }

    public interface OnListReceiveListener {
        void onListReceive(List<SalesStock> salesStockListByDate);
    }

    public void setListReceiveListener(OnListReceiveListener listener) {
        this.onListReceiveListener = listener;
    }

    private Date getTimeStampFromDate(String stringDate) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return (Date) formatter.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void initialize() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbarTitle.setText(getResources().getString(R.string.report));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString(Constants.TOKEN, "");
        userId = sharedPreferences.getString(Constants.USER_ID, "");

        sheetBehavior = BottomSheetBehavior.from(mBottomSheet);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Chart(), getResources().getString(R.string.chart));
        viewPagerAdapter.addFragment(new Table(), getResources().getString(R.string.table));
        viewPager.setAdapter(viewPagerAdapter);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (sharedPreferences.getBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, false))
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                toggleBottomSheet();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * manually opening / closing bottom sheet on button click
     */
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                mBottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void postSalesDataToServer(List<SalesStock> unsyncedSalesList) {
        List<SuchiProto.Sale> saleListProto = mapSalesStockToProto(unsyncedSalesList);
        SuchiProto.SyncRequest syncRequest = SuchiProto.SyncRequest.newBuilder()
                .addAllSales(saleListProto)
                .build();

//        presenter.syncSalesData(token, syncRequest);

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
                    .setCreatedAt(saleStock.getCreatedAt())
                    .setUpdatedAt(saleStock.getUpdatedAt())
                    .build();


            String saleId = UUID.randomUUID().toString();
            String formattedSaleId = saleId.replace("-", "");

            SuchiProto.Sale saleProto = SuchiProto.Sale.newBuilder()
                    .setAmount(Double.valueOf(saleStock.getAmount()))
                    .setCreatedAt(System.currentTimeMillis())
                    .setSaleId(formattedSaleId)
                    .setUserId(userId)
                    .setCreatedAt(saleInventoryProto.getCreatedAt())
                    .setUpdatedAt(saleInventoryProto.getUpdatedAt())
                    .addSaleInventories(saleInventoryProto)
                    .build();

            saleListProto.add(saleProto);

        }

        return saleListProto;
    }

}
