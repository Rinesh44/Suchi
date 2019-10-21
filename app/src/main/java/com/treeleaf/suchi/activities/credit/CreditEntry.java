package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.adapter.AutocompleteCreditorAdapter;
import com.treeleaf.suchi.dto.CreditDto;
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

import java.io.ByteArrayOutputStream;
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
    private static final int PICKFILE_RESULT_CODE = 1;
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
    @BindView(R.id.il_amount_type)
    TextInputLayout mAmountTypeLayout;
    @BindView(R.id.et_amount_type)
    TextInputEditText mAmountType;
    @BindView(R.id.btn_add_to_credit)
    MaterialButton mAddToCredit;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.signature_pad)
    SignaturePad mSignaturePad;
    @BindView(R.id.btn_show_creditor_history)
    Button mCreditorHistory;
    @BindView(R.id.btn_add_sign)
    Button mAddSign;
    @BindView(R.id.rl_sign)
    RelativeLayout mSignField;
    @BindView(R.id.btn_add_attachments)
    MaterialButton mAttachments;
    @BindView(R.id.ll_attachments)
    LinearLayout mAttachmentsHolder;

    private double totalAmount = 0;
    private List<SalesStock> salesStockList = new RealmList<>();
    private List<Creditors> mAllCreditors;
    private CreditorsDto selectedCreditor;
    private SharedPreferences preferences;
    private String creditId;
    private String creditorSignEncoded;
    private Credit selectedCredit;
    private double amountDiff;
    private double paidAmount = 0;
    private boolean enableSign = false;
    private Credit existingCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_entry);

        ButterKnife.bind(this);

        initialize();
        mCreateNewCreditor.setOnClickListener(this);
        mAddToCredit.setOnClickListener(this);
        mAddSign.setOnClickListener(this);
        mCreditorHistory.setOnClickListener(this);
        mAttachments.setOnClickListener(this);

        mAmountType.setEnabled(false);

        getBillFromIntent();
        inflateListInBillHolder();
        setUpCreditorSearch();

        mAmountType.setText(String.valueOf(new DecimalFormat("#.##").format(totalAmount)));
        mCreditorName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboard();
                selectedCreditor = (CreditorsDto) adapterView.getItemAtPosition(i);

                selectedCredit = CreditRepo.getInstance().getCreditByCreditorId(selectedCreditor.getId());
                if (selectedCredit != null && !selectedCredit.getBalance().isEmpty())
                    mCreditorHistory.setVisibility(View.VISIBLE);
                else mCreditorHistory.setVisibility(View.GONE);

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
                if (!charSequence.toString().isEmpty() && charSequence.length() > 1) {
                    paidAmount = Double.valueOf(charSequence.toString());
                    amountDiff = paidAmount - totalAmount;
                    AppUtils.showLog(TAG, "amountDiff: " + amountDiff);
                    if (amountDiff >= 0) {
                        mAmountType.setText(String.valueOf(amountDiff));
                        mAmountType.setTextColor(getResources().getColor(R.color.green1));
                        mAmountTypeLayout.setHint("Balance");
                    } else {
                        mAmountType.setText(String.valueOf(new DecimalFormat("#.##").
                                format(Math.abs(amountDiff))));
                        mAmountType.setTextColor(getResources().getColor(R.color.red));
                        mAmountTypeLayout.setHint("Due Amount");
                    }
                    AppUtils.showLog(TAG, "paidAmt: " + paidAmount);
                    AppUtils.showLog(TAG, "amtDiff: " + amountDiff);
                } else {
                    paidAmount = 0;
                    amountDiff = paidAmount - totalAmount;
                    mAmountType.setText(String.valueOf(new DecimalFormat("#.##").format(totalAmount)));

                    AppUtils.showLog(TAG, "paidAmt: " + paidAmount);
                    AppUtils.showLog(TAG, "amtDiff: " + amountDiff);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            mAttachmentsHolder.setVisibility(View.VISIBLE);
            String filePath = Objects.requireNonNull(data.getData()).getPath();
            MaterialCardView attchment = (MaterialCardView) getLayoutInflater().inflate(R.layout.layout_attachment, null);
            TextView fileName = (TextView) attchment.findViewById(R.id.tv_filename);
            ImageView remove = (ImageView) attchment.findViewById(R.id.iv_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAttachmentsHolder.removeView(attchment);
                    if (mAttachmentsHolder.getChildCount() == 0)
                        mAttachmentsHolder.setVisibility(View.GONE);
                }
            });
            fileName.setText(filePath);
            mAttachmentsHolder.addView(attchment);
        }
    }


    private CreditDto mapCreditToDto(Credit selectedCredit) {
        CreditDto creditDto = new CreditDto();
        creditDto.setId(selectedCredit.getId());
        creditDto.setCreditorId(selectedCredit.getCreditorId());
        creditDto.setPaidAmount(selectedCredit.getPaidAmount());
        creditDto.setBalance(selectedCredit.getBalance());
        creditDto.setTotalAmount(selectedCredit.getTotalAmount());
        creditDto.setUserId(selectedCredit.getUserId());
        creditDto.setCreditorSignature(selectedCredit.getCreditorSignature());
        creditDto.setCreatedAt(selectedCredit.getCreatedAt());
        creditDto.setUpdatedAt(selectedCredit.getUpdatedAt());
        creditDto.setSync(selectedCredit.isSync());
        creditDto.setSoldItems(selectedCredit.getSoldItems());

        return creditDto;
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

                if (enableSign) {
                    Bitmap signatureBitmap = mSignaturePad.getTransparentSignatureBitmap();
                    encodeBitmapToBase64(signatureBitmap);
                }

                saveCreditToDb();
                break;

            case R.id.btn_show_creditor_history:
                Intent i = new Intent(CreditEntry.this, CreditDetails.class);
                CreditDto creditDto = mapCreditToDto(selectedCredit);
                i.putExtra("credit_info", creditDto);
                startActivity(i);
                break;

            case R.id.btn_add_sign:
                enableSign = !enableSign;
                if (enableSign) {
                    mSignField.setVisibility(View.VISIBLE);
                    mAddSign.setText("Remove creditor's signature");
                } else {
                    mAddSign.setText("Add creditors's signature");
                    mSignField.setVisibility(View.GONE);
                    creditorSignEncoded = "";
                }
                break;

            case R.id.btn_add_attachments:
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
                break;

            default:
                break;
        }
    }

    private void saveCreditToDb() {
        RealmList<SalesStock> realmStockList = new RealmList<>();
        realmStockList.addAll(salesStockList);

        if (mPaidAmount.getText().toString().isEmpty()) {
            paidAmount = 0;
            amountDiff = paidAmount - totalAmount;
        }

        boolean creditorExists = false;

        List<Credit> existingCreditList = CreditRepo.getInstance().getAllCredits();
        for (Credit credit : existingCreditList
        ) {
                if(credit.getCreditorId().equals(selectedCreditor.getId())) {
                    creditorExists = true;
                    existingCredit = credit;
                }
        }

        Credit credit = new Credit();

        if(!creditorExists) {
            creditId = UUID.randomUUID().toString().replace("-", "");

            credit.setId(creditId);
            credit.setCreditorId(selectedCreditor.getId());
            if (paidAmount == 0) credit.setPaidAmount(Objects.requireNonNull("N/A"));
            else credit.setPaidAmount(Objects.requireNonNull(String.valueOf(paidAmount)));
            credit.setBalance(Objects.requireNonNull(String.valueOf(amountDiff)));
            credit.setTotalAmount(String.valueOf(totalAmount));
            credit.setUserId(preferences.getString(Constants.USER_ID, ""));
            credit.setCreatedAt(System.currentTimeMillis());
            credit.setUpdatedAt(0);
            credit.setSoldItems(realmStockList);
            if (creditorSignEncoded != null && !creditorSignEncoded.isEmpty())
                credit.setCreditorSignature(creditorSignEncoded);

        } else {
            realmStockList.addAll(existingCredit.getSoldItems());

            credit.setId(existingCredit.getId());
            credit.setBalance(String.valueOf(Double.valueOf(existingCredit.getBalance()) + amountDiff));
            credit.setCreditorSignature(existingCredit.getCreditorSignature());
            credit.setCreatedAt(existingCredit.getCreatedAt());
            credit.setUpdatedAt(System.currentTimeMillis());
            credit.setCreditorId(existingCredit.getCreditorId());
            credit.setPaidAmount(String.valueOf(Double.valueOf(existingCredit.getPaidAmount()) + paidAmount));
            credit.setSoldItems(realmStockList);
            credit.setSync(existingCredit.isSync());
            credit.setUserId(existingCredit.getUserId());
            credit.setTotalAmount(String.valueOf(Double.valueOf(existingCredit.getTotalAmount())) + totalAmount);
        }

        saveSalesData();

        CreditRepo.getInstance().saveCredit(credit, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "credit saved to db");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.CREDIT_DATA_REMAINING_TO_SYNC, true);
                editor.apply();

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
            price.setText(new DecimalFormat("#.##").format(Double.valueOf(salesStock.getUnitPrice())));
            amount.setText(new DecimalFormat("#.##").format(Double.valueOf(salesStock.getAmount())));

            totalAmount += Double.valueOf(salesStock.getAmount());
            mBillHolder.addView(view);

            StringBuilder totalAmountBuilder = new StringBuilder();
            totalAmountBuilder.append("Rs. ");
            totalAmountBuilder.append(new DecimalFormat("#.##").format(totalAmount));
            mTotalAmount.setText(totalAmountBuilder);

        }
    }

    private void encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        creditorSignEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
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

             /*       SalesStock salesStock = new SalesStock(salesStockCurrent.getId(), salesStockCurrent.getInventory_id(),
                            amount, quantity, salesStockCurrent.getUnit(), salesStockCurrent.getName(),
                            salesStockCurrent.getPhotoUrl(), salesStockCurrent.getUnitPrice(),
                            salesStockCurrent.getBrand(), salesStockCurrent.getSubBrand(), salesStockCurrent.getCategories(),
                            false, salesStockCurrent.getCreatedAt(), System.currentTimeMillis(), true, creditId);*/

//                    stocksToAdd.add(salesStock);
                } else {
                    AppUtils.showLog(TAG, "id not matched" + salesStockCurrent.getName());
              /*      SalesStock salesStock = new SalesStock(salesStockCurrent.getId(), salesStockCurrent.getInventory_id(),
                            salesStockCurrent.getAmount(), salesStockCurrent.getQuantity(), salesStockCurrent.getUnit(), salesStockCurrent.getName(),
                            salesStockCurrent.getPhotoUrl(), salesStockCurrent.getUnitPrice(),
                            salesStockCurrent.getBrand(), salesStockCurrent.getSubBrand(), salesStockCurrent.getCategories(),
                            false, System.currentTimeMillis(), 0, true, creditId);*/

//                    stocksToAdd.add(salesStock);
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
