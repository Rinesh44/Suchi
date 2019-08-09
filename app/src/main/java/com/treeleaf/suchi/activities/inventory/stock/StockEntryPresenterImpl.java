package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class StockEntryPresenterImpl implements StockEntryPresenter {
    private static final String TAG = "StockEntryPresenterImpl";
    private Endpoints endpoints;
    private StockEntryActivity activity;

    public StockEntryPresenterImpl(Endpoints endpoints, StockEntryActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void makeStockEntry(String token, InventoryProto.StockKeepingUnit stockKeepingUnit) {
        endpoints.addSku(token, stockKeepingUnit).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.stockEntryFail("stock entry failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.stockEntryFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "stockEntryResponse: " + baseResponse);

                activity.stockEntrySuccess();
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.stockEntryFail("stock entry failed");
            }
        }));
    }
}
