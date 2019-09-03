package com.treeleaf.suchi.realm.repo;

import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.utils.RealmLiveData;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SalesStockRepo extends Repo {
    private static final String TAG = "SalesStockRepo";
    private static final SalesStockRepo salesStockRepo;

    static {
        salesStockRepo = new SalesStockRepo();
    }


    public static SalesStockRepo getInstance() {
        return salesStockRepo;
    }

    public void saveSalesStockList(final List<SalesStock> salesStockList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(salesStockList);
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

    public LiveData<RealmResults<SalesStock>> getAllSalesStockList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new RealmLiveData<>(realm.where(SalesStock.class).findAllAsync());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<SalesStock> getUnsyncedSalesStockList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class).equalTo("synced", false).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;

        } finally {
            close(realm);
        }
    }

    public List<SalesStock> getSyncedSalesStockList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {

            return new ArrayList<>(realm.where(SalesStock.class).equalTo("synced", true).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;

        } finally {
            close(realm);
        }
    }


    public void deleteAllSalesStock(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<SalesStock> salesStocks = realm.where(SalesStock.class).findAll();
                    salesStocks.deleteAllFromRealm();
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

}