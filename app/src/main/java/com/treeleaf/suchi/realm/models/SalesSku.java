package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SalesSku extends RealmObject {
    @PrimaryKey
    String id;
    String sku_id;
    String name;
    String photo_url;


    public SalesSku(String id, String sku_id, String name, String photo_url) {
        this.id = id;
        this.sku_id = sku_id;
        this.name = name;
        this.photo_url = photo_url;
    }

    public SalesSku() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku_id() {
        return sku_id;
    }

    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
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


}
