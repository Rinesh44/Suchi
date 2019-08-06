package com.treeleaf.suchi.realm.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    public final static String USER_ID = "userId";
    @PrimaryKey
    private String userId;
    private String storeName;
    private String address;
    private String phone;
    private String ownerName;
    private String userName;
    private String userStatus;

    public User() {
    }

    public User(String userId, String storeName, String address, String phone, String ownerName, String userName, String userStatus) {
        this.userId = userId;
        this.storeName = storeName;
        this.address = address;
        this.phone = phone;
        this.ownerName = ownerName;
        this.userName = userName;
        this.userStatus = userStatus;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}