package com.treeleaf.suchi.activities.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.treeleaf.suchi.MainActivity;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.credit.CreditActivity;
import com.treeleaf.suchi.activities.inventory.InventoryActivity;
import com.treeleaf.suchi.activities.profile.ProfileActivity;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

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
    @BindView(R.id.iv_menu)
    ImageView mMenu;

    private DashboardPresenter presenter;
    private SharedPreferences preferences;
    private ActionBarDrawerToggle actionBarToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        getMyApplication(this).getAppComponent().inject(this);

        init();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        presenter = new DashboardPresenterImpl(endpoints, this);

        mInventory.setOnClickListener(this);
        mCredit.setOnClickListener(this);
        mMenu.setOnClickListener(this);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                        break;
                    case R.id.logout:
                        Toast.makeText(DashboardActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return true;
                }

                return true;

            }
        });

    }


    private void init() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Dashboard");

        actionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarToggle);
        actionBarToggle.syncState();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.action_logout:
                showLogoutDialog();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

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

            case R.id.iv_menu:
                Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
