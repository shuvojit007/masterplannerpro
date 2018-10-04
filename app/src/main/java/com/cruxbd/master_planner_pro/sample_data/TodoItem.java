package com.cruxbd.master_planner_pro.sample_data;

/**
 * Created by Mridul on 11-Apr-18.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TodoItem implements Parcelable {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("done")
    @Expose
    private Boolean done;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("task")
    @Expose
    private String task;
    @SerializedName("list")
    @Expose
    private String list;

    public TodoItem() {
    }

    public TodoItem(Location location, Boolean done, Boolean deleted, String createdAt, String id, String userId, String task, String list) {
        this.location = location;
        this.done = done;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.id = id;
        this.userId = userId;
        this.task = task;
        this.list = list;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }



    @Override
    public String toString() {
        return "TodoItem{" +
                "location=" + location +
                ", done=" + done +
                ", deleted=" + deleted +
                ", createdAt='" + createdAt + '\'' +
                ", id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", task='" + task + '\'' +
                ", list='" + list + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, flags);
        dest.writeValue(this.done);
        dest.writeValue(this.deleted);
        dest.writeString(this.createdAt);
        dest.writeString(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.task);
        dest.writeString(this.list);
    }

    protected TodoItem(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.done = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.deleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.createdAt = in.readString();
        this.id = in.readString();
        this.userId = in.readString();
        this.task = in.readString();
        this.list = in.readString();
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
