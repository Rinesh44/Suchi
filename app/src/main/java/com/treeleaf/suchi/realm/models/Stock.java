package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Stock extends RealmObject {
    @PrimaryKey
    private String sn;
    private String sku;
    private String quantity;
    private String price;
    private String unit;

    public Stock() {
    }

    public Stock(String sn, String sku, String quantity, String price, String unit) {
        this.sn = sn;
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
        this.unit = unit;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
