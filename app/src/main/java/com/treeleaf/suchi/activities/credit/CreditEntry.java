package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.adapter.AutocompleteCreditorAdapter;
import com.treeleaf.suchi.dto.CreditorsDto;
import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.CreditRepo;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class CreditEntry extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "CreditEntry";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.ll_bill_holder)
    LinearLayout mBillHolder;
    @BindView(R.id.actv_creditor)
    AutoCompleteTextView mCreditorName;
    @BindView(R.id.btn_create_new_creditor)
    MaterialButton mCreateNewCreditor;
    @BindView(R.id.il_paid_amount)
    TextInputLayout mPaidAmountLayout;
    @BindView(R.id.et_paid_amount)
    TextInputEditText mPaidAmount;
    @BindView(R.id.il_due_amount)
    TextInputLayout mDueAmountLayout;
    @BindView(R.id.et_due_amount)
    TextInputEditText mDueAmount;
    @BindView(R.id.btn_add_to_credit)
    MaterialButton mAddToCredit;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;

    private double totalAmount = 0;
    private List<SalesStock> salesStockList = new RealmList<>();
    private List<Creditors> mAllCreditors;
    private CreditorsDto selectedCreditor;
    private SharedPreferences preferences;
    private String creditId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_entry);


        ButterKnife.bind(this);

        initialize();
        mCreateNewCreditor.setOnClickListener(this);
        mAddToCredit.setOnClickListener(this);
        mDueAmount.setEnabled(false);

        getBillFromIntent();
        inflateListInBillHolder();
        setUpCreditorSearch();

        mDueAmount.setText(String.valueOf(new DecimalFormat("##.##").format(totalAmount)));
        mCreditorName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
                selectedCreditor = (CreditorsDto) adapterView.getItemAtPosition(i);
                AppUtils.showLog(TAG, "selectedname: " + selectedCreditor.getName());
                mCreditorName.setText(selectedCreditor.getName());
            }
        });


        mPaidAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    double paidAmount = Double.valueOf(charSequence.toString());
                    double amountDiff = totalAmount - paidAmount;
                    if (amountDiff >= 0) {
                        mDueAmount.setText(String.valueOf(amountDiff));
                        mPaidAmountLayout.setErrorEnabled(false);
                    } else {
                        mPaidAmountLayout.setErrorEnabled(true);
                        mPaidAmountLayout.setError("Paid amount must be less than total");
                    }
                } else {
                    mDueAmount.setText(String.valueOf(new DecimalFormat("##.##").format(totalAmount)));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getBillFromIntent() {
        Intent i = getIntent();
        salesStockList = i.getParcelableArrayListExtra("sales_stock_list");
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.credit_entry));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCreditorSearch();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_new_creditor:
                startActivity(new Intent(CreditEntry.this, AddCreditor.class));
                break;

            case R.id.btn_add_to_credit:
                if (mCreditorName.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter creditor", Toast.LENGTH_SHORT).show();
                    return;
                }


                saveCreditToDb();

                break;
        }
    }

    private void saveCreditToDb() {
        RealmList<SalesStock> realmStockList = new RealmList<>();
        realmStockList.addAll(salesStockList);
        creditId = UUID.randomUUID().toString().replace("-", "");
        Credit credit = new Credit();
        credit.setId(creditId);
        credit.setCreditorId(selectedCreditor.getId());
        credit.setPaidAmount(Objects.requireNonNull(mPaidAmount.getText()).toString());
        credit.setDueAmount(Objects.requireNonNull(mDueAmount.getText()).toString());
        credit.setTotalAmount(mTotalAmount.getText().toString());
        credit.setUserId(preferences.getString(Constants.USER_ID, ""));
        credit.setCreatedAt(System.currentTimeMillis());
        credit.setUpdatedAt(0);
        credit.setSoldItems(realmStockList);

        saveSalesData();

        CreditRepo.getInstance().saveCredit(credit, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "credit saved to db");
                Intent i = new Intent(CreditEntry.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save credit to db");
            }
        });
    }

    private void inflateListInBillHolder() {
        for (SalesStock salesStock : salesStockList
        ) {
            View view = getLayoutInflater().inflate(R.layout.bill_row, null);
            TextView itemName = view.findViewById(R.id.tv_item_name);
            TextView qty = view.findViewById(R.id.tv_qty);
            TextView price = view.findViewById(R.id.tv_price);
            TextView amount = view.findViewById(R.id.tv_amount);

            itemName.setText(salesStock.getName());

            StringBuilder quantityHolder = new StringBuilder();
            quantityHolder.append(salesStock.getQuantity());
            quantityHolder.append(" ");
            quantityHolder.append(salesStock.getUnit());

            qty.setText(quantityHolder);
            price.setText(salesStock.getUnitPrice());
            amount.setText(salesStock.getAmount());

            totalAmount += Double.valueOf(salesStock.getAmount());
            mBillHolder.addView(view);

            StringBuilder totalAmountBuilder = new StringBuilder();
            totalAmountBuilder.append("Rs. ");
            totalAmountBuilder.append(new DecimalFormat("##.##").format(totalAmount));
            mTotalAmount.setText(totalAmountBuilder);

        }
    }

    private void setUpCreditorSearch() {
        mAllCreditors = CreditorRepo.getInstance().getAllCreditors();
        List<CreditorsDto> creditorsDtoList = new ArrayList<>();
        creditorsDtoList = mapCreditorModelToDto(mAllCreditors);

        AutocompleteCreditorAdapter creditorListAdapter = new AutocompleteCreditorAdapter(this, R.layout.autocomplete_search_item2, creditorsDtoList);

        mCreditorName.setAdapter(creditorListAdapter);
    }

    private List<CreditorsDto> mapCreditorModelToDto(List<Creditors> mAllCreditors) {
        List<CreditorsDto> creditorsDtoList = new ArrayList<>();
        for (Creditors creditor : mAllCreditors
        ) {
            CreditorsDto creditorsDto = new CreditorsDto();
            creditorsDto.setId(creditor.getId());
            creditorsDto.setName(creditor.getName());
            creditorsDto.setAddress(creditor.getAddress());
            creditorsDto.setPhone(creditor.getPhone());
            creditorsDto.setDescription(creditor.getDescription());
            creditorsDto.setPic(creditor.getPic());
            creditorsDto.setUserId(creditor.getUserId());
            creditorsDto.setCreatedAt(creditor.getCreatedAt());
            creditorsDto.setUpdatedAt(creditor.getUpdatedAt());
            creditorsDto.setSync(creditor.isSync());

            creditorsDtoList.add(creditorsDto);
        }

        return creditorsDtoList;
    }

    public void saveSalesData() {
        List<SalesStock> allSalesStock = SalesStockRepo.getInstance().getAllSalesStockList();
        //lists to prevent concurrent modification exception
        List<SalesStock> stocksToRemove = new ArrayList<>();
        List<SalesStock> stocksToAdd = new ArrayList<>();
        for (SalesStock salesStockCurrent : salesStockList
        ) {
            for (SalesStock salesStockDb : allSalesStock
            ) {
                if (salesStockDb.getId().equalsIgnoreCase(salesStockCurrent.getId())) {
                    AppUtils.showLog(TAG, "id matched: " + salesStockCurrent.getName());
                    stocksToRemove.add(salesStockCurrent);

                    String quantity = String.valueOf(Double.valueOf(salesStockDb.getQuantity()) +
                            Double.valueOf(salesStockCurrent.getQuantity()));

                    String amount = String.valueOf(Double.valueOf(quantity) *
                            Double.valueOf(salesStockCurrent.getUnitPrice()));

                    SalesStock salesStock = new SalesStock(salesStockCurrent.getId(), salesStockCurrent.getInventory_id(),
                            amount, quantity, salesStockCurrent.getUnit(), salesStockCurrent.getName(),
                            salesStockCurrent.getPhotoUrl(), salesStockCurrent.getUnitPrice(),
                            salesStockCurrent.getBrand(), salesStockCurrent.getSubBrand(), salesStockCurrent.getCategories(),
                            false, salesStockCurrent.getCreatedAt(), System.currentTimeMillis(), true, creditId);

                    stocksToAdd.add(salesStock);
                } else {
                    AppUtils.showLog(TAG, "id not matched" + salesStockCurrent.getName());
                    SalesStock salesStock = new SalesStock(salesStockCurrent.getId(), salesStockCurrent.getInventory_id(),
                            salesStockCurrent.getAmount(), salesStockCurrent.getQuantity(), salesStockCurrent.getUnit(), salesStockCurrent.getName(),
                            salesStockCurrent.getPhotoUrl(), salesStockCurrent.getUnitPrice(),
                            salesStockCurrent.getBrand(), salesStockCurrent.getSubBrand(), salesStockCurrent.getCategories(),
                            false, System.currentTimeMillis(), 0, true, creditId);

                    stocksToAdd.add(salesStock);
                }
            }
        }

        salesStockList.removeAll(stocksToRemove);
        salesStockList.addAll(stocksToAdd);

        SalesStockRepo.getInstance().saveSalesStockList(salesStockList, new Repo.Callback() {
            @Override
            public void success(Object o) {
                Toast.makeText(CreditEntry.this, "Sale item added", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.SALES_DATA_REMAINING_TO_SYNC, true);
                editor.apply();
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save sale item");
            }
        });
    }
}
