package com.cruxbd.master_planner_pro.di.countdown_view_all_activity.module;

import com.cruxbd.master_planner_pro.view.activities.CountdownViewAllActivity;

import dagger.Module;

@Module
public class CountdownViewAllActivityModule {
    private final CountdownViewAllActivity countdownViewAllActivity;

    public CountdownViewAllActivityModule(CountdownViewAllActivity countdownViewAllActivity) {
        this.countdownViewAllActivity = countdownViewAllActivity;
    }
}
