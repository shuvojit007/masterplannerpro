package com.cruxbd.master_planner_pro.view.activities.tags_adapter;

import io.realm.RealmObject;

public class Tag_Model extends RealmObject{
    private String tagTitle;
    private String tagColor;

    public Tag_Model(String tagTitle, String tagColor) {
        this.tagTitle = tagTitle;
        this.tagColor = tagColor;
    }

    public Tag_Model() {
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public void setTagTitle(String tagTitle) {
        this.tagTitle = tagTitle;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    @Override
    public String toString() {
        return
                "tagTitle=" + tagTitle +
                " tagColor=" + tagColor +"\n";

    }
}
