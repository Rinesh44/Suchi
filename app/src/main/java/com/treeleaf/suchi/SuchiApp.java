package com.treeleaf.suchi;

import android.app.Application;
import android.content.Context;

import com.treeleaf.suchi.dagger.component.AppComponent;
import com.treeleaf.suchi.dagger.component.DaggerAppComponent;
import com.treeleaf.suchi.dagger.component.module.NetModule;
import com.treeleaf.suchi.dagger.component.modules.module.AppModule;
import com.treeleaf.suchi.realm.RealmDatabase;

public class SuchiApp extends Application {
    private static final String TAG = "SuchiApp";
    private static Context context;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        SuchiApp.context = getApplicationContext();

        setUpRealm();
    }

    public static SuchiApp getMyApplication(Context context) {
        return (SuchiApp) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .netModule(new NetModule())
                    .build();
        }
        return appComponent;
    }

    private void setUpRealm() {
        RealmDatabase.init(this);
    }
}
