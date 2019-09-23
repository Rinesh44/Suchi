package com.treeleaf.suchi.activities.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.utils.Constants;
import com.treeleaf.suchi.utils.LocaleHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Settings extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rg_language)
    RadioGroup mLanguages;
    @BindView(R.id.rb_english)
    RadioButton mEnglish;
    @BindView(R.id.rb_nepali)
    RadioButton mNepali;
    @BindView(R.id.tv_language_setting_title)
    TextView mLanguageSettingTitle;
    @BindView(R.id.cb_allow_notification)
    AppCompatCheckBox mAllowNotification;
    @BindView(R.id.tv_notification_text)
    TextView mNotificationText;
    @BindView(R.id.sp_stock_threshold)
    AppCompatSpinner mStockThreshold;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        initialize();

        if (LocaleHelper.getLanguage(Settings.this).equalsIgnoreCase("ne")) {
            mNepali.setChecked(true);
        } else {
            mEnglish.setChecked(true);
        }

        setUpStockThreshold();

        mAllowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mNotificationText.setEnabled(true);
                    mStockThreshold.setEnabled(true);

                    editor.putBoolean(Constants.NOTIFICATION, true);
                    editor.apply();


                    mStockThreshold.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String selectedItem = (String) adapterView.getSelectedItem();
                            editor.putString(Constants.STOCK_THRESHOLD_NOTIFICATION, selectedItem);
                            editor.apply();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    mNotificationText.setEnabled(false);
                    mStockThreshold.setEnabled(false);

                    editor.putBoolean(Constants.NOTIFICATION, false);
                    editor.apply();
                }
            }
        });

        mLanguages.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Context context;
                Resources resources;

                int englishLangId = mEnglish.getId();
                int nepaliLangId = mNepali.getId();

                if (i == englishLangId) {
                    Toast.makeText(Settings.this, "english", Toast.LENGTH_SHORT).show();
                    context = LocaleHelper.setLocale(Settings.this, "en");
                    resources = context.getResources();
                    mLanguageSettingTitle.setText(resources.getString(R.string.language_settings));
                    mToolbarTitle.setText(resources.getString(R.string.settings));

                } else if (i == nepaliLangId) {

                    Toast.makeText(Settings.this, "nepali", Toast.LENGTH_SHORT).show();

                    context = LocaleHelper.setLocale(Settings.this, "ne");
                    resources = context.getResources();
                    mLanguageSettingTitle.setText(resources.getString(R.string.language_settings));
                    mToolbarTitle.setText(resources.getString(R.string.settings));
                }

            }
        });


    }

    private void setUpStockThreshold() {
        String[] threshold = new String[]{"5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> thresholdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, threshold);
        mStockThreshold.setAdapter(thresholdAdapter);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.settings));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        mStockThreshold.setEnabled(false);
        mNotificationText.setEnabled(false);
    }
}
