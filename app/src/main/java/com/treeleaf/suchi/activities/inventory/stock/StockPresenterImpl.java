package com.treeleaf.suchi.activities.inventory.stock;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.InventoryProto;
import com.treeleaf.suchi.entities.ReqResProto;
import com.treeleaf.suchi.realm.models.Brands;
import com.treeleaf.suchi.realm.models.Categories;
import com.treeleaf.suchi.realm.models.Items;
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

                List<Items> itemsList = mapInventoryPbToModel(baseResponse.getInventoriesList());
                activity.getStockItemsSuccess(itemsList);
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.getStockItemsFail("failed");

            }
        }));
    }

    private List<Items> mapInventoryPbToModel(List<InventoryProto.Inventory> inventoriesList) {
        List<Items> itemsList = new ArrayList<>();
        for (InventoryProto.Inventory inventory : inventoriesList
        ) {
            Items items = new Items();
            items.setId(inventory.getInventoryId());
            items.setName(inventory.getSku().getName());
            items.setPhoto_url(inventory.getSku().getPhoto());
            items.setCode(inventory.getSku().getCode());
            items.setDesc(inventory.getSku().getDescription());
            items.setUnitPrice(String.valueOf(inventory.getSku().getUnitPrice()));
            items.setQuantity(String.valueOf(inventory.getQuantity()));
            items.setMarkedPrice(String.valueOf(inventory.getMarkedPrice()));
            items.setSellingPrice(String.valueOf(inventory.getSalesPrice()));
            items.setExpiryDate(String.valueOf(inventory.getExpiryDate()));

            InventoryProto.Brand brandPb = inventory.getSku().getBrand();
            Brands brands = new Brands(brandPb.getBrandId(), brandPb.getName());
            items.setBrand(brands);

            InventoryProto.SubBrand subBrandPb = inventory.getSku().getSubBrand();
            SubBrands subBrands = new SubBrands(subBrandPb.getSubBrandId(), subBrandPb.getBrandId(),
                    subBrandPb.getName());
            items.setSubBrands(subBrands);

            InventoryProto.Unit unitPb = inventory.getSku().getUnit();
            Units units = new Units(unitPb.getUnitId(), unitPb.getName());
            items.setUnits(units);

            InventoryProto.Category categoryPb = inventory.getSku().getCategory();
            Categories categories = new Categories(categoryPb.getCategoryId(), categoryPb.getName());
            items.setCategories(categories);

            itemsList.add(items);

        }

        return itemsList;
    }
}
