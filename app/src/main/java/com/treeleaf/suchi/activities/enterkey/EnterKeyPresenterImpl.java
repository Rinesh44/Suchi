package com.treeleaf.suchi.activities.enterkey;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class EnterKeyPresenterImpl implements EnterKeyPresenter {
    private static final String TAG = "EnterKeyPresenterImpl";
    private Endpoints endpoints;
    private EnterKeyActivity activity;

    public EnterKeyPresenterImpl(Endpoints endpoints, EnterKeyActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void freeTrail(String auth, SuchiProto.SuchiKey suchiKey) {
        endpoints.getFreeTrial(auth, suchiKey).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.showMessage("Get free trial failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.showMessage(baseResponse.getMsg());
                    return;
                }

                activity.freeTrialSuccess();
                AppUtils.showLog(TAG, "freetrialResponse: " + baseResponse);
            }

            @Override
            public void onFailureResult() {
                activity.freeTrialFail("Free trail failed");
            }
        }));
    }
}
