package com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_model;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class parentModel  extends ExpandableGroup{
    public String title;


    public parentModel(String title, List items) {
        super(title, items);
    }
}
