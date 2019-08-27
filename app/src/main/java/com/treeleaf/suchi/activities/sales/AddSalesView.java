package com.treeleaf.suchi.activities.sales;


import com.treeleaf.suchi.realm.models.Sales;

import java.util.List;

public interface AddSalesView {
    void addSalesSuccess();

    void addSalesFail(String msg);

    void getSalesFail(String msg);

    void getSalesSuccess(List<Sales> salesList);
}
