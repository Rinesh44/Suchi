package com.treeleaf.suchi.activities.register;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.AccountProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class RegisterPresenterImpl implements RegisterPresenter {
    private static final String TAG = "RegisterPresenterImpl";
    private Endpoints endpoints;
    private RegisterActivity activity;

    public RegisterPresenterImpl(Endpoints endpoints, RegisterActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void register(String storeName, String address, String phone, String ownerName, String username, String password) {
        AccountProto.User user = AccountProto.User.newBuilder()
                .setAddress(address)
                .setStoreName(storeName)
                .setPhone(phone)
                .setOwnerName(ownerName)
                .setUsername(username)
                .setPassword(password)
                .build();

        endpoints.register(user).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();
                AppUtils.showLog(TAG, "registerResponse: " + baseResponse);

                if (baseResponse == null) {
                    activity.registerFail(null);
                    return;
                }

                if (baseResponse.getError()) {
                    activity.registerFail(baseResponse.getMsg());
                    return;
                }

                activity.registerSuccess();

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.registerFail(null);

            }
        }));
    }
}
