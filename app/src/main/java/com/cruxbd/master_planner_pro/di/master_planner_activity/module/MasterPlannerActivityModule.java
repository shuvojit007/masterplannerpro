package com.cruxbd.master_planner_pro.di.master_planner_activity.module;

import com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity;

import dagger.Module;

@Module
public class MasterPlannerActivityModule {
    private final MasterPlannerActivity masterPlannerActivity;

    public MasterPlannerActivityModule(MasterPlannerActivity masterPlannerActivity) {
        this.masterPlannerActivity = masterPlannerActivity;
    }
}
