package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Brands extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String name;

    public Brands() {
    }

    public Brands(String id, String name) {
        this.id = id;
        this.name = name;
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

    protected Brands(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Brands> CREATOR = new Parcelable.Creator<Brands>() {
        @Override
        public Brands createFromParcel(Parcel in) {
            return new Brands(in);
        }

        @Override
        public Brands[] newArray(int size) {
            return new Brands[size];
        }
    };
}