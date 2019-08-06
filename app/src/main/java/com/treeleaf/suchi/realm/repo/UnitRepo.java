package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Units;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UnitRepo extends Repo {
    private static final String TAG = "UnitRepo";
    public static final UnitRepo unitRepo;

    static {
        unitRepo = new UnitRepo();
    }

    public static UnitRepo getInstance() {
        return unitRepo;
    }

    public void saveUnits(final List<Units> unitList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(unitList);
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

    public List<Units> getAllUnits() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            List<Units> unitsList = new ArrayList<>(realm.where(Units.class).findAll());
            return unitsList;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public void deleteAllUnits(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Units> units = realm.where(Units.class).findAll();
                    units.deleteAllFromRealm();
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
