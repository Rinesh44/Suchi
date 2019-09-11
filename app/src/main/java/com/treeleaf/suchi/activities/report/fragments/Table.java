package com.treeleaf.suchi.activities.report.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.treeleaf.suchi.R;
import com.treeleaf.suchi.activities.sales.SalesActivity;
import com.treeleaf.suchi.activities.sales.SalesDetailsActivity;
import com.treeleaf.suchi.adapter.SalesAdapter;
import com.treeleaf.suchi.dto.SalesStockDto;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
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
    @BindView(R.id.rv_table_report)
    RecyclerView mTableReport;
    @BindView(R.id.tv_no_reports)
    TextView mNoReports;


    private Unbinder unbinder;
    private SalesAdapter mSalesAdapter;
    private SalesListViewModel salesListViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Table() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Table.
     */
    // TODO: Rename and change types and number of parameters
    public static Table newInstance(String param1, String param2) {
        Table fragment = new Table();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTableReport.setLayoutManager(new LinearLayoutManager(getActivity()));

        manageReportRecyclerView();

    }

    private void manageReportRecyclerView() {
        List<SalesStock> salesStockList = SalesStockRepo.getInstance().getAllSalesStockList();
        if (salesStockList.size() == 0) {
            mNoReports.setVisibility(View.VISIBLE);
            //todo add seach and set visibility to gone
        } else {
            //todo add seach and set visibility to visible
            mNoReports.setVisibility(View.GONE);
            List<SalesStockDto> salesStockDtoList = mapSaleStocksToSalesStockDto(salesStockList);
            mSalesAdapter = new SalesAdapter(getActivity(), salesStockDtoList);
            mTableReport.setAdapter(mSalesAdapter);
            mSalesAdapter.notifyDataSetChanged();

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
            salesStockDto.setSynced(salesStock.isSynced());
            salesStockDto.setUnit(salesStock.getUnit());
            salesStockDto.setUnitPrice(salesStock.getUnitPrice());

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
