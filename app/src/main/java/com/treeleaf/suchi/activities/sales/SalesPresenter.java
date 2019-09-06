package com.treeleaf.suchi.activities.sales;

import com.treeleaf.suchi.entities.SuchiProto;

public interface SalesPresenter {

    void syncSalesData(String token, SuchiProto.SyncRequest syncRequest);
}
