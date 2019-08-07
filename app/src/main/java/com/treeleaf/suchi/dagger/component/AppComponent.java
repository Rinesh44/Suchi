package com.treeleaf.suchi.dagger.component;

import com.treeleaf.suchi.activities.dashboard.DashboardActivity;
import com.treeleaf.suchi.activities.inventory.stock.SearchStock;
import com.treeleaf.suchi.activities.login.LoginActivity;
import com.treeleaf.suchi.activities.register.RegisterActivity;
import com.treeleaf.suchi.dagger.component.module.NetModule;
import com.treeleaf.suchi.dagger.component.modules.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {
    void inject(RegisterActivity registerActivity);

    void inject(LoginActivity loginActivity);

    void inject(DashboardActivity dashboardActivity);

    void inject(SearchStock searchStock);
}