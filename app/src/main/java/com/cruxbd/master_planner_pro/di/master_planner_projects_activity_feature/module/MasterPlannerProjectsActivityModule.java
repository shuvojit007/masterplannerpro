package com.cruxbd.master_planner_pro.di.master_planner_projects_activity_feature.module;

import com.cruxbd.master_planner_pro.view.activities.MasterPlannerProjectsActivity;

import dagger.Module;

@Module
public class MasterPlannerProjectsActivityModule {
    private final MasterPlannerProjectsActivity masterPlannerProjectsActivity;

    public MasterPlannerProjectsActivityModule(MasterPlannerProjectsActivity masterPlannerProjectsActivity){
        this.masterPlannerProjectsActivity = masterPlannerProjectsActivity;
    }

}
