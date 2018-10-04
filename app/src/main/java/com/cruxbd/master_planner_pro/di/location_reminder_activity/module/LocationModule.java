package com.cruxbd.master_planner_pro.di.location_reminder_activity.module;

import com.cruxbd.master_planner_pro.view.activities.location_based_reminder.LocationReminder;

import dagger.Module;

@Module
public class LocationModule {
    private final LocationReminder locationReminder;

    public LocationModule(LocationReminder locationReminder){
        this.locationReminder = locationReminder;
    }
}
