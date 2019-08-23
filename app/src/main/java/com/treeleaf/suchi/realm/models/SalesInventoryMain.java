package com.treeleaf.suchi.realm.models;

import io.realm.annotations.PrimaryKey;

public class SalesInventoryMain {

    @PrimaryKey
    String saleInventoryId;
    String amount;
    SalesInventory salesInventory;
    String quantity;


    public SalesInventoryMain(String saleInventoryId, String amount, SalesInventory salesInventory, String quantity) {
        this.saleInventoryId = saleInventoryId;
        this.amount = amount;
        this.salesInventory = salesInventory;
        this.quantity = quantity;
    }

    public SalesInventoryMain() {
    }

    public String getSaleInventoryId() {
        return saleInventoryId;
    }

    public void setSaleInventoryId(String saleInventoryId) {
        this.saleInventoryId = saleInventoryId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public SalesInventory getSalesInventoryStock() {
        return salesInventory;
    }

    public void setSalesInventory(SalesInventory salesInventory) {
        this.salesInventory = salesInventory;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
