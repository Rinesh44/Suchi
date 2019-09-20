package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.CreditHistoryAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.CreditDto;
import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.repo.CreditRepo;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.treeleaf.suchi.SuchiApp.getMyApplication;

public class CreditHistory extends BaseActivity implements CreditHistoryView {
    private static final String TAG = "CreditHistory";
    @Inject
    Endpoints endpoints;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rv_credit_history)
    RecyclerView mCreditHistory;
    @BindView(R.id.tv_no_data)
    TextView mNoData;


    private CreditHistoryAdapter adapter;
    private SharedPreferences preferences;
    private String token;
    private CreditHisotryPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_history);

        ButterKnife.bind(this);

        initialize();

        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRecyclerView();
        invalidateOptionsMenu();

    }

    private void setUpRecyclerView() {
        mCreditHistory.setLayoutManager(new LinearLayoutManager(this));
        List<Credit> creditList = CreditRepo.getInstance().getCreditsWithDues();
        if (creditList == null || creditList.isEmpty()) mNoData.setVisibility(View.VISIBLE);
        List<CreditDto> creditDtoList = mapModelToDto(creditList);
        adapter = new CreditHistoryAdapter(this, creditDtoList);
        adapter.setOnItemClickListener(new CreditHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CreditDto creditDto) {
                Intent i = new Intent(CreditHistory.this, CreditDetails.class);
                i.putExtra("credit_info", creditDto);
                startActivity(i);
            }
        });
        mCreditHistory.setAdapter(adapter);
    }

    private List<CreditDto> mapModelToDto(List<Credit> creditList) {
        List<CreditDto> creditDtoList = new ArrayList<>();
        for (Credit credit : creditList
        ) {
            CreditDto creditDto = new CreditDto();
            creditDto.setId(credit.getId());
            creditDto.setCreatedAt(credit.getCreatedAt());
            creditDto.setCreditorId(credit.getCreditorId());
            creditDto.setDueAmount(credit.getDueAmount());
            creditDto.setPaidAmount(credit.getPaidAmount());
            creditDto.setSoldItems(credit.getSoldItems());
            creditDto.setSync(credit.isSync());
            creditDto.setTotalAmount(credit.getTotalAmount());
            creditDto.setUpdatedAt(credit.getUpdatedAt());
            creditDto.setUserId(credit.getUserId());

            creditDtoList.add(creditDto);

        }

        return creditDtoList;
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.credit_history));

        getMyApplication(this).getAppComponent().inject(this);
        presenter = new CreditHisotryPresenterImpl(endpoints, this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = preferences.getString(Constants.TOKEN, "");

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (preferences.getBoolean(Constants.CREDIT_DATA_REMAINING_TO_SYNC, false))
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (NetworkUtils.isNetworkConnected(this)) {
                    List<Creditors> unsyncedCreditors = CreditorRepo.getInstance().getUnsyncedCreditors();
                    if (token != null) {
                        if (unsyncedCreditors != null && !unsyncedCreditors.isEmpty()) {
                            showLoading();
                            presenter.syncCreditors(token, unsyncedCreditors);
                        } else
                            Toast.makeText(this, "No creditor data found to sync", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Please connect to internet.", Toast.LENGTH_SHORT).show();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void syncCreditorsSuccess() {
        hideLoading();
        AppUtils.showLog(TAG, "sync creditors success");
        List<Credit> credits = CreditRepo.getInstance().getUnsyncedCredits();
        if (credits != null && !credits.isEmpty()) {
            presenter.syncCredits(token, credits);
        } else {
            Toast.makeText(this, "No credit data found to sync", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void syncCreditorsFail(String msg) {
        hideLoading();
        AppUtils.showLog(TAG, msg);

    }

    @Override
    public void syncCreditsSuccess() {
        AppUtils.showLog(TAG, "sync credit success");

    }

    @Override
    public void syncCreditsFail(String msg) {
        AppUtils.showLog(TAG, msg);
    }
}
