package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SalesInventoryStock extends RealmObject {
    @PrimaryKey
    private String inventoryStockId;
    private String inventoryId;
    private String quantity;
    private String salesPrice;

    public SalesInventoryStock(String inventoryStockId, String inventoryId, String quantity, String salesPrice) {
        this.inventoryStockId = inventoryStockId;
        this.inventoryId = inventoryId;
        this.quantity = quantity;
        this.salesPrice = salesPrice;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public SalesInventoryStock() {
    }

    public String getInventoryStockId() {
        return inventoryStockId;
    }

    public void setInventoryStockId(String inventoryStockId) {
        this.inventoryStockId = inventoryStockId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }


}
