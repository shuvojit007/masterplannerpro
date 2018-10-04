package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CardItems extends RealmObject implements Parcelable {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String cardId;
    private String description;
    private String billFor;
    private Double amount;
    private TodoReminder reminder;
    private String list;
    private Integer priority;
    private Boolean locked;
    private TodoLocation location;
    private Boolean done;
    private Boolean deleted;
    private Boolean hasCountdown;
    private CountdownModel countdown;
    private Date createdAt;

    public CardItems() {

    }

    public CardItems(String cardId, String description, String billFor, Double amount, TodoReminder reminder, String list, Integer priority, Boolean locked, TodoLocation location, Boolean done, Boolean deleted, Boolean hasCountdown, CountdownModel countdown, Date createdAt) {
        this.cardId = cardId;
        this.description = description;
        this.billFor = billFor;
        this.amount = amount;
        this.reminder = reminder;
        this.list = list;
        this.priority = priority;
        this.locked = locked;
        this.location = location;
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBillFor() {
        return billFor;
    }

    public void setBillFor(String billFor) {
        this.billFor = billFor;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TodoReminder getReminder() {
        return reminder;
    }

    public void setReminder(TodoReminder reminder) {
        this.reminder = reminder;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public TodoLocation getLocation() {
        return location;
    }

    public void setLocation(TodoLocation location) {
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

    public static Creator<CardItems> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.cardId);
        dest.writeString(this.description);
        dest.writeString(this.billFor);
        dest.writeValue(this.amount);
        dest.writeParcelable(this.reminder, flags);
        dest.writeString(this.list);
        dest.writeValue(this.priority);
        dest.writeValue(this.locked);
        dest.writeParcelable(this.location, flags);
        dest.writeValue(this.done);
        dest.writeValue(this.deleted);
        dest.writeValue(this.hasCountdown);
        dest.writeParcelable(this.countdown, flags);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected CardItems(Parcel in) {
        this.id = in.readString();
        this.cardId = in.readString();
        this.description = in.readString();
        this.billFor = in.readString();
        this.amount = (Double) in.readValue(Double.class.getClassLoader());
        this.reminder = in.readParcelable(TodoReminder.class.getClassLoader());
        this.list = in.readString();
        this.priority = (Integer) in.readValue(Integer.class.getClassLoader());
        this.locked = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.location = in.readParcelable(TodoLocation.class.getClassLoader());
        this.done = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.deleted = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.hasCountdown = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.countdown = in.readParcelable(CountdownModel.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Parcelable.Creator<CardItems> CREATOR = new Parcelable.Creator<CardItems>() {
        @Override
        public CardItems createFromParcel(Parcel source) {
            return new CardItems(source);
        }

        @Override
        public CardItems[] newArray(int size) {
            return new CardItems[size];
        }
    };
}
