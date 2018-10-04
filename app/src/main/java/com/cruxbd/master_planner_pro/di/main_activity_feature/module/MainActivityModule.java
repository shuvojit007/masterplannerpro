package com.cruxbd.master_planner_pro.di.main_activity_feature.module;



import com.cruxbd.master_planner_pro.view.activities.MainActivity;

import dagger.Module;

@Module
public class MainActivityModule {

    private final MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

}