package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
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
        List<SuchiProto.Inventory> inventoryListProto = mapInventoryModelToProto(inventoryList);

        SuchiProto.SyncRequest syncRequest = SuchiProto.SyncRequest.newBuilder()
                .addAllInventories(inventoryListProto)
                .build();

        endpoints.addUnSyncedInventories(token, syncRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                activity.hideLoading();
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

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

    private List<Inventory> mapInventoryPbToModel(List<SuchiProto.Inventory> inventoriesListPb) {
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
            stockKeepingUnit.setDefaultUnit(stockKeepingUnitPb.getDefaultUnit().getName());


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
                AppUtils.showLog(TAG, "unitIdResponse: " + inventoryStockPb.getUnit().getUnitId());
                AppUtils.showLog(TAG, "quantityResponse: " + inventoryStockPb.getQuantity());
                InventoryStocks inventoryStocks = new InventoryStocks(inventoryStockPb.getInventoryStockId(), inventoryStockPb.getInventoryId(), String.valueOf(inventoryStockPb.getQuantity()),
                        String.valueOf(inventoryStockPb.getMarkedPrice()), String.valueOf(inventoryStockPb.getSalesPrice()),
                        String.valueOf(inventoryStockPb.getUnit().getUnitId()), inventoryStockPb.getSync());

                inventoryStocksRealmList.add(inventoryStocks);
                inventory.setInventoryStocks(inventoryStocksRealmList);
            }

            inventoryList.add(inventory);

        }

        return inventoryList;
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


    private List<SuchiProto.Inventory> mapInventoryModelToProto(List<Inventory> inventoryList) {
        List<SuchiProto.Inventory> inventoryListProto = new ArrayList<>();
        for (Inventory inventory : inventoryList
        ) {
            List<SuchiProto.InventoryStock> inventoryStockPbList = new ArrayList<>();
            for (InventoryStocks inventoryStocks : inventory.getInventoryStocks()
            ) {
                AppUtils.showLog(TAG, "unitId: " + inventoryStocks.getUnitId());
                AppUtils.showLog(TAG, "quantity: " + inventoryStocks.getQuantity());
                SuchiProto.InventoryStock inventoryStockPb = SuchiProto.InventoryStock.newBuilder()
                        .setMarkedPrice(Double.valueOf(inventoryStocks.getMarkedPrice()))
                        .setSalesPrice(Double.valueOf(inventoryStocks.getSalesPrice()))
                        .setQuantity(Integer.valueOf(inventoryStocks.getQuantity()))
                        .setUnitId(inventoryStocks.getUnitId())
                        .build();

                inventoryStockPbList.add(inventoryStockPb);
            }

            SuchiProto.Inventory inventoryProto = SuchiProto.Inventory.newBuilder()
                    .setInventoryId(inventory.getInventory_id())
                    .setSkuId(inventory.getSkuId())
                    .setUserId(inventory.getUser_id())
                    .addAllInventoryStocks(inventoryStockPbList)
                    .setStatus(SuchiProto.SKUStatus.AVAILABLE)
                    .setExpiryDate(Long.valueOf(inventory.getExpiryDate()))
                    .build();

            inventoryListProto.add(inventoryProto);
        }

        return inventoryListProto;
    }
}
