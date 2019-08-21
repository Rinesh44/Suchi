package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;

public class SearchStockPresenterImpl implements SearchStockPresenter {
    private static final String TAG = "SearchStockPresenterImp";
    private Endpoints endpoints;
    private SearchStock activity;

    public SearchStockPresenterImpl(Endpoints endpoints, SearchStock activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }


    @Override
    public void getStockItems(String token) {
        endpoints.getInventory(token).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.getStockItemsFail("get inventory failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.getStockItemsFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "getInventoryResponse: " + baseResponse);

                List<Inventory> inventoryList = mapInventoryPbToModel(baseResponse.getInventoriesList());
                activity.getStockItemsSuccess(inventoryList);
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.getStockItemsFail("failed");

            }
        }));
    }

    private List<Inventory> mapInventoryPbToModel(List<InventoryProto.Inventory> inventoriesListPb) {
        List<Inventory> inventoryList = new ArrayList<>();
        for (InventoryProto.Inventory inventoryPb : inventoriesListPb
        ) {
            Inventory inventory = new Inventory();
            inventory.setInventory_id(inventoryPb.getInventoryId());
            inventory.setUser_id(inventoryPb.getUserId());
            inventory.setSynced(inventoryPb.getSync());
            inventory.setExpiryDate(String.valueOf(inventoryPb.getExpiryDate()));

            InventoryProto.StockKeepingUnit stockKeepingUnitPb = inventoryPb.getSku();
            StockKeepingUnit stockKeepingUnit = new StockKeepingUnit();
            stockKeepingUnit.setId(String.valueOf(stockKeepingUnitPb.getSkuId()));
            stockKeepingUnit.setName(stockKeepingUnitPb.getName());
            stockKeepingUnit.setPhoto_url(stockKeepingUnitPb.getPhoto());
            stockKeepingUnit.setCode(stockKeepingUnitPb.getCode());
            stockKeepingUnit.setDesc(stockKeepingUnitPb.getDescription());
            stockKeepingUnit.setUnitPrice(String.valueOf(stockKeepingUnitPb.getUnitPrice()));
            stockKeepingUnit.setSynced(stockKeepingUnitPb.getSync());
            stockKeepingUnit.setDefaultUnit(stockKeepingUnitPb.getDefaultUnit().getName());

            AppUtils.showLog(TAG, "defaultUnit: " + stockKeepingUnitPb.getDefaultUnit().getName());
            stockKeepingUnit.setDefaultUnit(stockKeepingUnitPb.getDefaultUnit().getName());

            InventoryProto.Brand brandPb = inventoryPb.getSku().getBrand();
            Brands brands = new Brands(brandPb.getBrandId(), brandPb.getName());
            stockKeepingUnit.setBrand(brands);

            InventoryProto.SubBrand subBrandPb = inventoryPb.getSku().getSubBrand();
            SubBrands subBrands = new SubBrands(subBrandPb.getSubBrandId(), subBrandPb.getBrandId(),
                    subBrandPb.getName());
            stockKeepingUnit.setSubBrands(subBrands);

            List<InventoryProto.Unit> unitPb = inventoryPb.getSku().getUnitsList();
            RealmList<Units> skuUnits = mapSKUUnits(unitPb);
            stockKeepingUnit.setUnits(skuUnits);

            InventoryProto.Category categoryPb = inventoryPb.getSku().getCategory();
            Categories categories = new Categories(categoryPb.getCategoryId(), categoryPb.getName());
            stockKeepingUnit.setCategories(categories);

            inventory.setSku(stockKeepingUnit);

            List<InventoryProto.InventoryStock> inventoryStockPbList = inventoryPb.getInventoryStocksList();
            RealmList<InventoryStocks> inventoryStocksRealmList = new RealmList<>();
            for (InventoryProto.InventoryStock inventoryStockPb : inventoryStockPbList
            ) {
                AppUtils.showLog(TAG, "unitId: " + inventoryStockPb.getUnit().getUnitId());
                InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockPb.getInventoryStockId(), String.valueOf(inventoryStockPb.getQuantity()),
                        String.valueOf(inventoryStockPb.getMarkedPrice()), String.valueOf(inventoryStockPb.getSalesPrice()),
                        inventoryStockPb.getUnit().getUnitId(), inventoryStockPb.getSync());

                inventoryStocksRealmList.add(inventoryStocks);
                inventory.setInventoryStocks(inventoryStocksRealmList);
            }

            inventoryList.add(inventory);

        }

        return inventoryList;
    }

    private RealmList<Units> mapSKUUnits(List<InventoryProto.Unit> unitPb) {
        RealmList<Units> skuUnits = new RealmList<>();
        for (InventoryProto.Unit unit : unitPb
        ) {
            Units units = new Units();
            units.setId(unit.getUnitId());
            units.setName(unit.getName());

            skuUnits.add(units);
        }

        return skuUnits;
    }

    @Override
    public void addStock(String token, InventoryProto.Inventory inventory) {
        endpoints.addInventory(token, inventory).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.addStockFail("Add stock failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.addStockFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "addStockResponse: " + baseResponse);

                activity.addStockSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.addStockFail("Add stock failed");
            }
        }));
    }

    @Override
    public void updateStock(String token, InventoryProto.Inventory inventory) {
        endpoints.updateInventory(token, inventory).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.updateStockFail("Update stock failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.updateStockFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "UpdateStockResponse: " + baseResponse);
                activity.updateStockSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.updateStockFail("Update stock failed");
            }
        }));
    }


}
