package com.treeleaf.suchi.realm.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Units extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id;
    private String name;

    public Units() {
    }

    public Units(String id, String name) {
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

    protected Units(Parcel in) {
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
    public static final Parcelable.Creator<Units> CREATOR = new Parcelable.Creator<Units>() {
        @Override
        public Units createFromParcel(Parcel in) {
            return new Units(in);
        }

        @Override
        public Units[] newArray(int size) {
            return new Units[size];
        }
    };
}
