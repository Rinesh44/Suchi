package com.treeleaf.suchi.activities.Settings;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
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
    @BindView(R.id.textView)
    TextView mConversionText;

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

        mLanguages.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Context context;
                Resources resources;

                int englishLangId = mEnglish.getId();
                int nepaliLangId = mNepali.getId();

                if (i == englishLangId) {
                    Toast.makeText(Settings.this, "english", Toast.LENGTH_SHORT).show();
                    context = LocaleHelper.setLocale(getApplicationContext(), "en");
                    resources = context.getResources();
                    mConversionText.setText(resources.getString(R.string.translate_this_to_nepali));
                } else if (i == nepaliLangId) {

                    Toast.makeText(Settings.this, "nepali", Toast.LENGTH_SHORT).show();

                    context = LocaleHelper.setLocale(getApplicationContext(), "ne");
                    resources = context.getResources();
                    mConversionText.setText(resources.getString(R.string.translate_this_to_nepali));
                }

            }
        });


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
        mToolbarTitle.setText("Settings");
    }
}
