package com.treeleaf.suchi.activities.dashboard;

import com.treeleaf.suchi.entities.SuchiProto;

public interface DashboardPresenter {

    void logout(String token);

    void syncSalesData(String token, SuchiProto.SyncRequest syncRequest);
}
