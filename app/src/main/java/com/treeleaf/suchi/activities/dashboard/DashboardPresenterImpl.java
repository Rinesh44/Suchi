package com.treeleaf.suchi.activities.dashboard;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.rpc.SuchiRpcProto;

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
        endpoints.logout(token).enqueue(new Callback<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onResponse(Call<SuchiRpcProto.SuchiBaseResponse> call, Response<SuchiRpcProto.SuchiBaseResponse> response) {
                activity.hideLoading();
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

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
            public void onFailure(Call<SuchiRpcProto.SuchiBaseResponse> call, Throwable t) {
                activity.hideLoading();
                activity.logoutFail("Logout failed");
            }
        });
    }
}
