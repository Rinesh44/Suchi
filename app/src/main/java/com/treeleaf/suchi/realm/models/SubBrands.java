package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SubBrands extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String brandId;
    private String name;

    public SubBrands(String id, String brandId, String name) {
        this.id = id;
        this.brandId = brandId;
        this.name = name;
    }

    public SubBrands() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected SubBrands(Parcel in) {
        id = in.readString();
        brandId = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(brandId);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SubBrands> CREATOR = new Parcelable.Creator<SubBrands>() {
        @Override
        public SubBrands createFromParcel(Parcel in) {
            return new SubBrands(in);
        }

        @Override
        public SubBrands[] newArray(int size) {
            return new SubBrands[size];
        }
    };
}
