package com.treeleaf.suchi.activities.report.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.InventoryStocksRepo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    @BindView(R.id.ll_highest_alltime_holder)
    LinearLayout mHighestAllTimeHolder;
    @BindView(R.id.hsv_highest_alltime)
    HorizontalScrollView mHighestAlltimeScrollview;
    @BindView(R.id.hsv_not_sold)
    HorizontalScrollView mNotSoldItemsScrollview;
    @BindView(R.id.ll_not_sold_holder)
    LinearLayout mNotSoldItemsHolder;
    @BindView(R.id.line_chart)
    LineChart lineChart;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.tv_xaxis)
    TextView mXaxix;
    @BindView(R.id.tv_yaxis)
    TextView mYaxix;
    @BindView(R.id.rl_stats)
    RelativeLayout mStatsTitleHolder;
    @BindView(R.id.tv_status)
    TextView mStatus;
    @BindView(R.id.ll_high_low)
    LinearLayout mHighLow;
    @BindView(R.id.rl_chart_text)
    RelativeLayout mChartTextHolder;
    @BindView(R.id.tv_chart_text)
    TextView mChartText;
    @BindView(R.id.tv_status2)
    TextView mStatus2;
    @BindView(R.id.iv_status)
    ImageView mStatusImage;
    @BindView(R.id.iv_status2)
    ImageView mStatusImage2;


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

        getSalesGrowthRate();
        setOverallHighestSoldItems();
        setItemsNotSold();

        mFilterByTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = group.findViewById(checkedId);

                //highlight selected button and disable unselected
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    rb.setBackground(getResources().getDrawable(R.drawable.round_bg_disabled));
                }

                selectedRadioButton.setBackground(getResources().getDrawable(R.drawable.round_bg));

                switch (checkedId) {
                    case R.id.btn_today:
                        setVisibilityToViews(todaysSalesStocks);
                        if (todaysSalesStocks != null && !todaysSalesStocks.isEmpty()) {
                            String uniqueItemsSoldToday = getSoldItemsUnique(todaysSalesStocks);
                            mSoldItemsCount.setText(uniqueItemsSoldToday);

                            setUpLineChart(todaysSalesStocks, "hours");
                            setUpPieChart(todaysSalesStocks);
                            prepareListForStats(todaysSalesStocks);
                        }

                        break;

                    case R.id.btn_week:
                        long[] timeStampsWeek = getWeeksStartAndEnd();
                        List<SalesStock> weeksSalesStock = SalesStockRepo.getInstance().
                                getSalesStockByDate(timeStampsWeek[0], timeStampsWeek[1]);
                        AppUtils.showLog(TAG, "weekSalesSize: " + weeksSalesStock.size());
                        setVisibilityToViews(weeksSalesStock);
                        if (!weeksSalesStock.isEmpty()) {
                            String uniqueItemsSoldThisWeek = getSoldItemsUnique(weeksSalesStock);
                            mSoldItemsCount.setText(uniqueItemsSoldThisWeek);
                            setUpLineChart(weeksSalesStock, "weekdays");
                            setUpPieChart(weeksSalesStock);
                            prepareListForStats(weeksSalesStock);
                        }
                        break;

                    case R.id.btn_month:
                        long[] timeStampsMonth = getMonthsStartAndEnd();
                        List<SalesStock> monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStampsMonth[0], timeStampsMonth[1]);
                        setVisibilityToViews(monthSalesStock);
                        if (!monthSalesStock.isEmpty()) {
                            String uniqueItemsSoldThisMonth = getSoldItemsUnique(monthSalesStock);

                            mSoldItemsCount.setText(uniqueItemsSoldThisMonth);
                            setUpLineChart(monthSalesStock, "monthdays");
                            setUpPieChart(monthSalesStock);
                            prepareListForStats(monthSalesStock);
                        }
                        break;

                    case R.id.btn_3months:
                        long[] timeStamps3Months = getP3MonthsStartAndEnd();
                        List<SalesStock> p3monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps3Months[0], timeStamps3Months[1]);
                        setVisibilityToViews(p3monthSalesStock);
                        if (!p3monthSalesStock.isEmpty()) {
                            String uniqueItemsSoldP3M = getSoldItemsUnique(p3monthSalesStock);

                            mSoldItemsCount.setText(uniqueItemsSoldP3M);
                            setUpLineChart(p3monthSalesStock, "p3months");
                            setUpPieChart(p3monthSalesStock);
                            prepareListForStats(p3monthSalesStock);
                        }
                        break;

                    case R.id.btn_6months:
                        long[] timeStamps6Month = getP6MonthsStartAndEnd();
                        List<SalesStock> p6monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps6Month[0], timeStamps6Month[1]);
                        setVisibilityToViews(p6monthSalesStock);
                        if (!p6monthSalesStock.isEmpty()) {
                            String uniqueItemsSoldP6M = getSoldItemsUnique(p6monthSalesStock);
                            mSoldItemsCount.setText(uniqueItemsSoldP6M);
                            setUpLineChart(p6monthSalesStock, "p3months");
                            setUpPieChart(p6monthSalesStock);
                            prepareListForStats(p6monthSalesStock);
                        }
                        break;
                }

            }
        });

        todaysSalesStocks = SalesStockRepo.getInstance().getSalesStockOfToday();
        setVisibilityToViews(todaysSalesStocks);
        if (todaysSalesStocks != null && !todaysSalesStocks.isEmpty()) {
            mSoldItemsCount.setText(getSoldItemsUnique(todaysSalesStocks));
            AppUtils.showLog(TAG, "chartSize: " + todaysSalesStocks.size());
            setUpLineChart(todaysSalesStocks, "hours");
            setUpPieChart(todaysSalesStocks);
            prepareListForStats(todaysSalesStocks);
        }

    }

    private void setItemsNotSold() {
        mNotSoldItemsHolder.removeAllViews();
        List<SalesStock> allSalesStock = SalesStockRepo.getInstance().getAllSalesStockList();
        List<InventoryStocks> allInventoryStocks = InventoryStocksRepo.getInstance().getAllInventoryStocks();

        List<InventoryStocks> salesStockNotSold = new ArrayList<>();

        HashSet<String> salesHashSet = new HashSet<>();
        HashSet<String> inventoryHashSet = new HashSet<>();

        for (SalesStock salesStock : allSalesStock
        ) {
            salesHashSet.add(salesStock.getId());
        }

        for (InventoryStocks inventoryStocks : allInventoryStocks
        ) {
            inventoryHashSet.add(inventoryStocks.getId());
        }

        AppUtils.showLog(TAG, "salesHashSetSize: " + salesHashSet.size());
        AppUtils.showLog(TAG, "inventoryHashSetSize: " + inventoryHashSet.size());

        inventoryHashSet.removeAll(salesHashSet);

        for (InventoryStocks inventoryStocks : allInventoryStocks
        ) {
            for (String id : inventoryHashSet
            ) {
                if (id.equalsIgnoreCase(inventoryStocks.getId())) {
                    salesStockNotSold.add(inventoryStocks);
                }
            }
        }


   /*     for (SalesStock salesStock : allSalesStock
        ) {
            boolean found = false;
            for (InventoryStocks inventoryStocks : allInventoryStocks
            ) {
                if (salesStock.getId().equalsIgnoreCase(inventoryStocks.getId())) {
                    found = true;
                }
            }

            if (!found) salesStockNotSold.add(salesStock);
        }*/

        AppUtils.showLog(TAG, "itemsNotSoldSize: " + salesStockNotSold.size());

        if (salesStockNotSold.isEmpty()) {

            TextView textView = new TextView(getActivity());
            textView.setText("None");
            textView.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);

            mNotSoldItemsHolder.addView(textView);
            return;
        }

        //flag to display arrow when more than one items are present
        boolean showArrow = true;
        //show highest sold items in list
        for (InventoryStocks stocks : salesStockNotSold
        ) {
            RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.single_item, null);
            CircleImageView itemImage = view.findViewById(R.id.iv_item);
            TextView itemName = view.findViewById(R.id.tv_sold);
            TextView itemCost = view.findViewById(R.id.tv_item_cost);
            TextView itemQty = view.findViewById(R.id.tv_qty_sold);
            TextView more = view.findViewById(R.id.tv_more);

            if (salesStockNotSold.size() > 1) {
                if (showArrow) more.setVisibility(View.VISIBLE);
                else more.setVisibility(View.GONE);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 500);
                view.setLayoutParams(layoutParams);
                view.setPadding(10, 0, 10, 0);
            }

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mNotSoldItemsScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            });

            AppUtils.showLog(TAG, "inventoryId: " + stocks.getInventory_id());
            Inventory inventory = InventoryRepo.getInstance().getInventoryById(stocks.getInventory_id());

            if (inventory != null) {
                String imageUrl = inventory.getSku().getPhoto_url();
                if (!imageUrl.isEmpty()) {
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.ic_stock)
                            .error(R.drawable.ic_stock);

                    Glide.with(this).load(imageUrl).apply(options).into(itemImage);
                }

                itemName.setText(inventory.getSku().getName());

                StringBuilder notSoldItemCostBuilder = new StringBuilder();
                notSoldItemCostBuilder.append("Rs. ");
                notSoldItemCostBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(inventory.getSku().getUnitPrice())));
                itemCost.setText(notSoldItemCostBuilder);

                itemQty.setVisibility(View.GONE);
   /*         StringBuilder highestSoldItemQtyBuilder = new StringBuilder();
            highestSoldItemQtyBuilder.append("Quantity sold: ");
            highestSoldItemQtyBuilder.append(salesStock.getQuantity());
            itemQty.setText(highestSoldItemQtyBuilder);*/
                mNotSoldItemsHolder.addView(view);
                mNotSoldItemsScrollview.setFillViewport(true);

                showArrow = false;
            }

        }
    }

    private void getSalesGrowthRate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long yesterdayFromDate = cal.getTimeInMillis();


        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long yesterdayTillDate = cal.getTimeInMillis();

        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long dayBeforeFromDate = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long dayBeforeTillDate = cal.getTimeInMillis();

        AppUtils.showLog(TAG, "yesterday: " + yesterdayFromDate + " " + yesterdayTillDate);
        AppUtils.showLog(TAG, "daybefore: " + dayBeforeFromDate + " " + dayBeforeTillDate);

        List<SalesStock> yesterdaysSalesStocks = SalesStockRepo.getInstance().
                getSalesStockByDate(yesterdayFromDate, yesterdayTillDate);

        List<SalesStock> dayBeforeSalesStocks = SalesStockRepo.getInstance().
                getSalesStockByDate(dayBeforeFromDate, dayBeforeTillDate);

        if (yesterdaysSalesStocks.isEmpty() || dayBeforeSalesStocks.isEmpty()) {
            mStatus.setVisibility(View.GONE);
            mStatusImage.setVisibility(View.GONE);
            mStatus2.setVisibility(View.GONE);
            mStatusImage2.setVisibility(View.GONE);
        } else {

            double yesterdaySalesTotal = 0;
            double dayBeforeSalesTotal = 0;


            for (SalesStock salesstock : yesterdaysSalesStocks
            ) {
                yesterdaySalesTotal += Integer.valueOf(salesstock.getQuantity());
            }

            for (SalesStock salesstock : dayBeforeSalesStocks
            ) {
                dayBeforeSalesTotal += Integer.valueOf(salesstock.getQuantity());
            }

            double diff = yesterdaySalesTotal - dayBeforeSalesTotal;
            AppUtils.showLog(TAG, "diff: " + diff);
            AppUtils.showLog(TAG, "original: " + dayBeforeSalesTotal);
            AppUtils.showLog(TAG, "new: " + yesterdaySalesTotal);

            double growth = diff / dayBeforeSalesTotal * 100;
            mStatus.setText(String.format("%.2f", growth) + "%");
            mStatus2.setText(String.format("%.2f", growth) + "%");

            if (growth > 0) {
                mStatus.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                mStatus2.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_increase);
                img.setBounds(0, 0, 60, 60);
                mStatus.setCompoundDrawables(img, null, null, null);
                mStatus2.setCompoundDrawables(img, null, null, null);
            } else if (growth < 0) {
                mStatus.setTextColor(getContext().getResources().getColor(R.color.red));
                mStatus2.setTextColor(getContext().getResources().getColor(R.color.red));
                Drawable img = getContext().getResources().getDrawable(R.drawable.ic_decrease);
                img.setBounds(0, 0, 60, 60);
                mStatus.setCompoundDrawables(img, null, null, null);
                mStatus2.setCompoundDrawables(img, null, null, null);
            }
        }


    }

    private void setUpPieChart(List<SalesStock> salesList) {

        ArrayList entries = new ArrayList();
        pieChart.getDescription().setEnabled(false);

        for (SalesStock salesStock : salesList
        ) {
            String getTimeHours = java.text.DateFormat.getTimeInstance().format(salesStock.getCreatedAt());
            String[] separatedHours = getTimeHours.split(":");
            String hour = separatedHours[0];
//            AppUtils.showLog(TAG, "hours: " + hour);

            String formatedHour = "";
            if (Integer.valueOf(hour) >= 12) {
                formatedHour = hour + " P.M";
            } else {
                formatedHour = hour + " A.M";
            }

            entries.add(new PieEntry(Float.valueOf(salesStock.getAmount()), formatedHour));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Amounts");

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawEntryLabels(true);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);


        Legend l = pieChart.getLegend();
        l.setEnabled(false);

        dataSet.setSliceSpace(2);
        dataSet.setValueTextColor(getContext().getResources().getColor(R.color.white));
        dataSet.setValueTextSize(12);
        pieChart.animateXY(2000, 2000);

    }


    private void setUpLineChart(List<SalesStock> salesList, String type) {
        lineChart.getDescription().setEnabled(false);
        ArrayList<Entry> entries = new ArrayList<>();


        AppUtils.showLog(TAG, "saleLIstSize: " + salesList.size());
        XAxis xAxis = lineChart.getXAxis();
        switch (type) {
            case "hours":
                //filter list so that each hours amount is added
                HashMap hoursList = new HashMap();
                List<SalesStock> removeList = new ArrayList<>();
                List<SalesStock> addList = new ArrayList<>();
                for (SalesStock salesStock : salesList
                ) {
                    String getTimeHours = java.text.DateFormat.getTimeInstance().format(salesStock.getCreatedAt());
                    String[] separatedHours = getTimeHours.split(":");
                    String hour = separatedHours[0];
                    if (hoursList.containsKey(Float.valueOf(hour))) {
                        removeList.add(salesStock);

                        float addedAmount = Float.valueOf(salesStock.getAmount()) + (Float) hoursList.get(Float.valueOf(hour));
                        SalesStock newSalesStock = new SalesStock(salesStock.getId(), salesStock.getInventory_id(),
                                String.valueOf(addedAmount), salesStock.getQuantity(), salesStock.getUnit(), salesStock.getName(),
                                salesStock.getPhotoUrl(), salesStock.getUnitPrice(), salesStock.getBrand(),
                                salesStock.getSubBrand(), salesStock.getCategories(), salesStock.getCreatedAt(),
                                salesStock.getUpdatedAt());

                        addList.add(newSalesStock);
                    } else {
                        hoursList.put(Float.valueOf(hour), Float.valueOf(salesStock.getAmount()));
                    }
                }

                //remove and add lists from above
                salesList.removeAll(removeList);
                salesList.addAll(addList);

                //sort list in ascending order by time
                Collections.sort(salesList, new Comparator<SalesStock>() {
                    @Override
                    public int compare(SalesStock o1, SalesStock o2) {
                        return Long.compare(o1.getCreatedAt(), o2.getCreatedAt());
                    }
                });

                //add data to line chart entries
                for (SalesStock sales : salesList
                ) {
                    String getTimeHours = java.text.DateFormat.getTimeInstance().format(sales.getCreatedAt());
                    String[] separatedHours = getTimeHours.split(":");
                    String hour = separatedHours[0];
                    entries.add(new Entry(Float.valueOf(hour), Float.valueOf(sales.getAmount()), "hours"));
                }

                mXaxix.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.hours));
                mChartText.setText("Items sold in hours");

                break;

            case "weekdays":
                List<String> weekDayList = new ArrayList<>();
                for (SalesStock salesStock : salesList
                ) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(salesStock.getCreatedAt());
                    float dayInWeek = c.get(Calendar.DAY_OF_WEEK);
                    weekDayList.add(String.valueOf(dayInWeek));
                    entries.add(new Entry(dayInWeek, Float.valueOf(salesStock.getAmount()), "weekd"));
                    mXaxix.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.days));
                    mChartText.setText("Items sold in weekdays");
                }

                break;

            case "monthdays":
                List<String> monthDays = new ArrayList<>();
                for (SalesStock salesStock : salesList
                ) {
                    Calendar c2 = Calendar.getInstance();
                    c2.setTimeInMillis(salesStock.getCreatedAt());
                    float dayInMonth = c2.get(Calendar.DAY_OF_MONTH);
                    monthDays.add(String.valueOf(dayInMonth));
                    entries.add(new Entry(dayInMonth, Float.valueOf(salesStock.getAmount()), "month"));
                    mXaxix.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.days));
                    mChartText.setText("Items sold in months");
                }

                break;

            case "p3months":
                List<String> p3mList = new ArrayList<>();
                for (SalesStock salesStock : salesList
                ) {
                    Calendar c3 = Calendar.getInstance();
                    c3.setTimeInMillis(salesStock.getCreatedAt());
                    float month = c3.get(Calendar.MONTH) + 1;
                    p3mList.add(String.valueOf(month));
                    entries.add(new Entry(month, Float.valueOf(salesStock.getAmount()), "month"));
                    mXaxix.setText(Objects.requireNonNull(getContext()).getResources().getString(R.string.months));
                    mChartText.setText("Items sold in months");
                }


                break;

           /*     case "p6months":
                    String getTime6Months = java.text.DateFormat.getTimeInstance().format(salesStock.getCreatedAt());
                    String[] separated6Months = getTime6Months.split(":");
                    String month6 = separated6Months[0];
                    AppUtils.showLog(TAG, "hours: " + month6);
                    AppUtils.showLog(TAG, "amounts: " + salesStock.getAmount());

                    entries.add(new Entry(Float.valueOf(month6), Float.valueOf(salesStock.getAmount())));
                    break;*/
        }


        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        //****
        // Controlling X axis

        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1

        //***
        // Controlling right side of y axis
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        LineData data = new LineData(dataSet);
        lineChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();
        // modify the legend ... by default it is on the left
        l.setEnabled(false);

        lineChart.animateX(500);
        lineChart.getXAxis().setSpaceMin(1f);
        //refresh
        lineChart.invalidate();
    }

    private String getSoldItemsUnique(List<SalesStock> todaysSalesStocks) {
        HashSet<String> uniqueItems = new HashSet<>();
        for (SalesStock salesStock : todaysSalesStocks
        ) {
            uniqueItems.add(salesStock.getName());
        }

        return String.valueOf(uniqueItems.size());
    }

    private void setVisibilityToViews(List<SalesStock> salesStockList) {
        if (salesStockList.size() == 0) {
            mNoReports.setVisibility(View.VISIBLE);
            mTop.setVisibility(View.GONE);
            mBottom.setVisibility(View.GONE);
            lineChart.setVisibility(View.GONE);
            pieChart.setVisibility(View.GONE);
            mXaxix.setVisibility(View.GONE);
            mYaxix.setVisibility(View.GONE);
            mStatsTitleHolder.setVisibility(View.GONE);
            mHighestAllTimeHolder.setVisibility(View.GONE);
            mHighLow.setVisibility(View.GONE);
            mChartTextHolder.setVisibility(View.GONE);
        } else {
            mNoReports.setVisibility(View.GONE);
            mTop.setVisibility(View.VISIBLE);
            mBottom.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            mXaxix.setVisibility(View.VISIBLE);
            mYaxix.setVisibility(View.VISIBLE);
            mStatsTitleHolder.setVisibility(View.VISIBLE);
            mHighestAllTimeHolder.setVisibility(View.VISIBLE);
            mHighLow.setVisibility(View.VISIBLE);
            mChartTextHolder.setVisibility(View.VISIBLE);
        }
    }


    public void prepareListForStats(List<SalesStock> salesStockList) {
        AppUtils.showLog(TAG, "salesBefore: " + salesStockList.size());
        List<SalesStock> filteredStockList = new ArrayList<>();
        if (!salesStockList.isEmpty() && salesStockList.size() > 1) {
            //add quantities of same stock and modify the list
            filteredStockList = addSameStocksQuantity(salesStockList);
            AppUtils.showLog(TAG, "salesAfter: " + salesStockList.size());
            for (SalesStock item : filteredStockList
            ) {
//                AppUtils.showLog(TAG, "items:" + item.getQuantity());
            }
        } else {
            filteredStockList = salesStockList;
        }

        getStatsFromList(filteredStockList, salesStockList);
    }


    private void getStatsFromList(List<SalesStock> salesStocksFiltered, List<SalesStock> salesStocksUnfiltered) {
        double total = 0;
        int sum = 0, sum2;
        List<SalesStock> highestSoldItems = new ArrayList<>();
        List<SalesStock> lowestSoldItems = new ArrayList<>();

        AppUtils.showLog(TAG, "FilteredListSize: " + salesStocksFiltered.size());
        //calculate total
        for (SalesStock sales : salesStocksUnfiltered
        ) {
            total += Double.valueOf(sales.getAmount());
        }


        //sum is highest quantity in the list
        for (SalesStock salesStock : salesStocksFiltered
        ) {
            if (Integer.valueOf(salesStock.getQuantity()) > sum) {
                sum = Integer.valueOf(salesStock.getQuantity());
            }
        }

        AppUtils.showLog(TAG, "highestSoldQuantity: " + sum);

        //sum2 - var to find lowest quantity in list
        //add highest and lowest sold items in the list
        sum2 = sum;
        for (SalesStock salesStock : salesStocksFiltered
        ) {
            if (Integer.valueOf(salesStock.getQuantity()) < sum2) {
                sum2 = Integer.valueOf(salesStock.getQuantity());
            }
            if (Integer.valueOf(salesStock.getQuantity()) == sum) {
                highestSoldItems.add(salesStock);
            }
        }

        AppUtils.showLog(TAG, "lowestSoldQuantity: " + sum2);


        for (SalesStock salesStock : salesStocksFiltered
        ) {
            if (Integer.valueOf(salesStock.getQuantity()) == sum2) {
                lowestSoldItems.add(salesStock);
            }
        }

        setStatValues(total, lowestSoldItems, highestSoldItems);

    }

    /**
     * check for repeated stocks, if found add quantities and add to list
     *
     * @param list
     * @return
     */
    private List<SalesStock> addSameStocksQuantity(List<SalesStock> list) {
        //temp list to prevent concurrent modification
        LinkedHashSet<SalesStock> addList = new LinkedHashSet<>();
        LinkedHashSet<SalesStock> removeList = new LinkedHashSet<>();
        //new list to preserve data of original list
        List<SalesStock> newList = new ArrayList<>(list);


        for (SalesStock salesStock1 : newList
        ) {
            AppUtils.showLog(TAG, "eachId: " + salesStock1.getId());
            List<SalesStock> subList = newList.subList(newList.indexOf(salesStock1) + 1, newList.size());
            AppUtils.showLog(TAG, "originallistSize: " + newList.size());
            AppUtils.showLog(TAG, "subLIstSize: " + subList.size());
            for (SalesStock salesStock2 : subList
            ) {
                if (salesStock1.getId().equalsIgnoreCase(salesStock2.getId())) {
                    AppUtils.showLog(TAG, "id matched");
                    removeList.add(salesStock2);
                    removeList.add(salesStock1);
                    int quantitySum = Integer.valueOf(salesStock1.getQuantity()) +
                            Integer.valueOf(salesStock2.getQuantity());


                    SalesStock modifiedStock = new SalesStock(salesStock1.getId(), salesStock1.getInventory_id(),
                            salesStock1.getAmount(), String.valueOf(quantitySum), salesStock1.getUnit(),
                            salesStock1.getName(), salesStock1.getPhotoUrl(), salesStock1.getUnitPrice(),
                            salesStock1.getBrand(), salesStock1.getSubBrand(), salesStock1.getCategories(),
                            salesStock1.getCreatedAt(), salesStock1.getUpdatedAt());

                    addList.add(modifiedStock);
                }
            }

        }

        AppUtils.showLog(TAG, "removeListCount: " + removeList.size());
        AppUtils.showLog(TAG, "addLIstCount: " + addList.size());
        newList.removeAll(removeList);
        newList.addAll(addList);
        return newList;
    }

    // Function to remove duplicates from an ArrayList
    public static List<SalesStock> removeDuplicates(List<SalesStock> list) {

        List<SalesStock> salesStocksToRemove = new ArrayList<>();

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId().equals(list.get(j).getId())) {
                    salesStocksToRemove.add(list.get(i));
                }
            }
        }

        list.removeAll(salesStocksToRemove);

        return list;

    }


    private void setStatValues(double total, List<SalesStock>
            lowestSoldItems, List<SalesStock> highestSoldItems) {

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
                view.setPadding(10, 0, 10, 0);

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
                view.setPadding(10, 0, 10, 0);

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

    private void setOverallHighestSoldItems() {
        mHighestAllTimeHolder.removeAllViews();
        List<SalesStock> allSalesStocks = SalesStockRepo.getInstance().getAllSalesStockList();
        List<Integer> quantityCollection = new ArrayList<>();
        List<SalesStock> filteredSalesList = addSameStocksQuantity(allSalesStocks);
        if (!filteredSalesList.isEmpty()) {
            for (SalesStock salesStock : filteredSalesList
            ) {
                quantityCollection.add(Integer.valueOf(salesStock.getQuantity()));
            }

            int mostSoldQty = Collections.max(quantityCollection);
            AppUtils.showLog(TAG, "mostSoldqty: " + mostSoldQty);

            List<SalesStock> highestSoldStockList = new ArrayList<>();

            for (SalesStock salesStock : filteredSalesList
            ) {
                if (Integer.valueOf(salesStock.getQuantity()) == mostSoldQty) {
                    highestSoldStockList.add(salesStock);
                }
            }

            removeDuplicates(highestSoldStockList);
            AppUtils.showLog(TAG, "alltimehighestSize:  " + highestSoldStockList.size());


            //flag to display arrow when more than one items are present
            boolean showArrow = true;
            //show highest sold items in list
            for (SalesStock salesStock : highestSoldStockList
            ) {
                RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.single_item, null);
                CircleImageView itemImage = view.findViewById(R.id.iv_item);
                TextView itemName = view.findViewById(R.id.tv_sold);
                TextView itemCost = view.findViewById(R.id.tv_item_cost);
                TextView itemQty = view.findViewById(R.id.tv_qty_sold);
                TextView more = view.findViewById(R.id.tv_more);

                if (highestSoldStockList.size() > 1) {
                    if (showArrow) more.setVisibility(View.VISIBLE);
                    else more.setVisibility(View.GONE);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 500);
                    view.setLayoutParams(layoutParams);
                }

                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mHighestAlltimeScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
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

                itemCost.setVisibility(View.GONE);
            /*    StringBuilder highestSoldItemCostBuilder = new StringBuilder();
                highestSoldItemCostBuilder.append("Rs. ");
                highestSoldItemCostBuilder.append(new DecimalFormat("##.##").format(Double.valueOf(salesStock.getAmount())));
                itemCost.setText(highestSoldItemCostBuilder);*/

                StringBuilder highestSoldItemQtyBuilder = new StringBuilder();
                highestSoldItemQtyBuilder.append("Quantity sold: ");
                highestSoldItemQtyBuilder.append(salesStock.getQuantity());
                itemQty.setText(highestSoldItemQtyBuilder);


                mHighestAllTimeHolder.addView(view);
                mHighestAlltimeScrollview.setFillViewport(true);

                showArrow = false;

            }
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


}
