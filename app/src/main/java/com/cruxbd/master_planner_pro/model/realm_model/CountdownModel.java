package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CountdownModel extends RealmObject implements Parcelable {

    @PrimaryKey
    String id = UUID.randomUUID().toString();

    private String projectId;
    private String cardItemId;
    private String todoId;
    private String title;
    private String countdownFrom;
    private Date countDownTime;
    private Boolean countdownStatus;
    Date createdAt = Calendar.getInstance().getTime();





    public CountdownModel(){
    }

    //countdown for master planner
    public CountdownModel(String countdownFrom, String projectId, String cardItemId, String title, Date countDownTime,
                          Boolean countdownStatus) {
        this.countdownFrom = countdownFrom;
        this.projectId = projectId;
        this.cardItemId = cardItemId;
        this.title = title;
        this.countDownTime = countDownTime;
        this.countdownStatus = countdownStatus;
    }

    //countdown for todo
    public CountdownModel(String countdownFrom, String todoId, String title, Date countDownTime, Boolean countdownStatus) {
        this.countdownFrom = countdownFrom;
        this.todoId = todoId;
        this.title = title;
        this.countDownTime = countDownTime;
        this.countdownStatus = countdownStatus;
    }

    //countdown for normal
    public CountdownModel(String countdownFrom, String title, Date countDownTime, Boolean countdownStatus) {
        this.countdownFrom = countdownFrom;
        this.title = title;
        this.countDownTime = countDownTime;
        this.countdownStatus = countdownStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCardItemId() {
        return cardItemId;
    }

    public void setCardItemId(String cardItemId) {
        this.cardItemId = cardItemId;
    }

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountdownFrom() {
        return countdownFrom;
    }

    public void setCountdownFrom(String countdownFrom) {
        this.countdownFrom = countdownFrom;
    }

    public Date getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(Date countDownTime) {
        this.countDownTime = countDownTime;
    }

    public Boolean getCountdownStatus() {
        return countdownStatus;
    }

    public void setCountdownStatus(Boolean countdownStatus) {
        this.countdownStatus = countdownStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.projectId);
        dest.writeString(this.cardItemId);
        dest.writeString(this.todoId);
        dest.writeString(this.title);
        dest.writeString(this.countdownFrom);
        dest.writeLong(this.countDownTime != null ? this.countDownTime.getTime() : -1);
        dest.writeValue(this.countdownStatus);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected CountdownModel(Parcel in) {
        this.id = in.readString();
        this.projectId = in.readString();
        this.cardItemId = in.readString();
        this.todoId = in.readString();
        this.title = in.readString();
        this.countdownFrom = in.readString();
        long tmpCountDownTime = in.readLong();
        this.countDownTime = tmpCountDownTime == -1 ? null : new Date(tmpCountDownTime);
        this.countdownStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Creator<CountdownModel> CREATOR = new Creator<CountdownModel>() {
        @Override
        public CountdownModel createFromParcel(Parcel source) {
            return new CountdownModel(source);
        }

        @Override
        public CountdownModel[] newArray(int size) {
            return new CountdownModel[size];
        }
    };
}
