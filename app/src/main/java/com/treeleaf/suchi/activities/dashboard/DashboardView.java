package com.treeleaf.suchi.activities.dashboard;

public interface DashboardView {
    void logoutSuccess();

    void logoutFail(String msg);

    void syncSuccess();

    void syncFailed(String msg);
}
