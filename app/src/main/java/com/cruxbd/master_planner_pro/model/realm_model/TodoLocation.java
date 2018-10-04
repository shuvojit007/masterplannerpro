package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TodoLocation extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String locationName;
    private Double lat;
    private Double lng;
    private int radious;


    public TodoLocation() {
    }

    public TodoLocation(String locationName, Double lat, Double lng, int radious) {
        this.locationName = locationName;
        this.lat = lat;
        this.lng = lng;
        this.radious = radious;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getRadious() {
        return radious;
    }

    public void setRadious(int radious) {
        this.radious = radious;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.locationName);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeInt(this.radious);
    }

    protected TodoLocation(Parcel in) {
        this.id = in.readString();
        this.locationName = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.radious = in.readInt();
    }

    public static final Parcelable.Creator<TodoLocation> CREATOR = new Parcelable.Creator<TodoLocation>() {
        @Override
        public TodoLocation createFromParcel(Parcel source) {
            return new TodoLocation(source);
        }

        @Override
        public TodoLocation[] newArray(int size) {
            return new TodoLocation[size];
        }
    };

}
