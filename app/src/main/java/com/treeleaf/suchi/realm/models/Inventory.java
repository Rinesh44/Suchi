package com.treeleaf.suchi.realm.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Inventory extends RealmObject implements Parcelable {

    @PrimaryKey
    private String inventory_id;
    private String expiryDate;
    private String skuId;
    private String user_id;
    private boolean synced;
    private StockKeepingUnit sku;
    private RealmList<InventoryStocks> inventoryStocks;

    public Inventory() {
    }

    public Inventory(String inventory_id, String expiryDate, String skuId, String user_id, boolean synced, StockKeepingUnit sku, RealmList<InventoryStocks> inventoryStocks) {
        this.inventory_id = inventory_id;
        this.expiryDate = expiryDate;
        this.skuId = skuId;
        this.user_id = user_id;
        this.synced = synced;
        this.sku = sku;
        this.inventoryStocks = inventoryStocks;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<InventoryStocks> getInventoryStocks() {
        return inventoryStocks;
    }

    public void setInventoryStocks(RealmList<InventoryStocks> inventoryStocks) {
        this.inventoryStocks = inventoryStocks;
    }

    protected Inventory(Parcel in) {
        inventory_id = in.readString();
        expiryDate = in.readString();
        skuId = in.readString();
        user_id = in.readString();
        synced = in.readByte() != 0x00;
        sku = (StockKeepingUnit) in.readValue(StockKeepingUnit.class.getClassLoader());
        if (in.readByte() == 0x01) {
            inventoryStocks = new RealmList<>();
            in.readList(inventoryStocks, InventoryStocks.class.getClassLoader());
        } else {
            inventoryStocks = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inventory_id);
        dest.writeString(expiryDate);
        dest.writeString(skuId);
        dest.writeString(user_id);
        dest.writeByte((byte) (synced ? 0x01 : 0x00));
        dest.writeValue(sku);
        if (inventoryStocks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(inventoryStocks);
        }
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