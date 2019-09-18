package com.treeleaf.suchi.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class CreditorsDto implements Parcelable {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private String pic;
    private String userId;
    private long createdAt;
    private long updatedAt;
    private boolean sync;

    public CreditorsDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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


    protected CreditorsDto(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        description = in.readString();
        pic = in.readString();
        userId = in.readString();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        sync = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(description);
        dest.writeString(pic);
        dest.writeString(userId);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeByte((byte) (sync ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CreditorsDto> CREATOR = new Parcelable.Creator<CreditorsDto>() {
        @Override
        public CreditorsDto createFromParcel(Parcel in) {
            return new CreditorsDto(in);
        }

        @Override
        public CreditorsDto[] newArray(int size) {
            return new CreditorsDto[size];
        }
    };
}
