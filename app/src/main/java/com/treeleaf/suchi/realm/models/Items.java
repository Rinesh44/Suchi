package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
public class Items extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String name;
    private String photo_url;
    private String code;
    private String desc;
    private String unitPrice;
    private Brands brand;
    private SubBrands subBrands;
    private Units units;
    private Categories categories;


    public Items() {
    }


    public Items(String id, String name, String photo_url, String code, String desc, String unitPrice, Brands brand, SubBrands subBrands, Units units, Categories categories) {
        this.id = id;
        this.name = name;
        this.photo_url = photo_url;
        this.code = code;
        this.desc = desc;
        this.unitPrice = unitPrice;
        this.brand = brand;
        this.subBrands = subBrands;
        this.units = units;
        this.categories = categories;
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

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Brands getBrand() {
        return brand;
    }

    public void setBrand(Brands brand) {
        this.brand = brand;
    }

    public SubBrands getSubBrands() {
        return subBrands;
    }

    public void setSubBrands(SubBrands subBrands) {
        this.subBrands = subBrands;
    }

    public Units getUnits() {
        return units;
    }

    public void setUnits(Units units) {
        this.units = units;
    }

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    protected Items(Parcel in) {
        id = in.readString();
        name = in.readString();
        photo_url = in.readString();
        code = in.readString();
        desc = in.readString();
        unitPrice = in.readString();
        brand = (Brands) in.readValue(Brands.class.getClassLoader());
        subBrands = (SubBrands) in.readValue(SubBrands.class.getClassLoader());
        units = (Units) in.readValue(Units.class.getClassLoader());
        categories = (Categories) in.readValue(Categories.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(photo_url);
        dest.writeString(code);
        dest.writeString(desc);
        dest.writeString(unitPrice);
        dest.writeValue(brand);
        dest.writeValue(subBrands);
        dest.writeValue(units);
        dest.writeValue(categories);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Items> CREATOR = new Parcelable.Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel in) {
            return new Items(in);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };
}