package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TodoReminder extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private int alarm_req_code;
    private boolean alarm_status;
    private Boolean reminderEnabled;
    private Date remindDate;
    private Boolean repeatEnabled;
    private String repeatType;
    private int repeatCustom;

    public TodoReminder() {
    }

    public TodoReminder(Boolean reminderEnabled, Date remindDate, Boolean repeatEnabled, String repeatType, int repeatCustom) {
        this.reminderEnabled = reminderEnabled;
        this.remindDate = remindDate;
        this.repeatEnabled = repeatEnabled;
        this.repeatType = repeatType;
        this.repeatCustom = repeatCustom;
    }

    public TodoReminder(String id, int alarm_req_code, boolean alarm_status, Boolean reminderEnabled, Date remindDate, Boolean repeatEnabled, String repeatType, int repeatCustom) {
        this.id = id;
        this.alarm_req_code = alarm_req_code;
        this.alarm_status = alarm_status;
        this.reminderEnabled = reminderEnabled;
        this.remindDate = remindDate;
        this.repeatEnabled = repeatEnabled;
        this.repeatType = repeatType;
        this.repeatCustom = repeatCustom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAlarm_req_code() {
        return alarm_req_code;
    }

    public void setAlarm_req_code(int alarm_req_code) {
        this.alarm_req_code = alarm_req_code;
    }

    public boolean isAlarm_status() {
        return alarm_status;
    }

    public void setAlarm_status(boolean alarm_status) {
        this.alarm_status = alarm_status;
    }

    public Boolean getReminderEnabled() {
        return reminderEnabled;
    }

    public void setReminderEnabled(Boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.alarm_req_code);
        dest.writeByte(this.alarm_status ? (byte) 1 : (byte) 0);
        dest.writeValue(this.reminderEnabled);
        dest.writeLong(this.remindDate != null ? this.remindDate.getTime() : -1);
        dest.writeValue(this.repeatEnabled);
        dest.writeString(this.repeatType);
        dest.writeInt(this.repeatCustom);
    }

    protected TodoReminder(Parcel in) {
        this.id = in.readString();
        this.alarm_req_code = in.readInt();
        this.alarm_status = in.readByte() != 0;
        this.reminderEnabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpRemindDate = in.readLong();
        this.remindDate = tmpRemindDate == -1 ? null : new Date(tmpRemindDate);
        this.repeatEnabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.repeatType = in.readString();
        this.repeatCustom = in.readInt();
    }

    public static final Creator<TodoReminder> CREATOR = new Creator<TodoReminder>() {
        @Override
        public TodoReminder createFromParcel(Parcel source) {
            return new TodoReminder(source);
        }

        @Override
        public TodoReminder[] newArray(int size) {
            return new TodoReminder[size];
        }
    };
}

