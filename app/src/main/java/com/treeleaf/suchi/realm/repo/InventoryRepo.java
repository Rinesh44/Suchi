package com.treeleaf.suchi.realm.repo;

import androidx.lifecycle.LiveData;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.utils.RealmLiveData;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class InventoryRepo extends Repo {

    private static final String TAG = "InventoryRepo";
    private static final InventoryRepo STOCK_KEEPING_UNIT_REPO;

    static {
        STOCK_KEEPING_UNIT_REPO = new InventoryRepo();
    }

    public static InventoryRepo getInstance() {
        return STOCK_KEEPING_UNIT_REPO;
    }

    public void saveInventory(final Inventory inventory, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(inventory);
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

    public void saveInventoryList(final List<Inventory> InventoryList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(InventoryList);
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

    public LiveData<RealmResults<Inventory>> getAllInventories() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new RealmLiveData<>(realm.where(Inventory.class).findAllAsync());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public List<Inventory> getAllInventoryList() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Inventory.class).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }


    public List<Inventory> getUnsyncedInventories() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Inventory.class)
                    .equalTo("synced", false).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public List<Inventory> getSyncedInventories() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Inventory.class)
                    .equalTo("synced", true).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public Inventory getInventoryById(String id) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return realm.where(Inventory.class).equalTo("inventory_id", id)
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
                    RealmResults<Inventory> items = realm.where(Inventory.class).findAll();
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
