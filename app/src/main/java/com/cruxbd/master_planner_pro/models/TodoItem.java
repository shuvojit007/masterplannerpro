package com.cruxbd.master_planner_pro.models;

/**
 * Created by Mridul on 11-Apr-18.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;


import java.util.Date;
import java.util.UUID;

public class TodoItem implements Parcelable {

    private String id ;

    private String userId;
    private String task;
    private String note;
    private Date dueDate;

    private Date remindDate;
    private Boolean repeatEnabled;
    private String repeatType; //Once,EveryDay,...
    //private RealmList<String> repeatWeekDays;
    private int repeatCustom;
    private int alarm_req_code;
    private boolean alarm_status;

    private String list;
    private int priority;
    private boolean locked;
    private String locationName;
    private Double lat;
    private Double lng;
    private Boolean done;
    private Boolean deleted;
    private Boolean hasCountdown;
    private CountdownModel countdown;
    private Date createdAt;

    public TodoItem() {

    }

    public TodoItem(String id, String userId, String task, String note, Date dueDate, Date remindDate, Boolean repeatEnabled, String repeatType, int repeatCustom, int alarm_req_code, boolean alarm_status, String list, int priority, boolean locked, String locationName, Double lat, Double lng, Boolean done, Boolean deleted, Boolean hasCountdown, CountdownModel countdown, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.task = task;
        this.note = note;
        this.dueDate = dueDate;
        this.remindDate = remindDate;
        this.repeatEnabled = repeatEnabled;
        this.repeatType = repeatType;
        this.repeatCustom = repeatCustom;
        this.alarm_req_code = alarm_req_code;
        this.alarm_status = alarm_status;
        this.list = list;
        this.priority = priority;
        this.locked = locked;
        this.locationName = locationName;
        this.lat = lat;
        this.lng = lng;
        this.done = done;
        this.deleted = deleted;
        this.hasCountdown = hasCountdown;
        this.countdown = countdown;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public Boolean getRepeatEnabled() {
        return repeatEnabled;
    }

    public void setRepeatEnabled(Boolean repeatEnabled) {
        this.repeatEnabled = repeatEnabled;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public int getRepeatCustom() {
        return repeatCustom;
    }

    public void setRepeatCustom(int repeatCustom) {
        this.repeatCustom = repeatCustom;
    }

    public int getAlarm_req_code() {
        return alarm_req_code;
    }

    public void setAlarm_req_code(int alarm_req_code) {
        this.alarm_req_code = alarm_req_code;
    }

    public boolean getAlarm_status() {
        return alarm_status;
    }

    public void setAlarm_status(boolean alarm_status) {
        this.alarm_status = alarm_status;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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

    public Boolean getHasCountdown() {
        return hasCountdown;
    }

    public void setHasCountdown(Boolean hasCountdown) {
        this.hasCountdown = hasCountdown;
    }

    public CountdownModel getCountdown() {
        return countdown;
    }

    public void setCountdown(CountdownModel countdown) {
        this.countdown = countdown;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", task='" + task + '\'' +
                ", note='" + note + '\'' +
                ", dueDate=" + dueDate +
                ", remindDate=" + remindDate +
                ", repeatEnabled=" + repeatEnabled +
                ", repeatType='" + repeatType + '\'' +
                ", repeatCustom=" + repeatCustom +
                ", alarm_req_code=" + alarm_req_code +
                ", alarm_status=" + alarm_status +
                ", list='" + list + '\'' +
                ", priority=" + priority +
                ", locked=" + locked +
                ", locationName='" + locationName + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", done=" + done +
                ", deleted=" + deleted +
                ", hasCountdown=" + hasCountdown +
                ", countdown=" + countdown +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.task);
        dest.writeString(this.note);
        dest.writeLong(this.dueDate != null ? this.dueDate.getTime() : -1);
        dest.writeLong(this.remindDate != null ? this.remindDate.getTime() : -1);
        dest.writeValue(this.repeatEnabled);
        dest.writeString(this.repeatType);
        dest.writeInt(this.repeatCustom);
        dest.writeInt(this.alarm_req_code);
        dest.writeByte(this.alarm_status ? (byte) 1 : (byte) 0);
        dest.writeString(this.list);
        dest.writeInt(this.priority);
        dest.writeByte(this.locked ? (byte) 1 : (byte) 0);
        dest.writeString(this.locationName);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeValue(this.done);
        dest.writeValue(this.deleted);
        dest.writeValue(this.hasCountdown);
        dest.writeParcelable(this.countdown, flags);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected TodoItem(Parcel in) {
        this.id = in.readString();
        this.userId = in.readString();
        this.task = in.readString();
        this.note = in.readString();
        long tmpDueDate = in.readLong();
        this.dueDate = tmpDueDate == -1 ? null : new Date(tmpDueDate);
        long tmpRemindDate = in.readLong();
        this.remindDate = tmpRemindDate == -1 ? null : new Date(tmpRemindDate);
        this.repeatEnabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.repeatType = in.readString();
        this.repeatCustom = in.readInt();
        this.alarm_req_code = in.readInt();
        this.alarm_status = in.readByte() != 0;
        this.list = in.readString();
        this.priority = in.readInt();
        this.locked = in.readByte() != 0;
        this.locationName = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.done = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.deleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.hasCountdown = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.countdown = in.readParcelable(CountdownModel.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
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
