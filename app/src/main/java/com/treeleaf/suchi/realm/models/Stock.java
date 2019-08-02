package com.treeleaf.suchi.realm.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "stock_table")
public class Stock {

    @NonNull
    @PrimaryKey
    private String sn;
    private String sku;
    private String quantity;
    private String price;

    @Ignore
    public Stock() {
    }

    public Stock(String sn, String sku, String quantity, String price) {
        this.sn = sn;
        this.sku = sku;
        this.quantity = quantity;
        this.price = price;
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
}
