package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class SearchStockPresenterImpl implements SearchStockPresenter {
    private static final String TAG = "SearchStockPresenterImp";
    private Endpoints endpoints;
    private SearchStock activity;

    public SearchStockPresenterImpl(Endpoints endpoints, SearchStock activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }


    @Override
    public void addStock(String token, InventoryProto.Inventory inventory) {
        endpoints.addInventory(token, inventory).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.addStockFail("Add stock failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.addStockFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "addStockResponse: " + baseResponse);

                activity.addStockSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.addStockFail("Add stock failed");
            }
        }));
    }
}
