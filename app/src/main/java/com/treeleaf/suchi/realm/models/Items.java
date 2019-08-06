package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Items extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String photo_url;
    private String code;
    private String desc;
    private String unitPrice;
    private String category;
    private Brands brand;
    private SubBrands subBrands;
    private Units units;


    public Items() {
    }


    public Items(String id, String name, String photo_url, String code, String desc, String unitPrice, String category, Brands brand, SubBrands subBrands, Units units) {
        this.id = id;
        this.name = name;
        this.photo_url = photo_url;
        this.code = code;
        this.desc = desc;
        this.unitPrice = unitPrice;
        this.category = category;
        this.brand = brand;
        this.subBrands = subBrands;
        this.units = units;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
