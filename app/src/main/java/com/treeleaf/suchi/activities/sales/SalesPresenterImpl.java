package com.treeleaf.suchi.activities.sales;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Inventory;
import com.treeleaf.suchi.realm.models.InventoryStocks;
import com.treeleaf.suchi.realm.models.SalesStock;
import com.treeleaf.suchi.realm.models.StockKeepingUnit;
import com.treeleaf.suchi.realm.models.SubBrands;
import com.treeleaf.suchi.realm.models.Units;
import com.treeleaf.suchi.realm.repo.InventoryRepo;
import com.treeleaf.suchi.realm.repo.Repo;
import com.treeleaf.suchi.realm.repo.SalesStockRepo;
import com.treeleaf.suchi.realm.repo.UnitRepo;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;

public class SalesPresenterImpl implements SalesPresenter {
    private static final String TAG = "SalesPresenterImpl";
    private Endpoints endpoints;
    private SalesActivity activity;

    public SalesPresenterImpl(Endpoints endpoints, SalesActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void syncSalesData(String token, SuchiProto.SyncRequest syncRequest) {
        endpoints.addSales(token, syncRequest).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                activity.hideLoading();
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.sycnSalesDataFail("sales sync response null");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.sycnSalesDataFail(baseResponse.getMsg());
                    return;
                }

//                AppUtils.showLog(TAG, "inventoryResponse: " + baseResponse.getInventoriesList());
                List<Inventory> inventoryList = mapInventoryPbToModel(baseResponse.getInventoriesList());
                saveUpdatedInventories(inventoryList);
                AppUtils.showLog(TAG, "salesResponse: " + baseResponse.getSalesList());
                List<SalesStock> salesStockList = mapSalesStockPbToModel(baseResponse.getSalesList());
                saveUpdatedSalesStocks(salesStockList);
                activity.syncSalesDataSuccess();

            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.sycnSalesDataFail("Sync sales failed");
            }
        }));
    }

    private void saveUpdatedSalesStocks(List<SalesStock> salesStockList) {
        SalesStockRepo.getInstance().saveSalesStockList(salesStockList, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "sales stocks updated");
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to update sales stock");
            }
        });
    }

    private List<SalesStock> mapSalesStockPbToModel(List<SuchiProto.Sale> salesList) {
        List<SalesStock> salesStockList = new ArrayList<>();
        for (SuchiProto.Sale sale : salesList
        ) {
            for (SuchiProto.SaleInventory saleInventory : sale.getSaleInventoriesList()
            ) {

                SalesStock salesStock = new SalesStock();
                salesStock.setId(saleInventory.getInventoryStockId());
                salesStock.setQuantity(String.valueOf(saleInventory.getQuantity()));
                salesStock.setUnit(saleInventory.getUnit().getName());
                salesStock.setInventory_id(saleInventory.getInventoryId());
                salesStock.setAmount(String.valueOf(sale.getAmount()));
                salesStock.setName(saleInventory.getInventory().getSku().getName());
                salesStock.setPhotoUrl(saleInventory.getInventory().getSku().getPhoto());
                salesStock.setUnitPrice(String.valueOf(saleInventory.getAmount()));
//                salesStock.setSynced(saleInventory.getSync());
                salesStock.setCreatedAt(sale.getCreatedAt());

                salesStockList.add(salesStock);
            }
        }
        return salesStockList;
    }

    private void saveUpdatedInventories(List<Inventory> inventoryList) {
        InventoryRepo.getInstance().saveInventoryList(inventoryList, new Repo.Callback() {
            @Override
            public void success(Object o) {
                AppUtils.showLog(TAG, "Update inventory success");
            }

            @Override
            public void fail() {
                AppUtils.showLog(TAG, "failed to save updated inventories");
            }
        });
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
}
