package com.treeleaf.suchi.activities.report;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.DatePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportActivity extends BaseActivity implements Chart.OnFragmentInteractionListener, Table.OnFragmentInteractionListener {
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
                if (mFromDate.getText().toString().isEmpty() ||
                        mToDate.getText().toString().isEmpty()) {
                    Toast.makeText(ReportActivity.this, "Please enter the dates", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ReportActivity.this, "done clicked", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void initialize() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbarTitle.setText("Report");

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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Table(), "TABLE");
        adapter.addFragment(new Chart(), "CHART");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
