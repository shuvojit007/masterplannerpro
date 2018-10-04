package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;
import io.realm.annotations.Required;

public class LocationModel extends RealmObject implements Parcelable {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String key;
    private double lat;
    private double lng;
    private String PlaceName;
    private float radius;

    private String todo;
    private String notifyWhen;
    private String actionType;
    private String actionPhone;
    private String actionMessage;
    private Date validity;

    public LocationModel() {
    }

    public LocationModel(double lat, double lng, String placeName, float radius, String todo, String key) {
        this.lat = lat;
        this.lng = lng;
        this.PlaceName = placeName;
        this.radius = radius;
        this.todo = todo;
        this.key = key;
    }

    @Override
    public String toString() {
        return "LocationModel{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", PlaceName='" + PlaceName + '\'' +
                ", radius=" + radius +
                ", todo='" + todo + '\'' +
                ", notifyWhen='" + notifyWhen + '\'' +
                ", actionType='" + actionType + '\'' +
                ", actionPhone='" + actionPhone + '\'' +
                ", actionMessage='" + actionMessage + '\'' +
                ", validity=" + validity +
                '}';
    }

    public LocationModel(double lat, double lng, String placeName, float radius, String todo, String notifyWhen, String actionType, String actionPhone, String actionMessage, Date validity, String key) {
        this.lat = lat;
        this.lng = lng;
        this.PlaceName = placeName;
        this.radius = radius;
        this.todo = todo;
        this.notifyWhen = notifyWhen;
        this.actionType = actionType;
        this.actionPhone = actionPhone;
        this.actionMessage = actionMessage;
        this.validity = validity;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getNotifyWhen() {
        return notifyWhen;
    }

    public void setNotifyWhen(String notifyWhen) {
        this.notifyWhen = notifyWhen;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionPhone() {
        return actionPhone;
    }

    public void setActionPhone(String actionPhone) {
        this.actionPhone = actionPhone;
    }

    public String getActionMessage() {
        return actionMessage;
    }

    public void setActionMessage(String actionMessage) {
        this.actionMessage = actionMessage;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.PlaceName);
        dest.writeFloat(this.radius);
        dest.writeString(this.todo);
        dest.writeString(this.notifyWhen);
        dest.writeString(this.actionType);
        dest.writeString(this.actionPhone);
        dest.writeString(this.actionMessage);
        dest.writeLong(this.validity != null ? this.validity.getTime() : -1);
        dest.writeString(this.key);
    }

    protected LocationModel(Parcel in) {
        this.id = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.PlaceName = in.readString();
        this.radius = in.readFloat();
        this.todo = in.readString();
        this.notifyWhen = in.readString();
        this.actionType = in.readString();
        this.actionPhone = in.readString();
        this.actionMessage = in.readString();
        long tmpValidity = in.readLong();
        this.validity = tmpValidity == -1 ? null : new Date(tmpValidity);
        this.key = in.readString();
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel source) {
            return new LocationModel(source);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };
}
