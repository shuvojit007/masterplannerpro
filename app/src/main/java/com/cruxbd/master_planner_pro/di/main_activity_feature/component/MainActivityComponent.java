package com.cruxbd.master_planner_pro.di.main_activity_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.main_activity_feature.module.MainActivityModule;
import com.cruxbd.master_planner_pro.di.main_activity_feature.scope.MainActivityScope;

import com.cruxbd.master_planner_pro.view.activities.MainActivity;


import dagger.Component;

@Component(modules = MainActivityModule.class, dependencies = AppComponent.class)
@MainActivityScope
public interface MainActivityComponent {
    void injectMainActivity(MainActivity mainActivity);

}