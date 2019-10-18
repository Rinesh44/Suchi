package com.treeleaf.suchi.realm.models;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Credit extends RealmObject {
    @PrimaryKey
    private String id;
    private String creditorId;
    private String paidAmount;
    private String balance;
    private String totalAmount;
    private String userId;
    private String creditorSignature;
    private long createdAt;
    private long updatedAt;
    private boolean sync;
    private RealmList<SalesStock> soldItems;

    public RealmList<SalesStock> getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(RealmList<SalesStock> soldItems) {
        this.soldItems = soldItems;
    }

    public Credit() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreditorId() {
        return creditorId;
    }

    public void setCreditorId(String creditorId) {
        this.creditorId = creditorId;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String dueAmount) {
        this.balance = dueAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getCreditorSignature() {
        return creditorSignature;
    }

    public void setCreditorSignature(String creditorSignature) {
        this.creditorSignature = creditorSignature;
    }
}
