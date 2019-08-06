package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.SubBrands;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class SubBrandRepo extends Repo {
    private static final String TAG = "SubBrandRepo";
    private static final SubBrandRepo subBrandRepo;

    static {
        subBrandRepo = new SubBrandRepo();
    }


    public static SubBrandRepo getInstance() {
        return subBrandRepo;
    }

    public void saveSubBrandList(final List<SubBrands> subBrandList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(subBrandList);
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

    public List<SubBrands> getAllSubBrands() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            List<SubBrands> subBrandList = new ArrayList<>(realm.where(SubBrands.class).findAll());
            return subBrandList;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public void deleteAllSubBrands(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<SubBrands> subBrands = realm.where(SubBrands.class).findAll();
                    subBrands.deleteAllFromRealm();
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
