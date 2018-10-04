package com.cruxbd.master_planner_pro.di.master_planner_scrollview_fragment_feature.module;

import com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment;

import dagger.Module;

@Module
public class MasterPlannerScrollViewFragmentModule {
    private final MasterPlannerScrollViewFragment masterPlannerScrollViewFragment;
    public MasterPlannerScrollViewFragmentModule(MasterPlannerScrollViewFragment masterPlannerScrollViewFragment){
        this.masterPlannerScrollViewFragment = masterPlannerScrollViewFragment;
    }
}
