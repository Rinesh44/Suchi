package com.treeleaf.suchi.realm.models;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sales extends RealmObject {
    @PrimaryKey
    String saleId;
    String amount;
    String createdAt;
    RealmList<SalesInventoryMain> salesInventoryMain;
    boolean sync;
    String userId;

    public Sales(String saleId, String amount, String createdAt, RealmList<SalesInventoryMain> salesInventoryMain, boolean sync, String userId) {
        this.saleId = saleId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.salesInventoryMain = salesInventoryMain;
        this.sync = sync;
        this.userId = userId;
    }

    public Sales() {
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<SalesInventoryMain> getSalesInventoryMain() {
        return salesInventoryMain;
    }

    public void setSalesInventoryMain(RealmList<SalesInventoryMain> salesInventoryMain) {
        this.salesInventoryMain = salesInventoryMain;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
