package com.treeleaf.suchi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;

import io.realm.RealmResults;

public class SalesListViewModel extends AndroidViewModel {
    private static final String TAG = "SalesListViewModel";

    private LiveData<RealmResults<SalesStock>> saleItems;

    public SalesListViewModel(@NonNull Application application) {
        super(application);
        saleItems = SalesStockRepo.getInstance().getAllSalesStockList();
    }

    public LiveData<RealmResults<SalesStock>> getSaleItems() {
        return saleItems;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


}
