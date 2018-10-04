package com.cruxbd.master_planner_pro.di.view_countdown_activity_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.view_countdown_activity_feature.module.ViewCountdownActivityModule;
import com.cruxbd.master_planner_pro.di.view_countdown_activity_feature.scope.ViewCountdownActivityScope;
import com.cruxbd.master_planner_pro.view.activities.ViewCountdownActivity;

import dagger.Component;

@Component (modules = ViewCountdownActivityModule.class, dependencies = AppComponent.class)
@ViewCountdownActivityScope
public interface ViewCountdownActivityComponent {
    void injectViewCountdownActivity(ViewCountdownActivity viewCountdownActivity);
}
