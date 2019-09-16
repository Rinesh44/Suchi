package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Brands;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class BrandRepo extends Repo {
    private static final String TAG = "BrandRepo";
    private static final BrandRepo brandRepo;

    static {
        brandRepo = new BrandRepo();
    }


    public static BrandRepo getInstance() {
        return brandRepo;
    }

    public void saveBrandList(final List<Brands> brandList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(brandList);
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

    public List<Brands> getAllBrands() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return new ArrayList<>(realm.where(Brands.class).findAll());

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public List<String> getAllBrandNames() {
        List<Brands> brandsList = getAllBrands();
        List<String> brandNamesList = new ArrayList<>();
        for (Brands brand : brandsList
        ) {
            brandNamesList.add(brand.getName());
        }

        return brandNamesList;
    }


    public void deleteAllAddresses(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Brands> brands = realm.where(Brands.class).findAll();
                    brands.deleteAllFromRealm();
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
