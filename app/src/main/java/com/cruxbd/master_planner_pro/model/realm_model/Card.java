package com.cruxbd.master_planner_pro.model.realm_model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Card extends RealmObject implements Parcelable {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String projectId;
    private String name;
    private RealmList<CardItems> cardItems;

    public Card() {

    }

    public Card(String projectId, String name, RealmList<CardItems> cardItems) {
        this.projectId = projectId;
        this.name = name;
        this.cardItems = cardItems;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<CardItems> getCardItems() {
        return cardItems;
    }

    public void setCardItems(RealmList<CardItems> cardItems) {
        this.cardItems = cardItems;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.projectId);
        dest.writeString(this.name);
        dest.writeTypedList(this.cardItems);
    }

    protected Card(Parcel in) {
        this.id = in.readString();
        this.projectId = in.readString();
        this.name = in.readString();
        this.cardItems = new RealmList<>();
        this.cardItems.addAll(in.createTypedArrayList(CardItems.CREATOR));
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
