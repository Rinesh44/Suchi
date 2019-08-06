package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Items;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ItemsRepo extends Repo {
    private static final String TAG = "ItemsRepo";
    private static final ItemsRepo itemsRepo;

    static {
        itemsRepo = new ItemsRepo();
    }

    public static ItemsRepo getInstance() {
        return itemsRepo;
    }

    public void saveItemsList(final List<Items> itemsList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(itemsList);
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

    public List<Items> getAllItems() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            List<Items> itemsList = new ArrayList<>(realm.where(Items.class).findAll());
            return itemsList;

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
                    RealmResults<Items> items = realm.where(Items.class).findAll();
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
