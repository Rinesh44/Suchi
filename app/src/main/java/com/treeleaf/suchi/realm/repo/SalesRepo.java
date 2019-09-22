package com.treeleaf.suchi.realm.repo;

import android.text.format.DateUtils;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.utils.AppUtils;

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

    public void saveSales(final Sales sales, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(sales);
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

    public List<Sales> getSalesByDate(long fromDate, long tillDate) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Sales.class)
                    .between("createdAt", fromDate, tillDate)
                    .findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<Sales> getUnsyncedSalesList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Sales.class).equalTo("sync", false).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;

        }
    }

    public List<Sales> getSalesStockOfToday() {
        try {
            List<Sales> allStocks = getAllSalesList();
            List<Sales> todaysSales = new ArrayList<>();
            for (Sales sales : allStocks
            ) {
                AppUtils.showLog(TAG, "todayTimeStamp:" + sales.getCreatedAt());
                if (DateUtils.isToday(sales.getCreatedAt())) {
                    todaysSales.add(sales);
                }
            }

            return todaysSales;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
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