package com.treeleaf.suchi.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.treeleaf.suchi.realm.models.InventoryStocks;

import java.util.List;

import io.realm.RealmList;

public class InventoryDto implements Parcelable {

    private String inventory_id;
    private String expiryDate;
    private String skuId;
    private String user_id;
    private boolean synced;
    private StockKeepingUnitDto sku;
    private RealmList<InventoryStocksDto> inventoryStocks;

    public InventoryDto() {
    }

    public InventoryDto(String inventory_id, String expiryDate, String skuId, String user_id, boolean synced, StockKeepingUnitDto sku, RealmList<InventoryStocksDto> inventoryStocks) {
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


    public StockKeepingUnitDto getSku() {
        return sku;
    }

    public void setSku(StockKeepingUnitDto sku) {
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

    public List<InventoryStocksDto> getInventoryStocks() {
        return inventoryStocks;
    }

    public void setInventoryStocks(RealmList<InventoryStocksDto> inventoryStocks) {
        this.inventoryStocks = inventoryStocks;
    }

    protected InventoryDto(Parcel in) {
        inventory_id = in.readString();
        expiryDate = in.readString();
        skuId = in.readString();
        user_id = in.readString();
        synced = in.readByte() != 0x00;
        sku = (StockKeepingUnitDto) in.readValue(StockKeepingUnitDto.class.getClassLoader());
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
    public static final Parcelable.Creator<InventoryDto> CREATOR = new Parcelable.Creator<InventoryDto>() {
        @Override
        public InventoryDto createFromParcel(Parcel in) {
            return new InventoryDto(in);
        }

        @Override
        public InventoryDto[] newArray(int size) {
            return new InventoryDto[size];
        }
    };
}
