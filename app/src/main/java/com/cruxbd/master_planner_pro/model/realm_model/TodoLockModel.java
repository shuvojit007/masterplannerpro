package com.cruxbd.master_planner_pro.model.realm_model;

import java.util.UUID;

import io.realm.RealmObject;

public class TodoLockModel extends RealmObject {
    private String id = UUID.randomUUID().toString();
    private String pin;

    public TodoLockModel() {
    }

    public TodoLockModel(String pin) {
        this.pin = pin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
