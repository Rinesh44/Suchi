package com.treeleaf.suchi.activities.report.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class Chart extends Fragment {
    private static final String TAG = "Chart";
    @BindView(R.id.tv_no_reports)
    TextView mNoReports;
    @BindView(R.id.hsv_filter)
    HorizontalScrollView mFilter;
    @BindView(R.id.rg_filter)
    RadioGroup mFilterByTime;
    @BindView(R.id.tv_highest_sold)
    TextView mHighestSoldItem;
    @BindView(R.id.tv_lowest_sold)
    TextView mLowestSold;
    @BindView(R.id.tv_total_amount)
    TextView mTotalAmount;
    @BindView(R.id.tv_sold_items)
    TextView mSoldItemsCount;

    private Unbinder unbinder;


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
                for (int i = 0; i < count; i++) {git
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    rb.setBackground(getResources().getDrawable(R.drawable.round_bg_disabled));
                }

                selectedRadioButton.setBackground(getResources().getDrawable(R.drawable.round_bg));

                switch (checkedId) {
                    case R.id.btn_today:
                        List<SalesStock> todaysSalesStocks = SalesStockRepo.getInstance().getSalesStockOfToday();
                        mSoldItemsCount.setText(String.valueOf(todaysSalesStocks.size()));
                        getStatsFromList(todaysSalesStocks);
                        break;

                    case R.id.btn_week:
                        long timeStampsWeek[] = getWeeksStartAndEnd();
                        List<SalesStock> weeksSalesStock = SalesStockRepo.getInstance().
                                getSalesStockByDate(timeStampsWeek[0], timeStampsWeek[1]);
                        break;

                    case R.id.btn_month:
                        long timeStampsMonth[] = getMonthsStartAndEnd();
                        List<SalesStock> monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStampsMonth[0], timeStampsMonth[1]);
                        break;

                    case R.id.btn_3months:
                        long timeStamps3Months[] = getP3MonthsStartAndEnd();
                        List<SalesStock> p3monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps3Months[0], timeStamps3Months[1]);
                        break;

                    case R.id.btn_6months:
                        long timeStamps6Month[] = getP6MonthsStartAndEnd();
                        List<SalesStock> p6monthSalesStock = SalesStockRepo.getInstance()
                                .getSalesStockByDate(timeStamps6Month[0], timeStamps6Month[1]);
                        break;
                }

            }
        });
    }

    private void getStatsFromList(List<SalesStock> todaysSalesStocks) {
        double total = 0;
        int sum = 0, sum2 = 0;
        List<SalesStock> highestSoldItems = new ArrayList<>();
        List<SalesStock> lowestSoldItems = new ArrayList<>();
        for (SalesStock salesStock : todaysSalesStocks
        ) {
            total += Double.valueOf(salesStock.getAmount());
            if (Integer.valueOf(salesStock.getQuantity()) > sum) {
                sum = Integer.valueOf(salesStock.getQuantity());
            }

        }

        sum2 = sum;
        for (SalesStock salesStock : todaysSalesStocks
        ) {

            if (Integer.valueOf(salesStock.getQuantity()) < sum2) {
                sum2 = Integer.valueOf(salesStock.getQuantity());

            }
            if (Integer.valueOf(salesStock.getQuantity()) == sum) {
                highestSoldItems.add(salesStock);
            }
        }

        for (SalesStock salesStock : todaysSalesStocks
        ) {
            if (Integer.valueOf(salesStock.getQuantity()) == sum2) {
                lowestSoldItems.add(salesStock);
            }
        }

        setStatValues(total, lowestSoldItems, highestSoldItems);

    }

    private void setStatValues(double total, List<SalesStock> lowestSoldItems, List<SalesStock> highestSoldItems) {
        StringBuilder totalAmountBuilder = new StringBuilder();
        totalAmountBuilder.append("Rs. ");
        totalAmountBuilder.append(total);
        mTotalAmount.setText(totalAmountBuilder);

        for (SalesStock salesStock : lowestSoldItems
        ) {
            StringBuilder lowestSoldItemsGroup = new StringBuilder();
//            lowestSoldItemsGroup.append()
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
