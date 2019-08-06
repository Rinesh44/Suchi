package com.treeleaf.suchi.activities.login;

public interface LoginPresenter {

    void login(String username, String password);

    void getAllData(String token);

}
