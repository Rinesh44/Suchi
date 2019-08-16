package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class InventoryStocks extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String quantity;
    private String markedPrice;
    private String salesPrice;
    private String unitId;
    private boolean synced;

    public InventoryStocks(String id, String quantity, String markedPrice, String salesPrice, String unitId, boolean synced) {
        this.id = id;
        this.quantity = quantity;
        this.markedPrice = markedPrice;
        this.salesPrice = salesPrice;
        this.unitId = unitId;
        this.synced = synced;
    }

    public InventoryStocks() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    protected InventoryStocks(Parcel in) {
        id = in.readString();
        quantity = in.readString();
        markedPrice = in.readString();
        salesPrice = in.readString();
        unitId = in.readString();
        synced = in.readByte() != 0x00;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(quantity);
        dest.writeString(markedPrice);
        dest.writeString(salesPrice);
        dest.writeString(unitId);
        dest.writeByte((byte) (synced ? 0x01 : 0x00));

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<InventoryStocks> CREATOR = new Parcelable.Creator<InventoryStocks>() {
        @Override
        public InventoryStocks createFromParcel(Parcel in) {
            return new InventoryStocks(in);
        }

        @Override
        public InventoryStocks[] newArray(int size) {
            return new InventoryStocks[size];
        }
    };
}