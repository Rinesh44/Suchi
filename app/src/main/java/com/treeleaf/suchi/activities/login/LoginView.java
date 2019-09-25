package com.treeleaf.suchi.activities.login;

import com.treeleaf.suchi.entities.TreeleafProto;
import com.treeleaf.suchi.rpc.SuchiRpcProto;

public interface LoginView {

    void loginSuccess(SuchiRpcProto.SuchiBaseResponse baseResponse);

    void loginFail(String msg);

    void getAllDataSuccess();

    void getAllDataFail(String msg);
}
