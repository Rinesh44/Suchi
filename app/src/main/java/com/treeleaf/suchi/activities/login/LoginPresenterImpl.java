package com.treeleaf.suchi.activities.login;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.AccountProto;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Items;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.BrandRepo;
import com.treeleaf.suchi.realm.repo.CategoryRepo;
import com.treeleaf.suchi.realm.repo.ItemsRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SubBrandRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;

public class LoginPresenterImpl implements LoginPresenter {
    private static final String TAG = "LoginPresenterImpl";
    private Endpoints endpoints;
    private LoginActivity activity;
    private RealmList<Brands> brandsList = new RealmList<>();
    private RealmList<SubBrands> subBrandsList = new RealmList<>();
    private RealmList<Categories> categoriesList = new RealmList<>();
    private RealmList<Units> unitsList = new RealmList<>();
    private RealmList<Items> itemsList = new RealmList<>();


    public LoginPresenterImpl(Endpoints endpoints, LoginActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void login(String username, String password) {
        AccountProto.LoginRequest loginRequest = AccountProto.LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        endpoints.login(loginRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                AppUtils.showLog(TAG, "loginResponse: " + baseResponse);

                if (baseResponse == null) {
                    activity.loginFail("Login failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.loginFail(baseResponse.getMsg());
                    return;
                }

                activity.loginSuccess(baseResponse.getLoginResponse());
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.loginFail("Login failed");
            }
        }));
    }

    @Override
    public void getAllData(String token) {
        endpoints.getAllData(token).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();

                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.getAllDataFail("get all data failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.getAllDataFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "allDataResponse: " + baseResponse);

                mapStockKeepingUnits(baseResponse.getStockKeepingUnitsList());
                mapBrands(baseResponse.getBrandsList());
                mapSubBrands(baseResponse.getSubBrandsList());
                mapCategories(baseResponse.getCategoriesList());
                mapUnits(baseResponse.getUnitsList());


                activity.getAllDataSuccess();

            }


            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.getAllDataFail("failed");

            }
        }));
    }

    private void mapStockKeepingUnits(List<InventoryProto.StockKeepingUnit> stockKeepingUnitsListPb) {
        for (InventoryProto.StockKeepingUnit itemsPb : stockKeepingUnitsListPb
        ) {
            Items items = new Items();
            items.setId(String.valueOf(itemsPb.getSkuId()));
            items.setName(itemsPb.getName());
            items.setPhoto_url(itemsPb.getPhoto());
            items.setCode(itemsPb.getCode());
            items.setDesc(itemsPb.getDescription());
            items.setUnitPrice(String.valueOf(itemsPb.getUnitPrice()));
            items.setCategory(String.valueOf(itemsPb.getCategory()));

            Brands brands = new Brands();
            brands.setId(itemsPb.getBrand().getBrandId());
            brands.setName(itemsPb.getBrand().getName());

            SubBrands subBrands = new SubBrands();
            subBrands.setId(itemsPb.getSubBrand().getSubBrandId());
            subBrands.setBrandId(itemsPb.getSubBrand().getBrandId());
            subBrands.setName(itemsPb.getSubBrand().getName());

            Units units = new Units();
            units.setId(itemsPb.getUnit().getUnitId());
            units.setName(itemsPb.getUnit().getName());

            items.setBrand(brands);
            items.setSubBrands(subBrands);
            items.setUnits(units);

            itemsList.add(items);

            ItemsRepo.getInstance().saveItemsList(itemsList, new Repo.Callback() {
                @Override
                public void success(Object o) {

                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "failed to save sku to db");
                }
            });
        }
    }

    private void mapUnits(List<InventoryProto.Unit> unitsListPb) {
        for (InventoryProto.Unit unitPb : unitsListPb
        ) {
            Units units = new Units(unitPb.getUnitId(), unitPb.getName());
            unitsList.add(units);

            UnitRepo.getInstance().saveUnits(unitsList, new Repo.Callback() {
                @Override
                public void success(Object o) {
                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "failed to save units to db");
                }
            });
        }
    }

    private void mapCategories(List<InventoryProto.Category> categoriesListPb) {
        for (InventoryProto.Category categoryPb : categoriesListPb
        ) {
            Categories categories = new Categories(categoryPb.getCategoryId(), categoryPb.getName());
            categoriesList.add(categories);

            CategoryRepo.getInstance().saveCategoryList(categoriesList, new Repo.Callback() {
                @Override
                public void success(Object o) {
                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "failed to save categories to db");
                }
            });
        }
    }

    private void mapBrands(List<InventoryProto.Brand> brandsListPb) {
        for (InventoryProto.Brand brandPb : brandsListPb
        ) {
            Brands brands = new Brands(brandPb.getBrandId(), brandPb.getName());
            brandsList.add(brands);

            BrandRepo.getInstance().saveBrandList(brandsList, new Repo.Callback() {
                @Override
                public void success(Object o) {
                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "failed to save brands to db");
                }
            });
        }
    }

    private void mapSubBrands(List<InventoryProto.SubBrand> subBrandsListPb) {
        for (InventoryProto.SubBrand subBrandsPb : subBrandsListPb
        ) {
            SubBrands subBrands = new SubBrands(subBrandsPb.getSubBrandId(), subBrandsPb.getBrandId(),
                    subBrandsPb.getName());
            subBrandsList.add(subBrands);

            SubBrandRepo.getInstance().saveSubBrandList(subBrandsList, new Repo.Callback() {
                @Override
                public void success(Object o) {
                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "failed to save sub brands to db");
                }
            });
        }
    }
}
