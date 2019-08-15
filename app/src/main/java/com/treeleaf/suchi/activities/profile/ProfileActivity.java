package com.treeleaf.suchi.activities.profile;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.realm.models.User;
import com.treeleaf.suchi.realm.repo.UserRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_username)
    TextView mUsername;
    @BindView(R.id.tv_store_name)
    TextView mStorename;
    @BindView(R.id.tv_address)
    TextView mAddress;
    @BindView(R.id.fab_change_pic)
    FloatingActionButton mChangePic;
    @BindView(R.id.iv_profile_image)
    CircleImageView mProfilePic;
    @BindView(R.id.tv_owner_name)
    TextView mOwnerName;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        initialize();

        setProfileCredentials();
        setProfilePicture();
        mChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAllowFlipping(true)
                        .setAllowRotation(true)
                        .setRequestedSize(1024, 768, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(ProfileActivity.this);
            }
        });

    }

    private void setProfilePicture() {
        String encodedProfileImage = preferences.getString(Constants.PROFILE_IMAGE, "");
        if (!encodedProfileImage.isEmpty()) {
            Bitmap bitmap = decodeBase64(encodedProfileImage);
            mProfilePic.setImageBitmap(bitmap);
        }
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mToolbarTitle.setText("Profile");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setProfileCredentials() {
        User user = UserRepo.getInstance().getToken().getUser();

        mUsername.setText(user.getUserName());
        mStorename.setText(user.getStoreName());
        mAddress.setText(user.getAddress());
        mOwnerName.setText(user.getOwnerName());
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    mProfilePic.setImageBitmap(bitmap);

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

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        saveImage(encoded);
    }

    private void saveImage(String encoded) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PROFILE_IMAGE, encoded);
        editor.apply();
    }
}
