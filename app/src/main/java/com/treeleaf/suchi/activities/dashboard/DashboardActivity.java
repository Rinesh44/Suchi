package com.treeleaf.suchi.activities.dashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.treeleaf.suchi.MainActivity;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.Settings.Settings;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.credit.CreditActivity;
import com.treeleaf.suchi.activities.inventory.InventoryActivity;
import com.treeleaf.suchi.activities.profile.ProfileActivity;
import com.treeleaf.suchi.activities.sales.SalesActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesRepo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.LocaleHelper;
import com.treeleaf.suchi.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class DashboardActivity extends BaseActivity implements DashboardView, View.OnClickListener {
    private static final String TAG = "DashboardActivity";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.btn_inventory)
    MaterialCardView mInventory;
    @BindView(R.id.btn_credit)
    MaterialCardView mCredit;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nv)
    NavigationView mNavigationView;
    @BindView(R.id.tv_inventory_title)
    TextView mInventoryTitle;
    @BindView(R.id.tv_credit_title)
    TextView mCreditTitle;
    @BindView(R.id.tv_remaining_days)
    TextView mRemainingDays;

    private DashboardPresenter presenter;
    private SharedPreferences preferences;
    private ActionBarDrawerToggle actionBarToggle;
    private String token, userId;

    @Override
    protected void onResume() {
        AppUtils.showLog(TAG, "onResume()");
        super.onResume();
        Menu menu = mNavigationView.getMenu();
        MenuItem nav_profile = menu.findItem(R.id.profile);
        MenuItem nav_sales = menu.findItem(R.id.sync);
        MenuItem nav_settings = menu.findItem(R.id.settings);
        MenuItem nav_logout = menu.findItem(R.id.logout);


        String selectedLanguage = preferences.getString(Constants.SELECTED_LANGUAGE, "");
        Context context = LocaleHelper.setLocale(this, selectedLanguage);
        Resources resources = context.getResources();
        mInventoryTitle.setText(resources.getString(R.string.inventory));
        mCreditTitle.setText(resources.getString(R.string.credit));
        nav_profile.setTitle(resources.getString(R.string.profile));
        nav_settings.setTitle(resources.getString(R.string.settings));
        nav_sales.setTitle(resources.getString(R.string.sync));
        nav_logout.setTitle(resources.getString(R.string.logout));
        mToolbarTitle.setText(resources.getString(R.string.dashboard));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        getMyApplication(this).getAppComponent().inject(this);

        init();
        checkIfFreeTrial();
        token = preferences.getString(Constants.TOKEN, "");
        userId = preferences.getString(Constants.USER_ID, "");
        presenter = new DashboardPresenterImpl(endpoints, this);

        mInventory.setOnClickListener(this);
        mCredit.setOnClickListener(this);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                        break;

                    case R.id.sync:
                        syncData();
                        break;

                    case R.id.settings:
                        startActivity(new Intent(DashboardActivity.this, Settings.class));
                        break;
                    case R.id.logout:
                        showLogoutDialog();
                        break;
                    default:
                        return true;
                }

                mDrawerLayout.closeDrawers();
                return true;

            }
        });

    }

    private void checkIfFreeTrial() {
        Intent i = getIntent();
        boolean freeTrial = i.getBooleanExtra("free_trial", false);

        if (freeTrial) {
            showFreeTrialDialog();
        }

    }

    public void showFreeTrialDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.free_trial_dialog);
        dialog.setCancelable(true);

        dialog.show();

    }

    private void syncData() {
        if (NetworkUtils.isNetworkConnected(this)) {
            List<Sales> unSyncedSalesList = SalesRepo.getInstance().getUnsyncedSalesList();
            if (token != null) {
                if (unSyncedSalesList != null && !unSyncedSalesList.isEmpty()) {
                    showLoading();
                    postSalesDataToServer(unSyncedSalesList);
                } else
                    Toast.makeText(this, "No data found to sync", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Please connect to internet.", Toast.LENGTH_SHORT).show();
        }

    }

    private void postSalesDataToServer(List<Sales> unSyncedSalesList) {
        List<SuchiProto.Sale> saleListProto = mapSalesToProto(unSyncedSalesList);
        SuchiProto.SyncRequest syncRequest = SuchiProto.SyncRequest.newBuilder()
                .addAllSales(saleListProto)
                .build();

        presenter.syncSalesData(token, syncRequest);
    }

    private List<SuchiProto.Sale> mapSalesToProto(List<Sales> unSyncedSalesList) {
        List<SuchiProto.Sale> salesList = new ArrayList<>();
        for (Sales sales : unSyncedSalesList
        ) {
            List<SalesStock> salesStockList = sales.getSalesStocks();
            List<SuchiProto.SaleInventory> saleInventoryList = new ArrayList<>();
            for (SalesStock salesStock : salesStockList
            ) {

                String unitId = UnitRepo.getInstance().getUnitIdByUnitName(salesStock.getUnit());

                String formattedQuantity = "";
                if (salesStock.getQuantity().contains(".")) {
                    formattedQuantity = salesStock.getQuantity().substring
                            (0, salesStock.getQuantity().length() - 2);
                } else formattedQuantity = salesStock.getQuantity();

                AppUtils.showLog(TAG, "formatedQwuantitY; " + formattedQuantity);

                SuchiProto.SaleInventory saleInventory = SuchiProto.SaleInventory.newBuilder()
                        .setAmount(Double.valueOf(salesStock.getAmount()))
//                        .setCreatedAt(salesStock.getCreatedAt())
                        .setInventoryId(salesStock.getInventory_id())
                        .setInventoryStockId(salesStock.getId())
                        .setQuantity(Integer.valueOf(formattedQuantity))
                        .setUnitId(unitId)
//                        .setUpdatedAt(salesStock.getUpdatedAt())
                        .build();

                saleInventoryList.add(saleInventory);
            }


            SuchiProto.Sale saleProto = SuchiProto.Sale.newBuilder()
                    .setSaleId(sales.getSaleId())
                    .setAmount(Double.valueOf(sales.getTotalAmount()))
                    .setCreatedAt(sales.getCreatedAt())
                    .setUpdatedAt(sales.getUpdatedAt())
                    .addAllSaleInventories(saleInventoryList)
                    .setUserId(userId)
                    .build();

            salesList.add(saleProto);
        }

        return salesList;
    }


    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mToolbarTitle.setText(getResources().getString(R.string.dashboard));

        actionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mNavigationView.getMenu().getItem(0).setChecked(true);

        long freeTrialExpiryDate = preferences.getLong(Constants.FREE_TRIAL_EXPIRY_DATE, 0);
        AppUtils.showLog(TAG, "expiryDate: " + freeTrialExpiryDate);

        if (freeTrialExpiryDate != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(freeTrialExpiryDate);

            long msDiff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
            String daysDiff = String.valueOf(TimeUnit.MILLISECONDS.toDays(msDiff) - 1).replace("-", "");

            mRemainingDays.setVisibility(View.VISIBLE);

            mRemainingDays.setText(daysDiff + getString(R.string.days_remaining));

        }

    }


    @Override
    public void logoutSuccess() {
        AppUtils.showLog(TAG, "logout success");

        //clear shared prefs
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();


        Intent i = new Intent(DashboardActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public void logoutFail(String msg) {
        showMessage(msg);
    }

    @Override
    public void syncSuccess() {
        AppUtils.showLog(TAG, "sync success");
        Toast.makeText(this, "Sync success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void syncFailed(String msg) {
        AppUtils.showLog(TAG, msg);
    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String token = preferences.getString(Constants.TOKEN, "");
                        showLoading();
                        if (token != null) presenter.logout(token);
                        else
                            Toast.makeText(DashboardActivity.this, "Unable to get token", Toast.LENGTH_SHORT).show();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setBackgroundColor(getResources().getColor(R.color.transparent));
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.transparent));
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

            }
        });
        alert11.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_inventory:
                startActivity(new Intent(DashboardActivity.this, InventoryActivity.class));
                break;

            case R.id.btn_credit:
                startActivity(new Intent(DashboardActivity.this, CreditActivity.class));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarToggle.syncState();
    }
}
