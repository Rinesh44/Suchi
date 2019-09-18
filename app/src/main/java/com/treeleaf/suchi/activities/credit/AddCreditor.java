package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.repo.CreditorRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddCreditor extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AddCreditor";
    @BindView(R.id.il_name)
    TextInputLayout mNameLayout;
    @BindView(R.id.et_name)
    TextInputEditText mName;
    @BindView(R.id.il_address)
    TextInputLayout mAddressLayout;
    @BindView(R.id.et_address)
    TextInputEditText mAddress;
    @BindView(R.id.il_phone)
    TextInputLayout mPhoneLayout;
    @BindView(R.id.et_phone)
    TextInputEditText mPhone;
    @BindView(R.id.il_desc)
    TextInputLayout mDescLayout;
    @BindView(R.id.et_desc)
    TextInputEditText mDescription;
    @BindView(R.id.btn_done)
    MaterialButton mDone;
    @BindView(R.id.fab_change_pic)
    FloatingActionButton mChangePic;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.iv_creditor_image)
    CircleImageView mCreditorImage;

    private String encodedImage;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_creditor);

        ButterKnife.bind(this);

        initialize();

        mDone.setOnClickListener(this);
        mChangePic.setOnClickListener(this);
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText("Add Creditor");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

//        getMyApplication(this).getAppComponent().inject(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_done:
                validateFileds();
                break;

            case R.id.fab_change_pic:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAllowFlipping(true)
                        .setAllowRotation(true)
                        .setRequestedSize(1024, 768, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(AddCreditor.this);
                break;
        }
    }

    private void validateFileds() {
        String fullname = Objects.requireNonNull(mName.getText()).toString().trim();
        String address = Objects.requireNonNull(mAddress.getText()).toString().trim();
        String phone = Objects.requireNonNull(mPhone.getText()).toString().trim();
        String desc = Objects.requireNonNull(mDescription.getText()).toString().trim();

        if (fullname.isEmpty()) {
            mNameLayout.setErrorEnabled(true);
            mNameLayout.setError("This field is required");
            mName.requestFocus();
        } else {
            mNameLayout.setErrorEnabled(false);
        }


        if (address.isEmpty()) {
            mAddressLayout.setErrorEnabled(true);
            mAddressLayout.setError("This field is required");
            mAddress.requestFocus();
        } else {
            mAddressLayout.setErrorEnabled(false);
        }


        if (phone.isEmpty()) {
            mPhoneLayout.setErrorEnabled(true);
            mPhoneLayout.setError("This field is required");
            mPhone.requestFocus();
        } else {
            mPhoneLayout.setErrorEnabled(false);
        }

        saveToDB(fullname, address, phone, desc);

    }


    public void saveToDB(String name, String address, String phone, String desc) {
        String id = UUID.randomUUID().toString().replace("-", "");
        Creditors creditors = new Creditors();
        creditors.setId(id);
        creditors.setName(name);
        creditors.setAddress(address);
        creditors.setPhone(phone);
        creditors.setDescription(desc);
        if (encodedImage != null) creditors.setPic(encodedImage);
        creditors.setUserId(preferences.getString(Constants.USER_ID, ""));
        creditors.setCreatedAt(System.currentTimeMillis());
        creditors.setUpdatedAt(0);
        creditors.setSync(false);

        CreditorRepo.getInstance().saveCreditor(creditors, new Repo.Callback() {
            @Override
            public void success(Object o) {
                Toast.makeText(AddCreditor.this, "Creditor added", Toast.LENGTH_SHORT).show();
                AppUtils.showLog(TAG, "creditor saved to db");
                finish();
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save creditor to db");
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

    private void encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

    }
}
