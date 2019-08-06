package com.treeleaf.suchi.activities.dashboard;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.ReqResProto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardPresenterImpl implements DashboardPresenter {
    private Endpoints endpoints;
    private DashboardActivity activity;

    public DashboardPresenterImpl(Endpoints endpoints, DashboardActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void logout(String token) {
        endpoints.logout(token).enqueue(new Callback<ReqResProto.Response>() {
            @Override
            public void onResponse(Call<ReqResProto.Response> call, Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                activity.hideLoading();
                if (baseResponse == null) {
                    activity.logoutFail("Logout failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.logoutFail(baseResponse.getMsg());
                    return;
                }

                activity.logoutSuccess();
            }

            @Override
            public void onFailure(Call<ReqResProto.Response> call, Throwable t) {
                activity.hideLoading();
                activity.logoutFail("Logout failed");
            }
        });
    }
}
