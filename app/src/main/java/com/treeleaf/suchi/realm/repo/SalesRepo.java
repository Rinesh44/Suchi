package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Sales;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SalesRepo extends Repo {
    private static final String TAG = "SalesRepo";
    private static final SalesRepo salesRepo;

    static {
        salesRepo = new SalesRepo();
    }


    public static SalesRepo getInstance() {
        return salesRepo;
    }

    public void saveSalesList(final List<Sales> salesList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(salesList);
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

    public List<Sales> getAllSalesList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Sales.class).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }




    public void deleteAllSales(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Sales> sales = realm.where(Sales.class).findAll();
                    sales.deleteAllFromRealm();
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