package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MasterPlannerProject extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String projectName;
    private int cardCount;
    private RealmList<Card> card;
    private Date createdAt;

    public MasterPlannerProject(){

    }

    public MasterPlannerProject(String projectName, int pageCount, RealmList<Card> card, Date createdAt) {
        this.projectName = projectName;
        this.cardCount = pageCount;
        this.card = card;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

    public RealmList<Card> getCard() {
        return card;
    }

    public void setCard(RealmList<Card> card) {
        this.card = card;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "MasterPlannerProject{" +
                "id='" + id + '\'' +
                ", projectName='" + projectName + '\'' +
                ", cardCount=" + cardCount +
                ", card=" + card +
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
        dest.writeString(this.projectName);
        dest.writeInt(this.cardCount);
        dest.writeTypedList(this.card);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
    }

    protected MasterPlannerProject(Parcel in) {
        this.id = in.readString();
        this.projectName = in.readString();
        this.cardCount = in.readInt();
        this.card = new RealmList<>();
        this.card.addAll(in.createTypedArrayList(Card.CREATOR));
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
    }

    public static final Creator<MasterPlannerProject> CREATOR = new Creator<MasterPlannerProject>() {
        @Override
        public MasterPlannerProject createFromParcel(Parcel source) {
            return new MasterPlannerProject(source);
        }

        @Override
        public MasterPlannerProject[] newArray(int size) {
            return new MasterPlannerProject[size];
        }
    };
}
