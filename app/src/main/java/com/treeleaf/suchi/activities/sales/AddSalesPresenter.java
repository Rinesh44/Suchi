package com.treeleaf.suchi.activities.sales;

import com.treeleaf.suchi.entities.SuchiProto;

public interface AddSalesPresenter {

    void addSales(String token, SuchiProto.Sale sale);

    void getSales(String token);
}
