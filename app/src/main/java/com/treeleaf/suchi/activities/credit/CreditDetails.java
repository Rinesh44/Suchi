package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.dto.CreditDto;
import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.CreditRepo;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.utils.AppUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreditDetails extends BaseActivity {
    private static final String TAG = "CreditDetails";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_due_amount)
    TextView mDueAmount;
    @BindView(R.id.tv_paid_amount)
    TextView mPaidAmount;
    @BindView(R.id.iv_creditor)
    CircleImageView mCreditor;
    @BindView(R.id.tv_name)
    TextView mName;
    @BindView(R.id.tv_address)
    TextView mAddress;
    @BindView(R.id.tv_phone)
    TextView mPhone;
    @BindView(R.id.ll_bill_holder)
    LinearLayout mBillHolder;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.btn_clear_dues)
    MaterialButton mClearDues;
    @BindView(R.id.tv_added_at)
    TextView mAddedAt;
    @BindView(R.id.tv_due_amount_title)
    TextView mDueAmountTitle;
    @BindView(R.id.iv_creditor_sign)
    ImageView mCreditorSign;

    private CreditDto credit;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_details);

        ButterKnife.bind(this);
        initialize();

        Intent i = getIntent();
        credit = i.getParcelableExtra("credit_info");

        inflateListInBillHolder();
        setCreditData();

        mClearDues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Credit creditNew = new Credit();
                creditNew.setId(credit.getId());
                creditNew.setCreatedAt(credit.getCreatedAt());
                creditNew.setUpdatedAt(System.currentTimeMillis());
                creditNew.setCreditorId(credit.getCreditorId());
                creditNew.setBalance("0");
                creditNew.setPaidAmount(credit.getPaidAmount());
                creditNew.setSoldItems(credit.getSoldItems());
                creditNew.setSync(credit.isSync());
                creditNew.setTotalAmount(credit.getTotalAmount());
                creditNew.setUserId(credit.getUserId());
                creditNew.setCreditorSignature(credit.getCreditorSignature());

                CreditRepo.getInstance().saveCredit(creditNew, new Repo.Callback() {
                    @Override
                    public void success(Object o) {
                        Toast.makeText(CreditDetails.this, "Dues cleared", Toast.LENGTH_SHORT).show();
                        finish();
                        AppUtils.showLog(TAG, "credit updated");
                    }

                    @Override
                    public void fail() {
                        AppUtils.showLog(TAG, "failed to update credit");   
                    }
                });

            }
        });

    }

    private void setCreditData() {
        if (!credit.getBalance().equals("0")) {
            if (!new DecimalFormat("#.##").format(Double.valueOf(credit.getPaidAmount())).equals("0")) {
                StringBuilder paidAmountBuilder = new StringBuilder();
                paidAmountBuilder.append("Rs. ");
                paidAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(credit.getPaidAmount())));
                mPaidAmount.setText(paidAmountBuilder);
            } else mPaidAmount.setText("N/A");

            if (Double.valueOf(credit.getBalance()) >= 0) {
                mDueAmountTitle.setText("Balance");
                StringBuilder dueAmountBuilder = new StringBuilder();
                dueAmountBuilder.append("Rs. ");
                dueAmountBuilder.append(new DecimalFormat("#.##").format(Double.valueOf(credit.getBalance())));
                mDueAmount.setText(dueAmountBuilder);
                mDueAmount.setTextColor(getResources().getColor(R.color.green1));
            } else {
                mDueAmountTitle.setText("Due amount");
                StringBuilder dueAmountBuilder = new StringBuilder();
                dueAmountBuilder.append("Rs. ");
                dueAmountBuilder.append(new DecimalFormat("#.##").format(Math.abs(Double.valueOf(credit.getBalance()))));
                mDueAmount.setText(dueAmountBuilder);
                mDueAmount.setTextColor(getResources().getColor(R.color.red));
            }
            StringBuilder dueAmountBuilder = new StringBuilder();
            dueAmountBuilder.append("Rs. ");
            dueAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(credit.getBalance())));
            mDueAmount.setText(dueAmountBuilder);
        } else {
            mDueAmount.setVisibility(View.GONE);
            mDueAmountTitle.setVisibility(View.GONE);
            mClearDues.setVisibility(View.GONE);

            String formattedTotalAmount = credit.getTotalAmount().replace("Rs. ", "");
            StringBuilder paidAmountBuilder = new StringBuilder();
            paidAmountBuilder.append("Rs. ");
            paidAmountBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(formattedTotalAmount)));
            mPaidAmount.setText(paidAmountBuilder);
        }

        Creditors creditors = CreditorRepo.getInstance().getCreditorById(credit.getCreditorId());

        if (creditors.getPic() != null) {
            Bitmap creditorImage = decodeBase64(creditors.getPic());

            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_proto)
                    .error(R.drawable.ic_user_proto);

            Glide.with(CreditDetails.this).load(creditorImage).apply(options).into(mCreditor);
        }

        if (credit.getCreditorSignature() != null) {
            Bitmap creditorSign = decodeBase64(credit.getCreditorSignature());
            RequestOptions options = new RequestOptions()
                    .fitCenter()
                    .placeholder(R.drawable.ic_user_proto)
                    .error(R.drawable.ic_user_proto);

            Glide.with(CreditDetails.this).load(creditorSign).apply(options).into(mCreditorSign);
        } else mCreditorSign.setVisibility(View.GONE);


        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append("Name: ");
        nameBuilder.append(creditors.getName());
        mName.setText(nameBuilder);

        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append("Address: ");
        addressBuilder.append(creditors.getAddress());
        mAddress.setText(addressBuilder);

        StringBuilder phoneBuilder = new StringBuilder();
        phoneBuilder.append("Phone: ");
        phoneBuilder.append(creditors.getPhone());
        mPhone.setText(phoneBuilder);

        StringBuilder addedDateBuilder = new StringBuilder();
        addedDateBuilder.append("Added At: ");
        addedDateBuilder.append(getDate(creditors.getCreatedAt()));
        mAddedAt.setText(addedDateBuilder);

    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.credit_details));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void inflateListInBillHolder() {
        List<SalesStock> salesStockList = credit.getSoldItems();
        for (SalesStock salesStock : salesStockList
        ) {
            View view = getLayoutInflater().inflate(R.layout.bill_row, null);
            TextView itemName = view.findViewById(R.id.tv_item_name);
            TextView qty = view.findViewById(R.id.tv_qty);
            TextView price = view.findViewById(R.id.tv_price);
            TextView amount = view.findViewById(R.id.tv_amount);
            TextView date = view.findViewById(R.id.tv_date);

            itemName.setText(salesStock.getName());

            String dateString = getDateAlternate(salesStock.getCreatedAt());
            date.setText(dateString);

            StringBuilder quantityHolder = new StringBuilder();
            quantityHolder.append(salesStock.getQuantity());
            quantityHolder.append(" ");
            quantityHolder.append(salesStock.getUnit());

            qty.setText(quantityHolder);
            price.setText(new DecimalFormat("#.##").format(Double.valueOf(salesStock.getUnitPrice())));
            amount.setText(new DecimalFormat("#.##").format(Double.valueOf(salesStock.getAmount())));


            AppUtils.showLog(TAG, "sales amount: " + salesStock.getAmount());
            totalAmount += Double.valueOf(salesStock.getAmount());

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);
            view.setLayoutParams(params);
            mBillHolder.addView(view);

            StringBuilder totalAmountBuilder = new StringBuilder();
            totalAmountBuilder.append("Rs. ");
            totalAmountBuilder.append(new DecimalFormat("#.##").format(totalAmount));
            mTotalAmount.setText(totalAmountBuilder);

        }
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
