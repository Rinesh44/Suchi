package com.treeleaf.suchi.activities.report.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.report.ReportActivity;
import com.treeleaf.suchi.activities.sales.SalesDetailsActivity;
import com.treeleaf.suchi.adapter.SalesAdapter;
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class Table extends Fragment implements ReportActivity.OnListReceiveListener {
    private static final String TAG = "Table";
    @BindView(R.id.rv_table_report)
    RecyclerView mTableReport;
    @BindView(R.id.tv_no_reports)
    TextView mNoReports;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.tv_items_sold)
    TextView mItemsSold;
    @BindView(R.id.tv_total_amt_top)
    TextView mTotalAmountTop;
    @BindView(R.id.hsv_filter)
    HorizontalScrollView mFilter;
    @BindView(R.id.tv_total_amt_top_title)
    TextView mTotalAmountTopTitle;
    @BindView(R.id.ll_table_titles)
    LinearLayout mTableTitles;
    @BindView(R.id.ll_total_amount_holder)
    LinearLayout mTotalAmountHolder;
    @BindView(R.id.rg_filter)
    RadioGroup mFilterByTime;
    @BindView(R.id.rl_date_holder)
    RelativeLayout mDateHolder;
    @BindView(R.id.tv_from_date)
    TextView mFromDate;
    @BindView(R.id.tv_till_date)
    TextView mTillDate;

    private Unbinder unbinder;
    private SalesAdapter mSalesAdapter;
    private StringBuilder totalAmountBuilder;
    private int totalItemCount = 0;
    List<SalesStock> todaysSalesStocks;

    public Table() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReportActivity mActivity = (ReportActivity) getActivity();
        assert mActivity != null;
        mActivity.setListReceiveListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        unbinder = ButterKnife.bind(this, view);

      /*  if (getArguments() != null) {

        }*/
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTableReport.setLayoutManager(new LinearLayoutManager(getActivity()));

        mFilterByTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = group.findViewById(checkedId);

                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    rb.setBackground(getResources().getDrawable(R.drawable.round_bg_disabled));
                }
                selectedRadioButton.setBackground(getResources().getDrawable(R.drawable.round_bg));

                switch (checkedId) {
                    case R.id.btn_today:
                        manageReportRecyclerView(todaysSalesStocks);
                        break;

                    case R.id.btn_week:
                        long timeStampsWeek[] = getWeeksStartAndEnd();
                        List<SalesStock> weeksSalesStock = SalesStockRepo.getInstance().
                                getSalesStockByDate(timeStampsWeek[0], timeStampsWeek[1]);

                        if (weeksSalesStock != null && !weeksSalesStock.isEmpty()) {
                            manageReportRecyclerView(weeksSalesStock);
                        } else {
                            AppUtils.showLog(TAG, "week sales empty or null");
                        }

                        break;

                    case R.id.btn_month:
                        long timeStampsMonth[] = getMonthsStartAndEnd();
                        List<SalesStock> monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStampsMonth[0], timeStampsMonth[1]);

                        if (monthSalesStock != null && !monthSalesStock.isEmpty())
                            manageReportRecyclerView(monthSalesStock);
                        break;

                    case R.id.btn_3months:
                        long timeStamps3Months[] = getP3MonthsStartAndEnd();
                        List<SalesStock> p3monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps3Months[0], timeStamps3Months[1]);

                        if (p3monthSalesStock != null && !p3monthSalesStock.isEmpty())
                            manageReportRecyclerView(p3monthSalesStock);
                        break;

                    case R.id.btn_6months:
                        long timeStamps6Month[] = getP6MonthsStartAndEnd();
                        List<SalesStock> p6monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps6Month[0], timeStamps6Month[1]);

                        if (p6monthSalesStock != null && !p6monthSalesStock.isEmpty())
                            manageReportRecyclerView(p6monthSalesStock);
                        break;
                }

            }
        });

        todaysSalesStocks = SalesStockRepo.getInstance().getSalesStockOfToday();
        AppUtils.showLog(TAG, "listSizetable: " + todaysSalesStocks.size());
        manageReportRecyclerView(todaysSalesStocks);

    }

    private long[] getWeeksStartAndEnd() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        AppUtils.showLog(TAG, "dayofWeek: " + c.get(Calendar.DAY_OF_WEEK));

        AppUtils.showLog(TAG, "firstDay: " + c.getFirstDayOfWeek());

        Date end = c.getTime();
        c.add(Calendar.DATE, -i);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime();

        AppUtils.showLog(TAG, "startW: " + start);
        AppUtils.showLog(TAG, "endW: " + end);
        return new long[]{start.getTime(), end.getTime()};
    }

    private long[] getMonthsStartAndEnd() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_MONTH) - 1;
        AppUtils.showLog(TAG, "dayOfMonth: " + c.get(Calendar.DAY_OF_MONTH));

        Date end = c.getTime();
        c.add(Calendar.DATE, -i);
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime();

        AppUtils.showLog(TAG, "startM: " + start);
        AppUtils.showLog(TAG, "endM: " + end);
        return new long[]{start.getTime(), end.getTime()};
    }


    private long[] getP3MonthsStartAndEnd() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
//        int i = c.get(Calendar.DAY_OF_MONTH) - 1;
//        AppUtils.showLog(TAG, "dayOfMonth: " + c.get(Calendar.DAY_OF_MONTH));

        Date end = c.getTime();
        c.add(Calendar.DATE, -90);
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime();

        AppUtils.showLog(TAG, "start3: " + start);
        AppUtils.showLog(TAG, "end3: " + end);
        return new long[]{start.getTime(), end.getTime()};
    }


    private long[] getP6MonthsStartAndEnd() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
//        int i = c.get(Calendar.DAY_OF_MONTH) - 1;
//        AppUtils.showLog(TAG, "dayOfMonth: " + c.get(Calendar.DAY_OF_MONTH));

        Date end = c.getTime();
        c.add(Calendar.DATE, -180);
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date start = c.getTime();

        AppUtils.showLog(TAG, "start6: " + start);
        AppUtils.showLog(TAG, "end6: " + end);
        return new long[]{start.getTime(), end.getTime()};
    }


    private void manageReportRecyclerView(List<SalesStock> salesStockList) {

        Collections.sort(salesStockList, new Comparator<SalesStock>() {
            @Override
            public int compare(SalesStock salesStock, SalesStock t1) {
                return Long.compare(t1.getCreatedAt(), salesStock.getCreatedAt());
            }
        });


        totalItemCount = 0;
        HashSet<String> uniqueItems = new HashSet<>();
        for (SalesStock salesStock : salesStockList
        ) {
            uniqueItems.add(salesStock.getName());
        }

        totalItemCount = uniqueItems.size();
        setVisibilityToViews();
        if (salesStockList.size() == 0) {
            mNoReports.setVisibility(View.VISIBLE);
        } else {
            mNoReports.setVisibility(View.GONE);
            List<SalesStockDto> salesStockDtoList = mapSaleStocksToSalesStockDto(salesStockList);
            mSalesAdapter = new SalesAdapter(getActivity(), salesStockDtoList);
            mTableReport.setAdapter(mSalesAdapter);
            mSalesAdapter.notifyDataSetChanged();

            setTotalAmount(salesStockDtoList);


            mSalesAdapter.setOnItemClickListener(new SalesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(SalesStockDto sales) {
                    Intent i = new Intent(getActivity(), SalesDetailsActivity.class);
                    i.putExtra("sales_object", sales);
                    startActivity(i);
                }
            });


        }
    }

    private void setVisibilityToViews() {
        if (totalItemCount <= 0) {
            mItemsSold.setVisibility(View.GONE);
            mTotalAmountTop.setVisibility(View.GONE);
            mTotalAmountTopTitle.setVisibility(View.GONE);
            mTableTitles.setVisibility(View.GONE);
            mTotalAmountHolder.setVisibility(View.GONE);
            mTableReport.setVisibility(View.GONE);
        } else {
            mItemsSold.setVisibility(View.VISIBLE);
            mTotalAmountTop.setVisibility(View.VISIBLE);
            mTotalAmountTopTitle.setVisibility(View.VISIBLE);
            mTableTitles.setVisibility(View.VISIBLE);
            mTotalAmountHolder.setVisibility(View.VISIBLE);
            mTableReport.setVisibility(View.VISIBLE);
        }

    }

    private void setTotalAmount(List<SalesStockDto> salesStockDtoList) {
        double totalAmount = 0;
        for (SalesStockDto salesstockDto : salesStockDtoList
        ) {
            totalAmount += Double.valueOf(salesstockDto.getAmount());
        }

        totalAmountBuilder = new StringBuilder();
        totalAmountBuilder.append("Rs. ");
        totalAmountBuilder.append(new DecimalFormat("##.##").format(totalAmount));
        mTotalAmount.setText(totalAmountBuilder);

        mTotalAmountTop.setText(totalAmountBuilder);

        StringBuilder soldItemsBuilder = new StringBuilder();
        soldItemsBuilder.append("Items sold: ");
        soldItemsBuilder.append(totalItemCount);
        mItemsSold.setText(soldItemsBuilder);
    }

    private List<SalesStockDto> mapSaleStocksToSalesStockDto(List<SalesStock> salesStockList) {
        List<SalesStockDto> salesStockDtoList = new ArrayList<>();
        for (SalesStock salesStock : salesStockList) {
            SalesStockDto salesStockDto = new SalesStockDto();
            salesStockDto.setName(salesStock.getName());
            salesStockDto.setAmount(salesStock.getAmount());
            salesStockDto.setId(salesStock.getId());
            salesStockDto.setInventory_id(salesStock.getInventory_id());
            salesStockDto.setPhotoUrl(salesStock.getPhotoUrl());
            salesStockDto.setQuantity(salesStock.getQuantity());
            salesStockDto.setBrand(salesStock.getBrand());
            salesStockDto.setSubBrand(salesStock.getSubBrand());
            salesStockDto.setCategories(salesStock.getCategories());
//            salesStockDto.setSynced(salesStock.isSynced());
            salesStockDto.setUnit(salesStock.getUnit());
            salesStockDto.setUnitPrice(salesStock.getUnitPrice());
            salesStockDto.setCreatedAt(salesStock.getCreatedAt());
            salesStockDto.setUpdatedAt(salesStock.getUpdatedAt());

            salesStockDtoList.add(salesStockDto);
        }

        return salesStockDtoList;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onListReceive(List<SalesStock> salesStockListByDate, boolean filterByDate, String
            fromDate, String toDate) {
        if (salesStockListByDate != null) {
            AppUtils.showLog(TAG, "salesStockListSize: " + salesStockListByDate.size());
            if (filterByDate) {
                mFilter.setVisibility(View.GONE);
                mDateHolder.setVisibility(View.VISIBLE);
                mFromDate.setText(fromDate);
                mTillDate.setText(toDate);

            } else {
                mFilter.setVisibility(View.VISIBLE);
                mDateHolder.setVisibility(View.GONE);
            }

            manageReportRecyclerView(salesStockListByDate);
        } else {
            totalItemCount = 0;
            setVisibilityToViews();
        }
    }
}
