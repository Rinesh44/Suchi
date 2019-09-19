package com.treeleaf.suchi.activities.credit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.base.BaseActivity;
import com.treeleaf.suchi.adapter.CreditHistoryAdapter;
import com.treeleaf.suchi.dto.CreditDto;
import com.treeleaf.suchi.realm.models.Credit;
import com.treeleaf.suchi.realm.repo.CreditRepo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CreditHistory extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rv_credit_history)
    RecyclerView mCreditHistory;
    @BindView(R.id.tv_no_data)
    TextView mNoData;


    private CreditHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_history);

        ButterKnife.bind(this);

        initialize();

        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mCreditHistory.setLayoutManager(new LinearLayoutManager(this));
        List<Credit> creditList = CreditRepo.getInstance().getCreditsWithDues();
        if (creditList == null || creditList.isEmpty()) mNoData.setVisibility(View.VISIBLE);
        List<CreditDto> creditDtoList = mapModelToDto(creditList);
        adapter = new CreditHistoryAdapter(this, creditDtoList);
        adapter.setOnItemClickListener(new CreditHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CreditDto creditDto) {
                Intent i = new Intent(CreditHistory.this, CreditDetails.class);
                i.putExtra("credit_info", creditDto);
                startActivity(i);
            }
        });
        mCreditHistory.setAdapter(adapter);
    }

    private List<CreditDto> mapModelToDto(List<Credit> creditList) {
        List<CreditDto> creditDtoList = new ArrayList<>();
        for (Credit credit : creditList
        ) {
            CreditDto creditDto = new CreditDto();
            creditDto.setId(credit.getId());
            creditDto.setCreatedAt(credit.getCreatedAt());
            creditDto.setCreditorId(credit.getCreditorId());
            creditDto.setDueAmount(credit.getDueAmount());
            creditDto.setPaidAmount(credit.getPaidAmount());
            creditDto.setSoldItems(credit.getSoldItems());
            creditDto.setSync(credit.isSync());
            creditDto.setTotalAmount(credit.getTotalAmount());
            creditDto.setUpdatedAt(credit.getUpdatedAt());
            creditDto.setUserId(credit.getUserId());

            creditDtoList.add(creditDto);

        }

        return creditDtoList;
    }

    private void initialize() {
        setUpToolbar(mToolbar);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbarTitle.setText(getResources().getString(R.string.credit_history));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
