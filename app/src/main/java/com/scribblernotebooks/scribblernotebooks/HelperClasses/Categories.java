package com.scribblernotebooks.scribblernotebooks.HelperClasses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jibin_ism on 22-Jun-15.
 */
public class Categories implements Parcelable{

    private String id,name;

    public Categories() {
    }

    public Categories(String id, String name) {
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

    private Categories(Parcel in){
        this.id=in.readString();
        this.name=in.readString();
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

    public static final Parcelable.Creator<Categories> CREATOR = new Parcelable.Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel source) {
            return new Categories(source);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };
}
