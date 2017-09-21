package com.amad.messaingapp.Entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pushparajparab on 9/21/17.
 */

public class MyRegion implements Parcelable {

    private  String name;

    @Override
    public String toString() {
        return name;
    }

    public MyRegion(String _name)
    {
        this.name = _name;
    }

    protected MyRegion(Parcel in) {
        name = in.readString();
    }

    public static final Creator<MyRegion> CREATOR = new Creator<MyRegion>() {
        @Override
        public MyRegion createFromParcel(Parcel in) {
            return new MyRegion(in);
        }

        @Override
        public MyRegion[] newArray(int size) {
            return new MyRegion[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
