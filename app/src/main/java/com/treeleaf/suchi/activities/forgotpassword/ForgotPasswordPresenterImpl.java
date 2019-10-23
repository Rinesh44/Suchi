package com.treeleaf.suchi.activities.forgotpassword;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import retrofit2.Response;

public class ForgotPasswordPresenterImpl implements ForgotPasswordPresenter{
    private static final String TAG = "ForgotPasswordPresenter";
    private Endpoints endpoints;
    private ForgotPassword activity;

    public ForgotPasswordPresenterImpl(Endpoints endpoints, ForgotPassword activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }


    @Override
    public void resetPassword(SuchiProto.PasswordReset passwordReset) {
        endpoints.resetPassword(passwordReset).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                activity.hideLoading();

                AppUtils.showLog(TAG, "resetPasswordResponse: " + baseResponse);
                if(baseResponse == null){
                    activity.showMessage("Password reset failed");
                    return;
                }

                if(baseResponse.getError()){
                    activity.showMessage(baseResponse.getMsg());
                    return;
                }

                activity.resetPasswordSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.resetPasswordFail("Failed");
            }
        }));
    }
}
