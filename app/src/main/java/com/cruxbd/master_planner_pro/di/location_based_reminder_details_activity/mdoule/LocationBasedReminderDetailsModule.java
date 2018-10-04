package com.cruxbd.master_planner_pro.di.location_based_reminder_details_activity.mdoule;

import com.cruxbd.master_planner_pro.di.location_based_reminder_details_activity.scope.LocationBasedReminderDetailsScope;
import com.cruxbd.master_planner_pro.view.activities.location_based_reminder.LocationBasedReminderDetails;

import dagger.Module;

@Module
@LocationBasedReminderDetailsScope
public class LocationBasedReminderDetailsModule {
    private final LocationBasedReminderDetails locationBasedReminderDetails;

    public LocationBasedReminderDetailsModule(LocationBasedReminderDetails locationBasedReminderDetails){
        this.locationBasedReminderDetails = locationBasedReminderDetails;
    }

}
