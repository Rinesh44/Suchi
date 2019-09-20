package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Sales extends RealmObject implements Parcelable {
    @PrimaryKey
    private String saleId;
    private String totalAmount;
    private long createdAt;
    private long updatedAt;
    private boolean sync;
    private String userId;
    private boolean isCredit;
    private String creditId;
    private RealmList<SalesStock> salesStocks;

    public Sales() {
    }

    public Sales(String saleId, String totalAmount, long createdAt, long updatedAt, boolean sync, String userId, boolean isCredit, String creditId, RealmList<SalesStock> salesStocks) {
        this.saleId = saleId;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sync = sync;
        this.userId = userId;
        this.isCredit = isCredit;
        this.creditId = creditId;
        this.salesStocks = salesStocks;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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

    public RealmList<SalesStock> getSalesStocks() {
        return salesStocks;
    }

    public void setSalesStocks(RealmList<SalesStock> salesStocks) {
        this.salesStocks = salesStocks;
    }

    protected Sales(Parcel in) {
        saleId = in.readString();
        totalAmount = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        sync = in.readByte() != 0x00;
        userId = in.readString();
        isCredit = in.readByte() != 0x00;
        creditId = in.readString();
        salesStocks = (RealmList) in.readValue(RealmList.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(saleId);
        dest.writeString(totalAmount);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (sync ? 0x01 : 0x00));
        dest.writeString(userId);
        dest.writeByte((byte) (isCredit ? 0x01 : 0x00));
        dest.writeString(creditId);
        dest.writeValue(salesStocks);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Sales> CREATOR = new Parcelable.Creator<Sales>() {
        @Override
        public Sales createFromParcel(Parcel in) {
            return new Sales(in);
        }

        @Override
        public Sales[] newArray(int size) {
            return new Sales[size];
        }
    };
}
