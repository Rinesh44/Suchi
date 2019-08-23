package com.treeleaf.suchi.activities.login;

import com.treeleaf.suchi.entities.TreeleafProto;

public interface LoginView {

    void loginSuccess(TreeleafProto.LoginResponse loginResponse);

    void loginFail(String msg);

    void getAllDataSuccess();

    void getAllDataFail(String msg);
}
