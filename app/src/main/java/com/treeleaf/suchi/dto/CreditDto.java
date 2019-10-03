package com.treeleaf.suchi.dto;


import android.os.Parcel;
import android.os.Parcelable;

import com.treeleaf.suchi.realm.models.SalesStock;

import io.realm.RealmList;

public class CreditDto implements Parcelable {
    private String id;
    private String creditorId;
    private String paidAmount;
    private String dueAmount;
    private String totalAmount;
    private String userId;
    private String creditorSignature;
    private long createdAt;
    private long updatedAt;
    private boolean sync;
    private RealmList<SalesStock> soldItems;

    public CreditDto() {
    }


    public RealmList<SalesStock> getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(RealmList<SalesStock> soldItems) {
        this.soldItems = soldItems;
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

    public String getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(String dueAmount) {
        this.dueAmount = dueAmount;
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

    protected CreditDto(Parcel in) {
        id = in.readString();
        creditorId = in.readString();
        paidAmount = in.readString();
        dueAmount = in.readString();
        totalAmount = in.readString();
        userId = in.readString();
        creditorSignature = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        sync = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            soldItems = new RealmList<SalesStock>();
            in.readList(soldItems, SalesStock.class.getClassLoader());
        } else {
            soldItems = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(creditorId);
        dest.writeString(paidAmount);
        dest.writeString(dueAmount);
        dest.writeString(totalAmount);
        dest.writeString(userId);
        dest.writeString(creditorSignature);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (sync ? 0x01 : 0x00));
        if (soldItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(soldItems);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CreditDto> CREATOR = new Parcelable.Creator<CreditDto>() {
        @Override
        public CreditDto createFromParcel(Parcel in) {
            return new CreditDto(in);
        }

        @Override
        public CreditDto[] newArray(int size) {
            return new CreditDto[size];
        }
    };
}