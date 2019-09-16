package com.treeleaf.suchi.realm.repo;

import com.treeleaf.suchi.realm.RealmDatabase;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Units;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class CategoryRepo extends Repo {
    public static final String TAG = "CategoryRepo";
    public static final CategoryRepo categotyRepo;

    static {
        categotyRepo = new CategoryRepo();
    }

    public static CategoryRepo getInstance() {
        return categotyRepo;
    }

    public void saveCategoryList(final List<Categories> categoryList, final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(categoryList);
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

    public List<Categories> getAllCategories() {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            List<Categories> categoriesList = new ArrayList<>(realm.where(Categories.class).findAll());
            return categoriesList;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }

    public List<String> getAllCategoryNames() {
        List<Categories> allCategories = getAllCategories();
        List<String> categoryNames = new ArrayList<>();
        for (Categories category : allCategories
        ) {
            categoryNames.add(category.getName());
        }
        return categoryNames;
    }

    public Categories getCategoryById(String categoryId) {
        Realm realm = RealmDatabase.getInstance().getRealm();
        try {
            return realm.where(Categories.class).equalTo("id", categoryId).findFirst();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        } finally {
            close(realm);
        }
    }


    public void deleteAllCategories(final Callback callback) {
        final Realm realm = RealmDatabase.getInstance().getRealm();

        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Categories> categories = realm.where(Categories.class).findAll();
                    categories.deleteAllFromRealm();
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
