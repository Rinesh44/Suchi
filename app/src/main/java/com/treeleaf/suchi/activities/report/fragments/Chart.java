package com.treeleaf.suchi.activities.report.fragments;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;


public class Chart extends Fragment {
    private static final String TAG = "Chart";
    @BindView(R.id.tv_no_reports)
    TextView mNoReports;
    @BindView(R.id.hsv_filter)
    HorizontalScrollView mFilter;
    @BindView(R.id.rg_filter)
    RadioGroup mFilterByTime;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.tv_sold_items)
    TextView mSoldItemsCount;
    @BindView(R.id.ll_top)
    LinearLayout mTop;
    @BindView(R.id.ll_bottom)
    LinearLayout mBottom;
    @BindView(R.id.hsv_highest)
    HorizontalScrollView mHighestScrollview;
    @BindView(R.id.hsv_lowest)
    HorizontalScrollView mLowestScrollview;
    @BindView(R.id.ll_highest_holder)
    LinearLayout mHighestSoldItemHolder;
    @BindView(R.id.ll_lowest_holder)
    LinearLayout mLowestSoldItemHolder;

    private Unbinder unbinder;
    private List<SalesStock> todaysSalesStocks;


    public Chart() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                        mSoldItemsCount.setText(String.valueOf(todaysSalesStocks.size()));
                        setVisibilityToViews(todaysSalesStocks);
                        break;

                    case R.id.btn_week:
                        long[] timeStampsWeek = getWeeksStartAndEnd();
                        List<SalesStock> weeksSalesStock = SalesStockRepo.getInstance().
                                getSalesStockByDate(timeStampsWeek[0], timeStampsWeek[1]);
                        mSoldItemsCount.setText(String.valueOf(weeksSalesStock.size()));
                        setVisibilityToViews(weeksSalesStock);
                        break;

                    case R.id.btn_month:
                        long[] timeStampsMonth = getMonthsStartAndEnd();
                        List<SalesStock> monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStampsMonth[0], timeStampsMonth[1]);
                        mSoldItemsCount.setText(String.valueOf(monthSalesStock.size()));
                        setVisibilityToViews(monthSalesStock);
                        break;

                    case R.id.btn_3months:
                        long[] timeStamps3Months = getP3MonthsStartAndEnd();
                        List<SalesStock> p3monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps3Months[0], timeStamps3Months[1]);
                        mSoldItemsCount.setText(String.valueOf(p3monthSalesStock.size()));
                        setVisibilityToViews(p3monthSalesStock);
                        break;

                    case R.id.btn_6months:
                        long[] timeStamps6Month = getP6MonthsStartAndEnd();
                        List<SalesStock> p6monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps6Month[0], timeStamps6Month[1]);
                        mSoldItemsCount.setText(String.valueOf(p6monthSalesStock.size()));
                        setVisibilityToViews(p6monthSalesStock);
                        break;
                }

            }
        });

        todaysSalesStocks = SalesStockRepo.getInstance().getSalesStockOfToday();
        mSoldItemsCount.setText(String.valueOf(todaysSalesStocks.size()));
        if (todaysSalesStocks != null) {
            AppUtils.showLog(TAG, "list is null or empty");
            setVisibilityToViews(todaysSalesStocks);
        }

    }

    private void setVisibilityToViews(List<SalesStock> salesStockList) {
        if (salesStockList.size() == 0) {
            mFilter.setVisibility(View.GONE);
            mNoReports.setVisibility(View.VISIBLE);
            mTop.setVisibility(View.GONE);
            mBottom.setVisibility(View.GONE);
        } else {
            mFilter.setVisibility(View.VISIBLE);
            mNoReports.setVisibility(View.GONE);
            mTop.setVisibility(View.VISIBLE);
            mBottom.setVisibility(View.VISIBLE);

            getStatsFromList(salesStockList);
        }
    }

    private void getStatsFromList(List<SalesStock> salesStocks) {
        double total = 0;
        int sum = 0, sum2;
        List<SalesStock> highestSoldItems = new ArrayList<>();
        List<SalesStock> lowestSoldItems = new ArrayList<>();

        //calculate total
        //sum is highest quantity in the list
        for (SalesStock salesStock : salesStocks
        ) {
            total += Double.valueOf(salesStock.getAmount());
            if (Integer.valueOf(salesStock.getQuantity()) > sum) {
                sum = Integer.valueOf(salesStock.getQuantity());
            }

        }

        //sum2 - var to find lowest quantity in list
        //add highest and lowest sold items in the list
        sum2 = sum;
        for (SalesStock salesStock : salesStocks
        ) {

            if (Integer.valueOf(salesStock.getQuantity()) < sum2) {
                sum2 = Integer.valueOf(salesStock.getQuantity());

            }
            if (Integer.valueOf(salesStock.getQuantity()) == sum) {
                highestSoldItems.add(salesStock);
            }
        }


        for (SalesStock salesStock : salesStocks
        ) {
            if (Integer.valueOf(salesStock.getQuantity()) == sum2) {
                lowestSoldItems.add(salesStock);
            }
        }

        setStatValues(total, lowestSoldItems, highestSoldItems);

    }

    private void setStatValues(double total, List<SalesStock> lowestSoldItems, List<SalesStock> highestSoldItems) {
        //set total
        StringBuilder totalAmountBuilder = new StringBuilder();
        totalAmountBuilder.append("Rs. ");
        totalAmountBuilder.append(new DecimalFormat("##.##").format(total));
        mTotalAmount.setText(totalAmountBuilder);

        mHighestSoldItemHolder.removeAllViews();
        mLowestSoldItemHolder.removeAllViews();


        boolean showMore1 = true;
        boolean showMore2 = true;
        //show highest sold items in list
        for (SalesStock salesStock : highestSoldItems
        ) {
            RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.single_item, null);

            CircleImageView itemImage = view.findViewById(R.id.iv_item);
            TextView itemName = view.findViewById(R.id.tv_sold);
            TextView itemCost = view.findViewById(R.id.tv_item_cost);
            TextView itemQty = view.findViewById(R.id.tv_qty_sold);
            TextView more = view.findViewById(R.id.tv_more);

            if (highestSoldItems.size() > 1) {
                if (showMore1) more.setVisibility(View.VISIBLE);
                else more.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 500);
                view.setLayoutParams(layoutParams);
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHighestScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });

            String imageUrl = salesStock.getPhotoUrl();
            if (!imageUrl.isEmpty()) {
                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.ic_stock)
                        .error(R.drawable.ic_stock);

                Glide.with(this).load(imageUrl).apply(options).into(itemImage);
            }

            itemName.setText(salesStock.getName());

            StringBuilder highestSoldItemCostBuilder = new StringBuilder();
            highestSoldItemCostBuilder.append("Rs. ");
            highestSoldItemCostBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(salesStock.getAmount())));
            itemCost.setText(highestSoldItemCostBuilder);

            StringBuilder highestSoldItemQtyBuilder = new StringBuilder();
            highestSoldItemQtyBuilder.append("Quantity sold: ");
            highestSoldItemQtyBuilder.append(salesStock.getQuantity());
            itemQty.setText(highestSoldItemQtyBuilder);


            mHighestSoldItemHolder.addView(view);
            mHighestScrollview.setFillViewport(true);

            showMore1 = false;

        }

        //show lowest sold items in list
        for (SalesStock salesStock : lowestSoldItems
        ) {

            RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.single_item, null);

            CircleImageView itemImage = view.findViewById(R.id.iv_item);
            TextView itemName = view.findViewById(R.id.tv_sold);
            TextView itemCost = view.findViewById(R.id.tv_item_cost);
            TextView itemQty = view.findViewById(R.id.tv_qty_sold);
            TextView more = view.findViewById(R.id.tv_more);


            if (lowestSoldItems.size() > 1) {
                if (showMore2) more.setVisibility(View.VISIBLE);
                else more.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 500);
                view.setLayoutParams(layoutParams);
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLowestScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });


            String imageUrl = salesStock.getPhotoUrl();

            if (!imageUrl.isEmpty()) {
                RequestOptions options = new RequestOptions()
                        .fitCenter()
                        .placeholder(R.drawable.ic_stock)
                        .error(R.drawable.ic_stock);

                Glide.with(this).load(imageUrl).apply(options).into(itemImage);
            }

            itemName.setText(salesStock.getName());

            StringBuilder lowestSoldItemCostBuilder = new StringBuilder();
            lowestSoldItemCostBuilder.append("Rs. ");
            lowestSoldItemCostBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(salesStock.getAmount())));
            itemCost.setText(lowestSoldItemCostBuilder);

            StringBuilder lowestSoldItemQtyBuilder = new StringBuilder();
            lowestSoldItemQtyBuilder.append("Quantity sold: ");
            lowestSoldItemQtyBuilder.append(salesStock.getQuantity());
            itemQty.setText(lowestSoldItemQtyBuilder);

            mLowestSoldItemHolder.addView(view);
            showMore2 = false;


        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        c.set(Calendar.HOUR_OF_DAY, 6);
        c.set(Calendar.MINUTE, 0);
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

}
