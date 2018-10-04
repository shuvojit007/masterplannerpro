package com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_view_holder.childViewHolder;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_view_holder.parentViewHolder;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_model.childModel;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class RecAdapter extends ExpandableRecyclerViewAdapter <parentViewHolder,childViewHolder> {

    public RecAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);

    }

    @Override
    public parentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_view_parent,parent,false);
        return new parentViewHolder(view);
    }

    @Override
    public childViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rec_view_child,parent,false);
            return new childViewHolder(view);
    }



    @Override
    public void onBindChildViewHolder(childViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        childModel cm = (childModel) group.getItems().get(childIndex);
        holder.setIcon(cm.getIcon());
        holder.setChildName(cm.getTitle());
        if(!cm.getTitle().equalsIgnoreCase("Add New Task")||
                !cm.getTitle().equalsIgnoreCase("Active Reminders")){
            holder.setAddlist(cm.getAdd());
            holder.setDropdown(cm.getDropdown());
        }
    }

    @Override
    public void onBindGroupViewHolder(parentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setParentName(group.getTitle());
    }


}

