package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class StockKeepingUnitRepo extends Repo {
    private static final String TAG = "StockKeepingUnitRepo";
    private static final StockKeepingUnitRepo STOCK_KEEPING_UNIT_REPO;

    static {
        STOCK_KEEPING_UNIT_REPO = new StockKeepingUnitRepo();
    }

    public static StockKeepingUnitRepo getInstance() {
        return STOCK_KEEPING_UNIT_REPO;
    }

    public void saveSkuList(final List<StockKeepingUnit> stockKeepingUnitList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(stockKeepingUnitList);
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

    public List<StockKeepingUnit> getAllSkuList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            List<StockKeepingUnit> stockKeepingUnitList = new ArrayList<>(realm.where(StockKeepingUnit.class).findAll());
            return stockKeepingUnitList;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public StockKeepingUnit getSkuById(String id) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return realm.where(StockKeepingUnit.class).equalTo("id", id)
                    .findFirst();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public void deleteAllItems(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<StockKeepingUnit> items = realm.where(StockKeepingUnit.class).findAll();
                    items.deleteAllFromRealm();
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
