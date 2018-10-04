package com.cruxbd.master_planner_pro.di.countdown_set_activity_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.countdown_set_activity_feature.module.CountdownSetActivityModule;
import com.cruxbd.master_planner_pro.di.countdown_set_activity_feature.scope.CountdownSetActivityScope;
import com.cruxbd.master_planner_pro.view.activities.CountdownSetActivity;

import dagger.Component;

@Component (modules = CountdownSetActivityModule.class, dependencies = AppComponent.class)
@CountdownSetActivityScope
public interface CountdownSetActivityComponent {
    void injectCountdownSetActivity(CountdownSetActivity countdownSetActivity);
}
