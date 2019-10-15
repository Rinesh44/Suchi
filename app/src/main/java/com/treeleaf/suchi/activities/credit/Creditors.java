package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.CreditorsAdapter;
import com.treeleaf.suchi.dto.CreditorsDto;
import com.treeleaf.suchi.realm.repo.CreditorRepo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Creditors extends BaseActivity {
    public static final String TAG = "Creditors";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.et_search)
    EditText mSearch;
    @BindView(R.id.rv_creditors)
    RecyclerView mCreditors;
    @BindView(R.id.tv_no_data)
    TextView mNoData;

    private CreditorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditors);

        ButterKnife.bind(this);
        initialize();

        List<com.treeleaf.suchi.realm.models.Creditors> creditorsList = CreditorRepo.getInstance().getAllCreditors();
        setUpRecyclerView(creditorsList);
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mToolbarTitle.setText(getResources().getString(R.string.creditors));

        mSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mSearch.setFocusableInTouchMode(true);
                return false;
            }
        });

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setUpRecyclerView(List<com.treeleaf.suchi.realm.models.Creditors> creditorsList) {
        mCreditors.setLayoutManager(new LinearLayoutManager(this));
        if (creditorsList == null || creditorsList.isEmpty()) {
            mNoData.setVisibility(View.VISIBLE);
            mSearch.setVisibility(View.GONE);
            mCreditors.setVisibility(View.GONE);
        } else {
            mNoData.setVisibility(View.GONE);
            mSearch.setVisibility(View.VISIBLE);
            mCreditors.setVisibility(View.VISIBLE);

            List<CreditorsDto> creditDtoList = mapModelToDto(creditorsList);
            adapter = new CreditorsAdapter(creditDtoList, this);
            adapter.setOnItemClickListener(new CreditorsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(CreditorsDto creditorDto) {
                    Intent i = new Intent(Creditors.this, CreditorDetails.class);
                    i.putExtra("creditor_info", creditorDto);
                    startActivity(i);
                }
            });
            mCreditors.setAdapter(adapter);
        }

    }

    private List<CreditorsDto> mapModelToDto(List<com.treeleaf.suchi.realm.models.Creditors> creditorsList) {
        List<CreditorsDto> creditorDtoList = new ArrayList<>();
        for (com.treeleaf.suchi.realm.models.Creditors creditors : creditorsList) {
            CreditorsDto creditorsDto = new CreditorsDto();
            creditorsDto.setId(creditors.getId());
            creditorsDto.setAddress(creditors.getAddress());
            creditorsDto.setCreatedAt(creditors.getCreatedAt());
            creditorsDto.setDescription(creditors.getDescription());
            creditorsDto.setName(creditors.getName());
            creditorsDto.setPhone(creditors.getPhone());
            creditorsDto.setPic(creditors.getPic());
            creditorsDto.setSync(creditors.isSync());
            creditorsDto.setUpdatedAt(creditors.getUpdatedAt());
            creditorsDto.setUserId(creditors.getUserId());

            creditorDtoList.add(creditorsDto);
        }

        return creditorDtoList;
    }


}
