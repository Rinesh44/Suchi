package com.treeleaf.suchi.activities.credit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.sales.AddSalesActivity;
import com.treeleaf.suchi.adapter.CreditHistoryAdapter;
import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.dto.CreditDto;
import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.repo.CreditRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

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
    @BindView(R.id.et_search)
    EditText mSearch;
    @BindView(R.id.tv_total_amount_top)
    TextView mTotalAmtTop;
    @BindView(R.id.tv_total_amount_bottom)
    TextView mTotalAmtBottom;
    @BindView(R.id.ll_total_amount_holder)
    LinearLayout mTotalAmtBottomHolder;
    @BindView(R.id.ll_table_titles)
    LinearLayout mTableTitles;
    @BindView(R.id.fab_add_credit)
    FloatingActionButton mAddCredit;


    private CreditHistoryAdapter adapter;
    private SharedPreferences preferences;
    private String token;
    private CreditHisotryPresenter presenter;
    private double totalAmount = 0;
    private List<Credit> creditList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_history);

        ButterKnife.bind(this);

        initialize();

        creditList = CreditRepo.getInstance().getAllCredits();
        Collections.sort(creditList, new Comparator<Credit>() {
            @Override
            public int compare(Credit o1, Credit o2) {
                return Long.compare(o2.getCreatedAt(), o1.getCreatedAt());
            }
        });

        setUpRecyclerView(creditList);
        hideFabWhenScrolled();

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());

                List<CreditDto> filteredList = adapter.getFilteredList();
                AppUtils.showLog(TAG, "filteredListSize: " + filteredList.size());
                if (filteredList.size() > 0) {
//                    makeViewsVisible();
                    calculateTotal(filteredList);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAddCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreditHistory.this, AddSalesActivity.class);
                i.putExtra("credit", true);
                startActivity(i);
            }
        });
    }

    private void makeViewsInVisible() {
        mNoData.setVisibility(View.VISIBLE);
        mTotalAmtBottomHolder.setVisibility(View.GONE);
        mTableTitles.setVisibility(View.GONE);
        mTotalAmtTop.setVisibility(View.GONE);
        mCreditHistory.setVisibility(View.GONE);
        mSearch.setVisibility(View.GONE);
    }


    private void makeViewsVisible() {
        mNoData.setVisibility(View.GONE);
        mTotalAmtBottomHolder.setVisibility(View.VISIBLE);
        mTableTitles.setVisibility(View.VISIBLE);
        mTotalAmtTop.setVisibility(View.VISIBLE);
        mCreditHistory.setVisibility(View.VISIBLE);
        mSearch.setVisibility(View.VISIBLE);
    }


    private void calculateTotal(List<CreditDto> filteredList) {
        totalAmount = 0;
        for (CreditDto creditDto : filteredList
        ) {
            totalAmount += Double.valueOf(creditDto.getBalance());
        }

        StringBuilder totalDuesTop = new StringBuilder();
        totalDuesTop.append("Total Dues:  ");
        totalDuesTop.append("Rs. ");
        totalDuesTop.append(new DecimalFormat("##.##").format(totalAmount));
        mTotalAmtTop.setText(totalDuesTop);

        StringBuilder totalDuesBottom = new StringBuilder();
        totalDuesBottom.append("Rs. ");
        totalDuesBottom.append(new DecimalFormat("##.##").format(totalAmount));
        mTotalAmtBottom.setText(totalDuesBottom);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearch.getText().clear();
        setUpRecyclerView(creditList);
//        invalidateOptionsMenu();

    }

    private void hideFabWhenScrolled() {

        mCreditHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && mAddCredit.isShown())
                    mAddCredit.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mAddCredit.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void setUpRecyclerView(List<Credit> creditList) {
        makeViewsVisible();
        mCreditHistory.setLayoutManager(new LinearLayoutManager(this));
        if (creditList == null || creditList.isEmpty()) {
            mNoData.setVisibility(View.VISIBLE);
            mSearch.setVisibility(View.GONE);
            mTotalAmtBottomHolder.setVisibility(View.GONE);
            mTableTitles.setVisibility(View.GONE);
            mTotalAmtTop.setVisibility(View.GONE);
        } else {
            mNoData.setVisibility(View.GONE);
            mSearch.setVisibility(View.VISIBLE);
            mTotalAmtBottomHolder.setVisibility(View.VISIBLE);
            mTableTitles.setVisibility(View.VISIBLE);
            mTotalAmtTop.setVisibility(View.VISIBLE);

            List<CreditDto> creditDtoList = mapModelToDto(creditList);
            Collections.sort(creditDtoList, new Comparator<CreditDto>() {
                @Override
                public int compare(CreditDto o1, CreditDto o2) {
                    return Double.valueOf(o2.getBalance()).compareTo(Double.valueOf(o1.getBalance()));
                }
            });

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
            calculateTotal(creditDtoList);
        }

    }

    private List<CreditDto> mapModelToDto(List<Credit> creditList) {
        List<CreditDto> creditDtoList = new ArrayList<>();
        for (Credit credit : creditList
        ) {
            CreditDto creditDto = new CreditDto();
            creditDto.setId(credit.getId());
            creditDto.setCreatedAt(credit.getCreatedAt());
            creditDto.setCreditorId(credit.getCreditorId());
            creditDto.setBalance(credit.getBalance());
            creditDto.setPaidAmount(credit.getPaidAmount());
            creditDto.setSoldItems(credit.getSoldItems());
            creditDto.setSync(credit.isSync());
            creditDto.setTotalAmount(credit.getTotalAmount());
            creditDto.setUpdatedAt(credit.getUpdatedAt());
            creditDto.setUserId(credit.getUserId());
            creditDto.setCreditorSignature(credit.getCreditorSignature());

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

        mSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearch.setFocusableInTouchMode(true);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_date, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Calendar myCalendar = Calendar.getInstance();

        switch (item.getItemId()) {
            case R.id.action_date:

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        filterListByDate(dayOfMonth, month + 1, year);
                    }
                };


                new DatePickerDialog(CreditHistory.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            /*    if (NetworkUtils.isNetworkConnected(this)) {
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
                }*/
                return true;

            case R.id.action_all:
                setUpRecyclerView(creditList);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filterListByDate(int dayOfMonth, int month, int year) {
        List<Credit> filterByDate = new ArrayList<>();
        for (Credit credit : creditList
        ) {
            String date = getDateSimple(credit.getCreatedAt());
            String[] separated = date.split("-");
            int getYear = Integer.valueOf(separated[0]);
            int getMonth = Integer.valueOf(separated[1]);
            int getDay = Integer.valueOf(separated[2]);

            AppUtils.showLog(TAG, "getYear: " + getYear);
            AppUtils.showLog(TAG, "getMonth: " + getMonth);
            AppUtils.showLog(TAG, "getDay: " + getDay);

            AppUtils.showLog(TAG, "Year: " + year);
            AppUtils.showLog(TAG, "Month: " + month);
            AppUtils.showLog(TAG, "Day: " + dayOfMonth);


            if (getYear == year && getMonth == month && getDay == dayOfMonth) {
                filterByDate.add(credit);
            }

        }

        if (!filterByDate.isEmpty()) {
            setUpRecyclerView(filterByDate);
        } else {
            makeViewsInVisible();
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
