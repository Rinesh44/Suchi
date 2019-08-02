package com.treeleaf.suchi.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmDatabase {

    private static RealmDatabase database;
    public static void init(Context context){
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("suchi")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(configuration);
        database = new RealmDatabase();
    }

    private RealmDatabase() {}

    public static RealmDatabase getInstance(){
        return database;
    }

    public Realm getRealm(){
        return Realm.getDefaultInstance();
    }
}