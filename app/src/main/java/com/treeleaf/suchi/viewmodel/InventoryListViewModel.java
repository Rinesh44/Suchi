package com.treeleaf.suchi.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.repo.InventoryRepo;

import io.realm.RealmResults;

public class InventoryListViewModel extends AndroidViewModel {

    private LiveData<RealmResults<Inventory>> inventories;

    public InventoryListViewModel(@NonNull Application application) {
        super(application);
        inventories = InventoryRepo.getInstance().getAllInventories();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<RealmResults<Inventory>> getInventories() {
        return inventories;
    }
}
