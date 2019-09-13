package com.treeleaf.suchi.activities.report.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.sales.SalesDetailsActivity;
import com.treeleaf.suchi.adapter.SalesAdapter;
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.viewmodel.SalesListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Table.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Table#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Table extends Fragment {
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


    private Unbinder unbinder;
    private SalesAdapter mSalesAdapter;
    private SalesListViewModel salesListViewModel;
    private StringBuilder totalAmountBuilder;
    private int totalItemCount = 0;

    private List<SalesStock> salesStockList;

    private OnFragmentInteractionListener mListener;

    public Table() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Table.
     */
    public static Table newInstance(List<SalesStock> salesStockList) {
        Table fragment = new Table();
        Bundle args = new Bundle();
        args.putParcelableArrayList("salesStockList", (ArrayList<? extends Parcelable>) salesStockList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            salesStockList = getArguments().getParcelableArrayList("salesStockList");
            if (salesStockList != null)
                AppUtils.showLog(TAG, "salesStockListSize: " + salesStockList.size());

            manageReportRecyclerView(salesStockList);
        }


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTableReport.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<SalesStock> salesStockList = SalesStockRepo.getInstance().getAllSalesStockList();
        manageReportRecyclerView(salesStockList);

    }

    private void manageReportRecyclerView(List<SalesStock> salesStockList) {
        totalItemCount = salesStockList.size();
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
            mFilter.setVisibility(View.GONE);
            mItemsSold.setVisibility(View.GONE);
            mTotalAmountTop.setVisibility(View.GONE);
            mTotalAmountTopTitle.setVisibility(View.GONE);
            mTableTitles.setVisibility(View.GONE);
            mTotalAmountHolder.setVisibility(View.GONE);
        } else {
            mFilter.setVisibility(View.VISIBLE);
            mItemsSold.setVisibility(View.VISIBLE);
            mTotalAmountTop.setVisibility(View.VISIBLE);
            mTotalAmountTopTitle.setVisibility(View.VISIBLE);
            mTableTitles.setVisibility(View.VISIBLE);
            mTotalAmountHolder.setVisibility(View.VISIBLE);
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
        totalAmountBuilder.append(totalAmount);
        mTotalAmount.setText(totalAmountBuilder);

        mTotalAmountTop.setText(totalAmountBuilder);

        StringBuilder soldItemsBuilder = new StringBuilder();
        soldItemsBuilder.append(totalItemCount);
        soldItemsBuilder.append(" items sold");
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
            salesStockDto.setSynced(salesStock.isSynced());
            salesStockDto.setUnit(salesStock.getUnit());
            salesStockDto.setUnitPrice(salesStock.getUnitPrice());
            salesStockDto.setCreatedAt(salesStock.getCreatedAt());
            salesStockDto.setUpdatedAt(salesStock.getUpdatedAt());

            salesStockDtoList.add(salesStockDto);
        }

        return salesStockDtoList;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
