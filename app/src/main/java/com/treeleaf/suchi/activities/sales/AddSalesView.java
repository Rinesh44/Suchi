package com.treeleaf.suchi.activities.sales;

public interface AddSalesView {
    void addSalesSuccess();

    void addSalesFail(String msg);

    void getSalesFail(String msg);

    void getSalesSuccess();
}
