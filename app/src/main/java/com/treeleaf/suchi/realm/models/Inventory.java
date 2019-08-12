package com.treeleaf.suchi.realm.models;


import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Inventory extends RealmObject implements Parcelable {

    @PrimaryKey
    private String inventory_id;
    private String expiryDate;
    private String markedPrice;
    private String salesPrice;
    private String quantity;
    private String user_id;
    private String skuId;
    private String unitId;
    private boolean synced;
    private StockKeepingUnit sku;

    public Inventory() {
    }

    public Inventory(String inventory_id, String expiryDate, String markedPrice, String salesPrice, String quantity, String user_id, String skuId, String unitId, boolean synced, StockKeepingUnit sku) {
        this.inventory_id = inventory_id;
        this.expiryDate = expiryDate;
        this.markedPrice = markedPrice;
        this.salesPrice = salesPrice;
        this.quantity = quantity;
        this.user_id = user_id;
        this.skuId = skuId;
        this.unitId = unitId;
        this.synced = synced;
        this.sku = sku;
    }


    public String getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMarkedPrice() {
        return markedPrice;
    }

    public void setMarkedPrice(String markedPrice) {
        this.markedPrice = markedPrice;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public StockKeepingUnit getSku() {
        return sku;
    }

    public void setSku(StockKeepingUnit sku) {
        this.sku = sku;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    protected Inventory(Parcel in) {
        inventory_id = in.readString();
        expiryDate = in.readString();
        markedPrice = in.readString();
        salesPrice = in.readString();
        quantity = in.readString();
        user_id = in.readString();
        skuId = in.readString();
        unitId = in.readString();
        synced = in.readByte() != 0x00;
        sku = (StockKeepingUnit) in.readValue(StockKeepingUnit.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inventory_id);
        dest.writeString(expiryDate);
        dest.writeString(markedPrice);
        dest.writeString(salesPrice);
        dest.writeString(quantity);
        dest.writeString(user_id);
        dest.writeString(skuId);
        dest.writeString(unitId);
        dest.writeByte((byte) (synced ? 0x01 : 0x00));
        dest.writeValue(sku);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Inventory> CREATOR = new Parcelable.Creator<Inventory>() {
        @Override
        public Inventory createFromParcel(Parcel in) {
            return new Inventory(in);
        }

        @Override
        public Inventory[] newArray(int size) {
            return new Inventory[size];
        }
    };
}