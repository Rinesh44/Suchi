package com.treeleaf.suchi.realm.repo;

import android.text.format.DateUtils;

import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.utils.AppUtils;
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


    public LiveData<RealmResults<SalesStock>> getAllSalesStock() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new RealmLiveData<>(realm.where(SalesStock.class).findAllAsync());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<SalesStock> getSalesStockByBrand(String brand) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class).equalTo("brand", brand).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<SalesStock> getSalesStockBySubBrand(String subBrand) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class).equalTo("subBrand", subBrand).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<SalesStock> getSalesStockByCategory(String category) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class).equalTo("categories", category).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<SalesStock> getSalesStockByItem(String item) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class).equalTo("name", item).findAll());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<SalesStock> getAllSalesStockList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }


    public List<SalesStock> getSalesStockByDate(long fromDate, long tillDate) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(SalesStock.class)
                    .between("createdAt", fromDate, tillDate)
                    .findAll());

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

        }
    }


    public List<SalesStock> getSalesStockOfToday() {
        try {
            List<SalesStock> allStocks = getAllSalesStockList();
            List<SalesStock> todaysSales = new ArrayList<>();
            for (SalesStock salesStock : allStocks
            ) {
                AppUtils.showLog(TAG, "todayTimeStamp:" + salesStock.getCreatedAt());
                if (DateUtils.isToday(salesStock.getCreatedAt())) {
                    todaysSales.add(salesStock);
                }
            }

            return todaysSales;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
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