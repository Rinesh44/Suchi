package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.dto.CreditorsDto;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.utils.AppUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreditorDetails extends BaseActivity {
    private static final String TAG = "CreditorDetails";
    @BindView(R.id.iv_creditor_image)
    CircleImageView mCreditorImage;
    @BindView(R.id.tv_creditor_name)
    EditText mCreditorName;
    @BindView(R.id.tv_creditor_address)
    EditText mCreditorAddress;
    @BindView(R.id.tv_creditor_number)
    EditText mCreditorNumber;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.fab_done)
    FloatingActionButton mDone;
    @BindView(R.id.tv_change)
    TextView mChangePic;

    private CreditorsDto creditorsDto;
    private String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditor_details);

        ButterKnife.bind(this);

        initialize();

        setUpCreditorInfo();

        mChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAllowFlipping(true)
                        .setAllowRotation(true)
                        .setRequestedSize(1024, 768, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(CreditorDetails.this);
            }
        });



        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Creditors creditor = new Creditors();
                creditor.setId(creditorsDto.getId());
                creditor.setUserId(creditorsDto.getUserId());
                creditor.setUpdatedAt(System.currentTimeMillis());
                creditor.setSync(false);
                if(!encodedImage.isEmpty()) creditor.setPic(encodedImage);
                else creditor.setPic(creditorsDto.getPic());
                creditor.setPhone(creditorsDto.getPhone());
                creditor.setName(mCreditorName.getText().toString().trim());
                creditor.setPhone(mCreditorNumber.getText().toString().trim());
                creditor.setDescription(creditorsDto.getDescription());
                creditor.setAddress(mCreditorAddress.getText().toString().trim());
                creditor.setCreatedAt(creditorsDto.getCreatedAt());

                CreditorRepo.getInstance().saveCreditor(creditor, new Repo.Callback() {
                    @Override
                    public void success(Object o) {
                        AppUtils.showLog(TAG, "edit successful");
                        Toast.makeText(CreditorDetails.this, "Successfully edited", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void fail() {
                        Toast.makeText(CreditorDetails.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        AppUtils.showLog(TAG, "edit failed");
                    }
                });
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    mCreditorImage.setImageBitmap(bitmap);

                    encodeBitmapToBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                AppUtils.showLog(TAG, "error:  " + error);
            }
        }
    }


    private void setUpCreditorInfo() {
        Intent i = getIntent();
        creditorsDto = i.getParcelableExtra("edit_creditor");

        if (creditorsDto != null) {
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

    private void encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }
}
