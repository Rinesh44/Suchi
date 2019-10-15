package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.dto.CreditorsDto;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreditorDetails extends BaseActivity {
    @BindView(R.id.iv_creditor_image)
    CircleImageView mCreditorImage;
    @BindView(R.id.tv_creditor_name)
    TextView mCreditorName;
    @BindView(R.id.tv_creditor_address)
    TextView mCreditorAddress;
    @BindView(R.id.tv_creditor_number)
    TextView mCreditorNumber;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditor_details);

        ButterKnife.bind(this);

        initialize();

        setUpCreditorInfo();

    }

    private void setUpCreditorInfo() {
        Intent i = getIntent();
        CreditorsDto creditorsDto = i.getParcelableExtra("creditor_info");

        if (creditorsDto.getPic() != null) {
            Bitmap creditorImage = decodeBase64(creditorsDto.getPic());
            if (creditorImage != null) {
                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.ic_user_proto)
                        .error(R.drawable.ic_user_proto);

                Glide.with(this).load(creditorImage).apply(options).into(mCreditorImage);
            }
        } else {
            mCreditorImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_proto));
        }

        mCreditorName.setText(creditorsDto.getName());
        mCreditorAddress.setText(creditorsDto.getAddress());
        mCreditorNumber.setText(creditorsDto.getPhone());

    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.creditor_details));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
