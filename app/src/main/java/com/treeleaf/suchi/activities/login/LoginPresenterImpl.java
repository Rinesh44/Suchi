package com.treeleaf.suchi.activities.login;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.AccountProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class LoginPresenterImpl implements LoginPresenter {
    private static final String TAG = "LoginPresenterImpl";
    private Endpoints endpoints;
    private LoginActivity activity;

    public LoginPresenterImpl(Endpoints endpoints, LoginActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void login(String username, String password) {
        AccountProto.LoginRequest loginRequest = AccountProto.LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        endpoints.login(loginRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                AppUtils.showLog(TAG, "loginResponse: " + baseResponse);

                if (baseResponse == null) {
                    activity.loginFail("Login failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.loginFail(baseResponse.getMsg());
                    return;
                }

                activity.loginSuccess(baseResponse.getLoginResponse());
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.loginFail("Login failed");
            }
        }));
    }
}
