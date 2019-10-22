package com.treeleaf.suchi.activities.forgotpassword;

import com.treeleaf.suchi.api.Endpoints;

public class ForgotPasswordPresenterImpl implements ForgotPasswordPresenter{
    private Endpoints endpoints;
    private ForgotPassword activity;

    public ForgotPasswordPresenterImpl(Endpoints endpoints, ForgotPassword activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }


    @Override
    public void forgotPassword(String emailPhone) {

    }
}
