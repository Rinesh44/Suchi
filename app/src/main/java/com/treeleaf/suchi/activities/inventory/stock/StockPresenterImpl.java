package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class StockPresenterImpl implements StockPresenter {
    private static final String TAG = "StockPresenterImpl";
    private StockActivity activity;
    private Endpoints endpoints;

    public StockPresenterImpl(StockActivity activity, Endpoints endpoints) {
        this.activity = activity;
        this.endpoints = endpoints;
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
            inventory.setQuantity(String.valueOf(inventoryPb.getQuantity()));
            inventory.setMarkedPrice(String.valueOf(inventoryPb.getMarkedPrice()));
            inventory.setSalesPrice(String.valueOf(inventoryPb.getSalesPrice()));
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

            InventoryProto.Brand brandPb = inventoryPb.getSku().getBrand();
            Brands brands = new Brands(brandPb.getBrandId(), brandPb.getName());
            stockKeepingUnit.setBrand(brands);

            InventoryProto.SubBrand subBrandPb = inventoryPb.getSku().getSubBrand();
            SubBrands subBrands = new SubBrands(subBrandPb.getSubBrandId(), subBrandPb.getBrandId(),
                    subBrandPb.getName());
            stockKeepingUnit.setSubBrands(subBrands);

            InventoryProto.Unit unitPb = inventoryPb.getSku().getUnit();
            Units units = new Units(unitPb.getUnitId(), unitPb.getName());
            stockKeepingUnit.setUnits(units);

            InventoryProto.Category categoryPb = inventoryPb.getSku().getCategory();
            Categories categories = new Categories(categoryPb.getCategoryId(), categoryPb.getName());
            stockKeepingUnit.setCategories(categories);

            inventory.setSku(stockKeepingUnit);

            inventoryList.add(inventory);

        }

        return inventoryList;
    }

    @Override
    public void addUnsyncedInventories(String token, List<Inventory> inventoryList) {
        List<InventoryProto.Inventory> inventoryListProto = mapInventoryModelToProto(inventoryList);
        endpoints.addUnSyncedInventories(token, inventoryListProto).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
            @Override
            public void onSuccessResult(Response<ReqResProto.Response> response) {
                activity.hideLoading();
                ReqResProto.Response baseResponse = response.body();

                if (baseResponse == null) {
                    activity.addUnsyncedInventoriesFail("sync inventory failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.addUnsyncedInventoriesFail(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "syncInventoryResponse: " + baseResponse.toString());

                activity.addUnsyncedInventoriesSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.addUnsyncedInventoriesFail("sync inventory failed");
            }
        }));
    }

    private List<InventoryProto.Inventory> mapInventoryModelToProto(List<Inventory> inventoryList) {
        List<InventoryProto.Inventory> inventoryListProto = new ArrayList<>();
        for (Inventory inventory : inventoryList
        ) {
            InventoryProto.Inventory inventoryProto = InventoryProto.Inventory.newBuilder()
                    .setInventoryId(inventory.getInventory_id())
                    .setSkuId(inventory.getSkuId())
                    .setUserId(inventory.getUser_id())
                    .setUnitId(inventory.getUnitId())
                    .setStatus(InventoryProto.SKUStatus.AVAILABLE)
                    .setMarkedPrice(Double.valueOf(inventory.getMarkedPrice()))
                    .setSalesPrice(Double.valueOf(inventory.getSalesPrice()))
                    .setQuantity(Integer.valueOf(inventory.getQuantity()))
                    .setExpiryDate(Long.valueOf(inventory.getExpiryDate()))
                    .build();

            inventoryListProto.add(inventoryProto);
        }

        return inventoryListProto;
    }
}
