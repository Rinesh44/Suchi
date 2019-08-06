package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SubBrands extends RealmObject {
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
}
