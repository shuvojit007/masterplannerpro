package com.cruxbd.master_planner_pro.di.master_planner_projects_activity_feature.component;


import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.master_planner_projects_activity_feature.module.MasterPlannerProjectsActivityModule;
import com.cruxbd.master_planner_pro.di.master_planner_projects_activity_feature.scope.MasterPlannerProjectsActivityScope;
import com.cruxbd.master_planner_pro.view.activities.MasterPlannerProjectsActivity;

import dagger.Component;

@Component(modules = MasterPlannerProjectsActivityModule.class, dependencies = AppComponent.class)
@MasterPlannerProjectsActivityScope
public interface MasterPlannerProjectsActivityComponent {
    void injectMasterPlannerProjectsActivity(MasterPlannerProjectsActivity masterPlannerProjectsActivity);

}
