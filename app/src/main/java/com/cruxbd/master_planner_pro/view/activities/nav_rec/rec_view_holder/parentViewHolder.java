package com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_view_holder;

import android.view.View;
import android.widget.TextView;

import com.cruxbd.master_planner_pro.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class parentViewHolder extends GroupViewHolder {
    private TextView parentTitle;

    public parentViewHolder(View itemView) {
        super(itemView);
        parentTitle = itemView.findViewById(R.id.rec_parent_name);
    }

    public void setParentName(String name){
        parentTitle.setText(name);
    }
}
