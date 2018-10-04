package com.cruxbd.master_planner_pro.model.realm_model;

import io.realm.RealmObject;

public class TodoModel extends RealmObject{

    private String location;

    private Boolean done;

    private Boolean deleted;

    private String createdAt;

    private String id;

    private String userId;

    private String task;

    private String list;

    public TodoModel() {
    }

    public TodoModel(String location, Boolean done, Boolean deleted, String createdAt, String id, String userId, String task, String list) {
        this.location = location;
        this.done = done;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.id = id;
        this.userId = userId;
        this.task = task;
        this.list = list;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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
        return "TodoModel{" +
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
}
