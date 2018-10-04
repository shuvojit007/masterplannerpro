package com.cruxbd.master_planner_pro.di.view_countdown_activity_feature.module;

import com.cruxbd.master_planner_pro.view.activities.ViewCountdownActivity;

import dagger.Module;

@Module
public class ViewCountdownActivityModule {
    private final ViewCountdownActivity viewCountdownActivity;

    public ViewCountdownActivityModule(ViewCountdownActivity viewCountdownActivity) {
        this.viewCountdownActivity = viewCountdownActivity;
    }
}
