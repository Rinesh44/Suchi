package com.treeleaf.suchi.activities.dashboard;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardPresenterImpl implements DashboardPresenter {
    private static final String TAG = "DashboardPresenterImpl";
    private Endpoints endpoints;
    private DashboardActivity activity;

    public DashboardPresenterImpl(Endpoints endpoints, DashboardActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }


    @Override
    public void syncSalesData(String token, SuchiProto.SyncRequest syncRequest) {
        endpoints.addSales(token, syncRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                activity.hideLoading();
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.syncFailed("sales sync response null");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.syncFailed(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "salesSyncResponse: " + baseResponse.getSalesList());
                activity.syncSuccess();

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.syncFailed("Sync sales failed");
            }
        }));
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
