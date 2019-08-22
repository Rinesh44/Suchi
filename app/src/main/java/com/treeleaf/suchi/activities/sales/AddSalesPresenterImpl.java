package com.treeleaf.suchi.activities.sales;

import com.treeleaf.suchi.api.Endpoints;

public class AddSalesPresenterImpl implements AddSalesPresenter {
    private Endpoints endpoints;
    private AddSalesActivity activity;

    public AddSalesPresenterImpl(Endpoints endpoints, AddSalesActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void addSales(String token) {

    }
}
