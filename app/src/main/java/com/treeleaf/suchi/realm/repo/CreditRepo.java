package com.treeleaf.suchi.realm.repo;


import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Credit;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CreditRepo extends Repo {
    private static final String TAG = "CreditRepo";
    private static final CreditRepo creditRepo;

    static {
        creditRepo = new CreditRepo();
    }


    public static CreditRepo getInstance() {
        return creditRepo;
    }

    public void saveCredit(final Credit credit, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(credit);
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

    public List<Credit> getAllCredits() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Credit.class).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public List<Credit> getCreditsWithDues() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Credit.class).notEqualTo("dueAmount", "0").findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public void deleteAllCredit(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Credit> credits = realm.where(Credit.class).findAll();
                    credits.deleteAllFromRealm();
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
