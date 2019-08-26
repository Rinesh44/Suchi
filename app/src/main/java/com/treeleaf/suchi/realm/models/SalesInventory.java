package com.treeleaf.suchi.realm.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SalesInventory extends RealmObject {

    @PrimaryKey
    String inventoryId;
    String expiryDate;
    RealmList<SalesInventoryStock> salesInventoryStockList;
    SalesSku salesSku;

    public SalesInventory(String inventoryId, String expiryDate, RealmList<SalesInventoryStock> salesInventoryStockList,
                          SalesSku salesSku) {
        this.inventoryId = inventoryId;
        this.expiryDate = expiryDate;
        this.salesInventoryStockList = salesInventoryStockList;
        this.salesSku = salesSku;
    }

    public SalesInventory() {

    }


    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<SalesInventoryStock> getSalesInventoryStockList() {
        return salesInventoryStockList;
    }

    public SalesSku getSalesSku() {
        return salesSku;
    }

    public void setSalesSku(SalesSku salesSku) {
        this.salesSku = salesSku;
    }

    public void setSalesInventoryStockList(RealmList<SalesInventoryStock> salesInventoryStockList) {
        this.salesInventoryStockList = salesInventoryStockList;
    }


}
