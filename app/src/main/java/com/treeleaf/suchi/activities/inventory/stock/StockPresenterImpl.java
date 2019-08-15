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

public class StockPresenterImpl implements StockPresenter {
    private static final String TAG = "StockPresenterImpl";
    private StockActivity activity;
    private Endpoints endpoints;

    public StockPresenterImpl(StockActivity activity, Endpoints endpoints) {
        this.activity = activity;
        this.endpoints = endpoints;
    }


    @Override
    public void addUnsyncedInventories(String token, List<Inventory> inventoryList) {
        List<InventoryProto.Inventory> inventoryListProto = mapInventoryModelToProto(inventoryList);

        InventoryProto.SyncRequest syncRequest = InventoryProto.SyncRequest.newBuilder()
                .addAllInventories(inventoryListProto)
                .build();

        endpoints.addUnSyncedInventories(token, syncRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<ReqResProto.Response>() {
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

                List<Inventory> inventoryList = mapInventoryPbToModel(baseResponse.getInventoriesList());
                activity.addUnsyncedInventoriesSuccess(inventoryList);
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.addUnsyncedInventoriesFail("sync inventory failed");
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

            List<InventoryProto.InventoryStock> inventoryStockPbList = inventoryPb.getInventoryStocksList();
            RealmList<InventoryStocks> inventoryStocksRealmList = new RealmList<>();
            for (InventoryProto.InventoryStock inventoryStockPb : inventoryStockPbList
            ) {
                InventoryStocks inventoryStocks = new InventoryStocks(String.valueOf(inventoryStockPb.getQuantity()),
                        String.valueOf(inventoryStockPb.getMarkedPrice()), String.valueOf(inventoryStockPb.getSalesPrice()),
                        String.valueOf(inventoryStockPb.getUnitId()), inventoryStockPb.getSync());

                inventoryStocksRealmList.add(inventoryStocks);
                inventory.setInventoryStocks(inventoryStocksRealmList);
            }

            inventoryList.add(inventory);

        }

        return inventoryList;
    }

    private List<InventoryProto.Inventory> mapInventoryModelToProto(List<Inventory> inventoryList) {
        List<InventoryProto.Inventory> inventoryListProto = new ArrayList<>();
        for (Inventory inventory : inventoryList
        ) {


            List<InventoryProto.InventoryStock> inventoryStockPbList = new ArrayList<>();
            for (InventoryStocks inventoryStocks : inventory.getInventoryStocks()
            ) {
                InventoryProto.InventoryStock inventoryStockPb = InventoryProto.InventoryStock.newBuilder()
                        .setMarkedPrice(Double.valueOf(inventoryStocks.getMarkedPrice()))
                        .setSalesPrice(Double.valueOf(inventoryStocks.getSalesPrice()))
                        .setQuantity(Integer.valueOf(inventoryStocks.getQuantity()))
                        .setUnitId(inventoryStocks.getUnitId())
                        .build();

                inventoryStockPbList.add(inventoryStockPb);
            }

            InventoryProto.Inventory inventoryProto = InventoryProto.Inventory.newBuilder()
                    .setInventoryId(inventory.getInventory_id())
                    .setSkuId(inventory.getSkuId())
                    .setUserId(inventory.getUser_id())
                    .addAllInventoryStocks(inventoryStockPbList)
                    .setStatus(InventoryProto.SKUStatus.AVAILABLE)
                    .setExpiryDate(Long.valueOf(inventory.getExpiryDate()))
                    .build();

            inventoryListProto.add(inventoryProto);
        }

        return inventoryListProto;
    }
}
