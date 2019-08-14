package com.treeleaf.suchi.utils;

import androidx.lifecycle.LiveData;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class RealmLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {
    private static final String TAG = "RealmLiveData";
    private RealmResults<T> results;
    private final RealmChangeListener<RealmResults<T>> listener =
            new RealmChangeListener<RealmResults<T>>() {
                @Override
                public void onChange(RealmResults<T> results) {
                    setValue(results);
                }
            };

    public RealmLiveData(RealmResults<T> realmResults) {

        results = realmResults;
        if (results.isLoaded()) {
            // we should not notify observers when results aren't ready yet (async query).
            // however, synchronous query should be set explicitly.
            setValue(results);
        }
    }

    @Override
    protected void onActive() {
        if (results.isValid())
            results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        if (results.isValid())
            results.removeChangeListener(listener);
    }
}