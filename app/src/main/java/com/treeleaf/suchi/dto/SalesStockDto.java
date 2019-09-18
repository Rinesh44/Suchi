package com.treeleaf.suchi.dto;


import android.os.Parcel;
import android.os.Parcelable;

import com.treeleaf.suchi.realm.models.SalesStock;

import io.realm.annotations.PrimaryKey;

public class SalesStockDto implements Parcelable {
    @PrimaryKey
    private String id;
    private String inventory_id;
    private String amount;
    private String quantity;
    private String unit;
    private String name;
    private String photoUrl;
    private String unitPrice;
    private String brand;
    private String subBrand;
    private String categories;
    private boolean synced;
    private long createdAt;
    private long updatedAt;
    private boolean isCredit;
    private String creditId;

    public SalesStockDto(String id, String inventory_id, String amount, String quantity, String unit, String name, String photoUrl, String unitPrice, String brand,
                         String subBrand, String categories, boolean synced, long createdAt, long updatedAt,
                         boolean isCredit, String creditId) {
        this.id = id;
        this.inventory_id = inventory_id;
        this.amount = amount;
        this.quantity = quantity;
        this.unit = unit;
        this.name = name;
        this.photoUrl = photoUrl;
        this.unitPrice = unitPrice;
        this.brand = brand;
        this.subBrand = subBrand;
        this.categories = categories;
        this.synced = synced;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isCredit = isCredit;
        this.creditId = creditId;
    }


    public SalesStockDto() {
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSubBrand() {
        return subBrand;
    }

    public void setSubBrand(String subBrand) {
        this.subBrand = subBrand;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean credit) {
        isCredit = credit;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    protected SalesStockDto(Parcel in) {
        id = in.readString();
        inventory_id = in.readString();
        amount = in.readString();
        quantity = in.readString();
        unit = in.readString();
        name = in.readString();
        photoUrl = in.readString();
        unitPrice = in.readString();
        brand = in.readString();
        subBrand = in.readString();
        categories = in.readString();
        synced = in.readByte() != 0x00;
        createdAt = in.readLong();
        updatedAt = in.readLong();
        isCredit = in.readByte() != 0x00;
        creditId = in.readString();
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
        dest.writeString(name);
        dest.writeString(photoUrl);
        dest.writeString(unitPrice);
        dest.writeString(brand);
        dest.writeString(subBrand);
        dest.writeString(categories);
        dest.writeByte((byte) (synced ? 0x01 : 0x00));
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (synced ? 0x01 : 0x00));
        dest.writeString(creditId);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SalesStockDto> CREATOR = new Parcelable.Creator<SalesStockDto>() {
        @Override
        public SalesStockDto createFromParcel(Parcel in) {
            return new SalesStockDto(in);
        }

        @Override
        public SalesStockDto[] newArray(int size) {
            return new SalesStockDto[size];
        }
    };
}
