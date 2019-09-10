package com.treeleaf.suchi;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.treeleaf.suchi.dagger.component.AppComponent;
import com.treeleaf.suchi.dagger.component.DaggerAppComponent;
import com.treeleaf.suchi.dagger.component.module.NetModule;
import com.treeleaf.suchi.dagger.component.modules.module.AppModule;
import com.treeleaf.suchi.realm.RealmDatabase;

public class SuchiApp extends Application {
    private static final String TAG = "SuchiApp";
    public static final String CHANNEL_ID = "channel1";
    private static Context context;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        SuchiApp.context = getApplicationContext();

        setUpRealm();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.setDescription("This is channel 1");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
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
