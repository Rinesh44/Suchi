package com.treeleaf.suchi.activities.sales;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class SalesPresenterImpl implements SalesPresenter {
    private static final String TAG = "SalesPresenterImpl";
    private Endpoints endpoints;
    private SalesActivity activity;

    public SalesPresenterImpl(Endpoints endpoints, SalesActivity activity) {
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
                    activity.sycnSalesDataFail("sales sync response null");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.sycnSalesDataFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "SyncSalesResponse: " + baseResponse.toString());
                activity.syncSalesDataSuccess();

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.sycnSalesDataFail("Sync sales failed");
            }
        }));
    }
}
