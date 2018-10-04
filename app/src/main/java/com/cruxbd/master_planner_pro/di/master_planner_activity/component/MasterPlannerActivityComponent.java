package com.cruxbd.master_planner_pro.di.master_planner_activity.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.master_planner_activity.module.MasterPlannerActivityModule;
import com.cruxbd.master_planner_pro.di.master_planner_activity.scope.MasterPlannerActivityScope;
import com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity;

import dagger.Component;


@Component (modules = MasterPlannerActivityModule.class, dependencies = AppComponent.class)
@MasterPlannerActivityScope
public interface MasterPlannerActivityComponent {
    void injectMasterPlannerActivity(MasterPlannerActivity masterPlannerActivity);
}
