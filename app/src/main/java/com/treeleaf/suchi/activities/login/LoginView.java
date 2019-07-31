package com.treeleaf.suchi.activities.login;

import com.treeleaf.suchi.entities.AccountProto;

public interface LoginView {

    void loginSuccess(AccountProto.LoginResponse loginResponse);

    void loginFail(String msg);
}
