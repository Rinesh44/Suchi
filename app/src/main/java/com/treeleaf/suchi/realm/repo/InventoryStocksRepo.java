package com.treeleaf.suchi.realm.repo;


import com.treeleaf.suchi.realm.RealmDatabase;

import com.treeleaf.suchi.realm.models.InventoryStocks;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class InventoryStocksRepo extends Repo {
    private static final String TAG = "InventoryStockRepo";
    private static final InventoryStocksRepo inventoryStockRepo;

    static {
        inventoryStockRepo = new InventoryStocksRepo();
    }


    public static InventoryStocksRepo getInstance() {
        return inventoryStockRepo;
    }

    public void saveInventoryStocks(final List<InventoryStocks> inventoryStocksList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(inventoryStocksList);
                    callback.success(true);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            callback.fail();
        } finally {
            close(realm);
        }
    }

    public List<InventoryStocks> getAllInventoryStocks() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(InventoryStocks.class).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

}