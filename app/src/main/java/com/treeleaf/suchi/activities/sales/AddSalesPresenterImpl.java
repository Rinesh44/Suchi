package com.treeleaf.suchi.activities.sales;

import com.treeleaf.suchi.api.Endpoints;
import com.treeleaf.suchi.entities.SuchiProto;
import com.treeleaf.suchi.realm.models.Sales;
import com.treeleaf.suchi.realm.models.SalesInventory;
import com.treeleaf.suchi.realm.models.SalesInventoryMain;
import com.treeleaf.suchi.realm.models.SalesInventoryStock;
import com.treeleaf.suchi.realm.models.SalesSku;
import com.treeleaf.suchi.rpc.SuchiRpcProto;
import com.treeleaf.suchi.utils.AppUtils;
import com.treeleaf.suchi.utils.CallbackWrapper;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;

public class AddSalesPresenterImpl implements AddSalesPresenter {
    private static final String TAG = "AddSalesPresenterImpl";
    private Endpoints endpoints;
    private AddSalesActivity activity;

    public AddSalesPresenterImpl(Endpoints endpoints, AddSalesActivity activity) {
        this.endpoints = endpoints;
        this.activity = activity;
    }

    @Override
    public void addSales(String token, SuchiProto.Sale sale) {
     /*   endpoints.addSales(token, sale).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.showMessage("Add sales failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.showMessage(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "AddSalesResponse: " + baseResponse.toString());

                activity.addSalesSuccess();
            }

            @Override
            public void onFailureResult() {
                activity.addSalesFail("failed");

            }
        }));*/
    }


    @Override
    public void getSales(String token) {
        endpoints.getSales(token).enqueue(new CallbackWrapper<>(activity, new CallbackWrapper.Wrapper<SuchiRpcProto.SuchiBaseResponse>() {
            @Override
            public void onSuccessResult(Response<SuchiRpcProto.SuchiBaseResponse> response) {
                activity.hideLoading();

                SuchiRpcProto.SuchiBaseResponse baseResponse = response.body();

                if (baseResponse == null) {
                    activity.showMessage("Get Sales failed");
                    return;
                }

                if (baseResponse.getError()) {
                    activity.showMessage(baseResponse.getMsg());
                    return;
                }

                AppUtils.showLog(TAG, "GetSalesResponse: " + baseResponse);
                List<Sales> salesList = mapSalesPbToModel(baseResponse.getSalesList());

                activity.getSalesSuccess(salesList);
            }

            @Override
            public void onFailureResult() {
                activity.hideLoading();
                activity.getSalesFail("failed");
            }
        }));
    }

    private List<Sales> mapSalesPbToModel(List<SuchiProto.Sale> salesListPb) {
        List<Sales> salesList = new ArrayList<>();
        for (SuchiProto.Sale salePb : salesListPb
        ) {
            Sales sales = new Sales();
            sales.setSaleId(salePb.getSaleId());
            sales.setAmount(String.valueOf(salePb.getAmount()));
            sales.setCreatedAt(String.valueOf(salePb.getCreatedAt()));
            sales.setSync(salePb.getSync());
            sales.setUserId(salePb.getUserId());

            List<SuchiProto.SaleInventory> saleInventoryListPb = salePb.getSaleInventoriesList();

            RealmList<SalesInventoryMain> saleInventoryMainList = new RealmList<>();
            for (SuchiProto.SaleInventory saleInventoryPb : saleInventoryListPb) {
                SalesInventoryMain salesInventoryMain = new SalesInventoryMain();
                salesInventoryMain.setAmount(String.valueOf(saleInventoryPb.getAmount()));
                salesInventoryMain.setQuantity(String.valueOf(saleInventoryPb.getQuantity()));
                salesInventoryMain.setSaleInventoryId(saleInventoryPb.getSaleInventoryId());

                SuchiProto.Inventory inventoryPb = saleInventoryPb.getInventory();
                SalesInventory inventory = new SalesInventory();
                inventory.setExpiryDate(String.valueOf(inventoryPb.getExpiryDate()));
                inventory.setInventoryId(inventoryPb.getInventoryId());

                SuchiProto.StockKeepingUnit skuPb = inventoryPb.getSku();
                SalesSku salesSku = new SalesSku();
                salesSku.setId(String.valueOf(skuPb.getId()));
                salesSku.setSku_id(skuPb.getSkuId());
                salesSku.setName(skuPb.getName());
                salesSku.setPhoto_url(skuPb.getPhoto());

                inventory.setSalesSku(salesSku);

                List<SuchiProto.InventoryStock> salesInventoryStockPb = inventoryPb.getInventoryStocksList();
                RealmList<SalesInventoryStock> salesInventoryStock = new RealmList<>();
                for (SuchiProto.InventoryStock inventoryStockPb : salesInventoryStockPb
                ) {
                    SalesInventoryStock salesInventoryStockModel = new SalesInventoryStock();
                    salesInventoryStockModel.setInventoryId(inventoryStockPb.getInventoryId());
                    salesInventoryStockModel.setInventoryStockId(inventoryStockPb.getInventoryStockId());
                    salesInventoryStockModel.setQuantity(String.valueOf(inventoryStockPb.getQuantity()));
                    salesInventoryStockModel.setSalesPrice(String.valueOf(inventoryStockPb.getSalesPrice()));

                    salesInventoryStock.add(salesInventoryStockModel);
                }

                inventory.setSalesInventoryStockList(salesInventoryStock);
                salesInventoryMain.setSalesInventory(inventory);
                saleInventoryMainList.add(salesInventoryMain);
            }

            sales.setSalesInventoryMain(saleInventoryMainList);
            salesList.add(sales);
        }

        return salesList;
    }


}
