package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SalesStock extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String inventory_id;
    private String amount;
    private String quantity;
    private String unit;

    public SalesStock(String id, String inventory_id, String amount, String quantity, String unit) {
        this.id = id;
        this.inventory_id = inventory_id;
        this.amount = amount;
        this.quantity = quantity;
        this.unit = unit;
    }


    public SalesStock() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    protected SalesStock(Parcel in) {
        id = in.readString();
        inventory_id = in.readString();
        amount = in.readString();
        quantity = in.readString();
        unit = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(inventory_id);
        dest.writeString(amount);
        dest.writeString(quantity);
        dest.writeString(unit);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SalesStock> CREATOR = new Parcelable.Creator<SalesStock>() {
        @Override
        public SalesStock createFromParcel(Parcel in) {
            return new SalesStock(in);
        }

        @Override
        public SalesStock[] newArray(int size) {
            return new SalesStock[size];
        }
    };
}
