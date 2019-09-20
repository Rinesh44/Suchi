package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Creditors;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.Units;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CreditorRepo extends Repo {
    private static final String TAG = "CreditorRepo";
    private static final CreditorRepo creditorRepo;

    static {
        creditorRepo = new CreditorRepo();
    }


    public static CreditorRepo getInstance() {
        return creditorRepo;
    }

    public void saveCreditor(final Creditors creditor, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(creditor);
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

    public List<Creditors> getUnsyncedCreditors() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Creditors.class)
                    .equalTo("sync", false).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }


    public List<Creditors> getAllCreditors() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Creditors.class).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public Creditors getCreditorById(String id) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            return realm.where(Creditors.class).equalTo("id", id).findFirst();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public List<String> getAllCreditorNames() {
        List<Creditors> creditorList = getAllCreditors();
        List<String> creditorsNames = new ArrayList<>();
        for (Creditors creditors : creditorList
        ) {
            creditorsNames.add(creditors.getName());
        }

        return creditorsNames;
    }


    public void deleteAllCreditors(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Creditors> creditors = realm.where(Creditors.class).findAll();
                    creditors.deleteAllFromRealm();
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
