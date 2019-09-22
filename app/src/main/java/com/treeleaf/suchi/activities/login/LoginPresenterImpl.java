package com.treeleaf.suchi.activities.login;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.entities.TreeleafProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.BrandRepo;
import com.treeleaf.suchi.realm.repo.CategoryRepo;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.SalesRepo;
import com.treeleaf.suchi.realm.repo.StockKeepingUnitRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SubBrandRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
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
    private RealmList<StockKeepingUnit> stockKeepingUnitList = new RealmList<>();


    public LoginPresenterImpl(Endpoints endpoints, LoginActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void login(String username, String password) {
        TreeleafProto.LoginRequest loginRequest = TreeleafProto.LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        endpoints.login(loginRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                AppUtils.showLog(TAG, "loginResponse: " + baseResponse);
                activity.hideLoading();

                if (baseResponse == null) {
                    activity.hideLoading();
                    activity.loginFail("Login failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.hideLoading();
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
        endpoints.getAllData(token).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {

                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.getAllDataFail("get all data failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.getAllDataFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "allDataResponse: " + baseResponse);

                AppUtils.showLog(TAG, "subBrands: " + baseResponse.getSubBrandsList().toString());

                AppUtils.showLog(TAG, "sales: " + baseResponse.getSalesList());

                if (baseResponse.getInventoriesList() != null && !baseResponse.getInventoriesList().isEmpty())
                    mapInventories(baseResponse.getInventoriesList());
                if (baseResponse.getStockKeepingUnitsList() != null && !baseResponse.getStockKeepingUnitsList().isEmpty())
                    mapStockKeepingUnits(baseResponse.getStockKeepingUnitsList());
                if (baseResponse.getBrandsList() != null && !baseResponse.getBrandsList().isEmpty())
                    mapBrands(baseResponse.getBrandsList());
                if (baseResponse.getSubBrandsList() != null && !baseResponse.getSubBrandsList().isEmpty())
                    mapSubBrands(baseResponse.getSubBrandsList());
                if (baseResponse.getCategoriesList() != null && !baseResponse.getCategoriesList().isEmpty())
                    mapCategories(baseResponse.getCategoriesList());
                if (baseResponse.getUnitsList() != null && !baseResponse.getUnitsList().isEmpty())
                    mapUnits(baseResponse.getUnitsList());
                if (baseResponse.getSalesList() != null && !baseResponse.getSalesList().isEmpty())
                    mapSales(baseResponse.getSalesList());

                activity.getAllDataSuccess();

            }


            @Override
            public void onFailureResult() {
                activity.getAllDataFail("failed");

            }
        }));
    }

    private void mapSales(List<SuchiProto.Sale> salesList) {
        List<Sales> sales = new ArrayList<>();
        for (SuchiProto.Sale salePb : salesList
        ) {
            Sales salesModel = new Sales();

            RealmList<SalesStock> salesStockList = new RealmList<>();
            List<SuchiProto.SaleInventory> saleInventoryListPb = salePb.getSaleInventoriesList();
            for (SuchiProto.SaleInventory saleInventoryPb : saleInventoryListPb
            ) {
                SalesStock salesStock = new SalesStock();

                Inventory inventory = InventoryRepo.getInstance().getInventoryById(saleInventoryPb.getInventoryId());
                salesStock.setId(saleInventoryPb.getInventoryStockId());
                salesStock.setInventory_id(saleInventoryPb.getInventoryId());
                salesStock.setAmount(String.valueOf(saleInventoryPb.getAmount()));
                salesStock.setQuantity(String.valueOf(saleInventoryPb.getQuantity()));
                salesStock.setUnit(saleInventoryPb.getUnit().getName());
                salesStock.setName(inventory.getSku().getName());
                salesStock.setPhotoUrl(inventory.getSku().getPhoto_url());
                salesStock.setUnitPrice(inventory.getSku().getUnitPrice());
                salesStock.setBrand(inventory.getSku().getBrand().getName());
                salesStock.setSubBrand(inventory.getSku().getSubBrands().getName());
                salesStock.setCategories(inventory.getSku().getCategories().getName());
                salesStock.setCreatedAt(saleInventoryPb.getCreatedAt());
                salesStock.setUpdatedAt(saleInventoryPb.getUpdatedAt());

                salesStockList.add(salesStock);
            }

            salesModel.setSaleId(salePb.getSaleId());
            salesModel.setTotalAmount(String.valueOf(salePb.getAmount()));
            salesModel.setCreatedAt(salePb.getCreatedAt());
            salesModel.setUpdatedAt(salePb.getUpdatedAt());
            salesModel.setSync(salePb.getSync());
            salesModel.setUserId(salePb.getUserId());
            salesModel.setCredit(salePb.getIsCredit());
            salesModel.setCreditId(salePb.getCreditorId());
            salesModel.setSalesStocks(salesStockList);

            sales.add(salesModel);
        }

        SalesRepo.getInstance().saveSalesList(sales, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "sales data saved");
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save sales data");
            }
        });


    }

    private void mapInventories(List<SuchiProto.Inventory> inventoriesListPb) {
        List<Inventory> inventoryList = new ArrayList<>();
        for (SuchiProto.Inventory inventoryPb : inventoriesListPb
        ) {
            Inventory inventory = new Inventory();
            inventory.setInventory_id(inventoryPb.getInventoryId());
            inventory.setUser_id(inventoryPb.getUserId());
            inventory.setSynced(inventoryPb.getSync());
            inventory.setSkuId(inventoryPb.getSku().getSkuId());
            inventory.setExpiryDate(String.valueOf(inventoryPb.getExpiryDate()));

            SuchiProto.StockKeepingUnit stockKeepingUnitPb = inventoryPb.getSku();
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setId(String.valueOf(stockKeepingUnitPb.getSkuId()));
            stockKeepingUnit.setName(stockKeepingUnitPb.getName());
            stockKeepingUnit.setPhoto_url(stockKeepingUnitPb.getPhoto());
            stockKeepingUnit.setCode(stockKeepingUnitPb.getCode());
            stockKeepingUnit.setDesc(stockKeepingUnitPb.getDescription());
            stockKeepingUnit.setUnitPrice(String.valueOf(stockKeepingUnitPb.getUnitPrice()));
            stockKeepingUnit.setSynced(stockKeepingUnitPb.getSync());

            SuchiProto.Brand brandPb = inventoryPb.getSku().getBrand();
            Brands brands = new Brands(brandPb.getBrandId(), brandPb.getName());
            stockKeepingUnit.setBrand(brands);

            SuchiProto.SubBrand subBrandPb = inventoryPb.getSku().getSubBrand();
            SubBrands subBrands = new SubBrands(subBrandPb.getSubBrandId(), subBrandPb.getBrandId(),
                    subBrandPb.getName());
            stockKeepingUnit.setSubBrands(subBrands);

            List<SuchiProto.Unit> unitPb = inventoryPb.getSku().getUnitsList();

            RealmList<Units> skuUnits = mapSKUUnits(unitPb);
            stockKeepingUnit.setUnits(skuUnits);

            SuchiProto.Category categoryPb = inventoryPb.getSku().getCategory();
            Categories categories = new Categories(categoryPb.getCategoryId(), categoryPb.getName());
            stockKeepingUnit.setCategories(categories);

            inventory.setSku(stockKeepingUnit);

            List<SuchiProto.InventoryStock> inventoryStockPbList = inventoryPb.getInventoryStocksList();
            RealmList<InventoryStocks> inventoryStocksRealmList = new RealmList<>();
            for (SuchiProto.InventoryStock inventoryStockPb : inventoryStockPbList
            ) {
                InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockPb.getInventoryStockId(), inventoryStockPb.getInventoryId(), String.valueOf(inventoryStockPb.getQuantity()),
                        String.valueOf(inventoryStockPb.getMarkedPrice()), String.valueOf(inventoryStockPb.getSalesPrice()),
                        String.valueOf(inventoryStockPb.getUnit().getUnitId()), inventoryStockPb.getSync());

                inventoryStocksRealmList.add(inventoryStocks);
                inventory.setInventoryStocks(inventoryStocksRealmList);
            }

            inventoryList.add(inventory);

            InventoryRepo.getInstance().saveInventoryList(inventoryList, new Repo.Callback() {
                @Override
                public void success(Object o) {

                }

                @Override
                public void fail() {
                    AppUtils.showLog(TAG, "failed to save inventories");
                }
            });

        }
    }

    private RealmList<Units> mapSKUUnits(List<SuchiProto.Unit> unitPb) {
        RealmList<Units> skuUnits = new RealmList<>();
        for (SuchiProto.Unit unit : unitPb
        ) {
            Units units = new Units();
            units.setId(unit.getUnitId());
            units.setName(unit.getName());

            skuUnits.add(units);
        }

        return skuUnits;
    }

    private void mapStockKeepingUnits(List<SuchiProto.StockKeepingUnit> stockKeepingUnitsListPb) {
        for (SuchiProto.StockKeepingUnit itemsPb : stockKeepingUnitsListPb
        ) {
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setId(String.valueOf(itemsPb.getSkuId()));
            stockKeepingUnit.setName(itemsPb.getName());
            stockKeepingUnit.setPhoto_url(itemsPb.getPhoto());
            stockKeepingUnit.setCode(itemsPb.getCode());
            stockKeepingUnit.setDesc(itemsPb.getDescription());

            stockKeepingUnit.setUnitPrice(String.valueOf(itemsPb.getUnitPrice()));
            stockKeepingUnit.setDefaultUnit(itemsPb.getDefaultUnit().getName());

            Brands brands = new Brands();
            brands.setId(itemsPb.getBrand().getBrandId());
            brands.setName(itemsPb.getBrand().getName());

            SubBrands subBrands = new SubBrands();
            subBrands.setId(itemsPb.getSubBrand().getSubBrandId());
            subBrands.setBrandId(itemsPb.getSubBrand().getBrandId());
            subBrands.setName(itemsPb.getSubBrand().getName());

           /* Units units = new Units();
            units.setId(itemsPb.getUnit().getUnitId());
            units.setName(itemsPb.getUnit().getName());*/

            AppUtils.showLog(TAG, "UnitList: " + itemsPb.getUnitsList());

            Categories categories = new Categories();
            categories.setId(itemsPb.getCategory().getCategoryId());
            categories.setName(itemsPb.getCategory().getName());

            stockKeepingUnit.setBrand(brands);
            stockKeepingUnit.setSubBrands(subBrands);

            List<SuchiProto.Unit> unitPb = itemsPb.getUnitsList();
            RealmList<Units> skuUnits = mapSKUUnits(unitPb);
            stockKeepingUnit.setUnits(skuUnits);

            stockKeepingUnit.setCategories(categories);

            stockKeepingUnitList.add(stockKeepingUnit);

            StockKeepingUnitRepo.getInstance().saveSkuList(stockKeepingUnitList, new Repo.Callback() {
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

    private void mapUnits(List<SuchiProto.Unit> unitsListPb) {
        for (SuchiProto.Unit unitPb : unitsListPb
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


    private void mapCategories(List<SuchiProto.Category> categoriesListPb) {
        for (SuchiProto.Category categoryPb : categoriesListPb
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

    private void mapBrands(List<SuchiProto.Brand> brandsListPb) {
        for (SuchiProto.Brand brandPb : brandsListPb
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

    private void mapSubBrands(List<SuchiProto.SubBrand> subBrandsListPb) {
        for (SuchiProto.SubBrand subBrandsPb : subBrandsListPb
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
