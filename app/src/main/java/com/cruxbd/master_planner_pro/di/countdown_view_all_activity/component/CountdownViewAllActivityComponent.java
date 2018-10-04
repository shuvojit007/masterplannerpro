package com.cruxbd.master_planner_pro.di.countdown_view_all_activity.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.countdown_view_all_activity.module.CountdownViewAllActivityModule;
import com.cruxbd.master_planner_pro.di.countdown_view_all_activity.scope.CountdownViewAllActivityScope;
import com.cruxbd.master_planner_pro.view.activities.CountdownViewAllActivity;

import dagger.Component;

@Component (modules = CountdownViewAllActivityModule.class, dependencies = AppComponent.class)
@CountdownViewAllActivityScope
public interface CountdownViewAllActivityComponent {
    void injectCountdownViewAllActivity(CountdownViewAllActivity countdownViewAllActivity);
}
