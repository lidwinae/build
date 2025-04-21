package com.example.praktikum;

import android.os.Parcel;
import android.os.Parcelable;

public class BuildItem implements Parcelable {
    private String name;
    private int imageRes;
    private boolean isAvailable;

    public BuildItem(String name, int imageRes, boolean isAvailable) {
        this.name = name;
        this.imageRes = imageRes;
        this.isAvailable = isAvailable;
    }

    protected BuildItem(Parcel in) {
        name = in.readString();
        imageRes = in.readInt();
        isAvailable = in.readByte() != 0;
    }

    public static final Creator<BuildItem> CREATOR = new Creator<BuildItem>() {
        @Override
        public BuildItem createFromParcel(Parcel in) {
            return new BuildItem(in);
        }

        @Override
        public BuildItem[] newArray(int size) {
            return new BuildItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(imageRes);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
    }
}