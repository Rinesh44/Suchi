package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SalesInventoryStock extends RealmObject {
    @PrimaryKey
    String inventoryStockId;
    String quantity;
    String salesPrice;

    public SalesInventoryStock(String inventoryStockId, String quantity, String salesPrice) {
        this.inventoryStockId = inventoryStockId;
        this.quantity = quantity;
        this.salesPrice = salesPrice;
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
