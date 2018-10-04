package com.cruxbd.master_planner_pro.di.location_reminder_activity.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.location_reminder_activity.module.LocationModule;
import com.cruxbd.master_planner_pro.di.location_reminder_activity.scope.LocationScope;
import com.cruxbd.master_planner_pro.view.activities.location_based_reminder.LocationReminder;

import dagger.Component;

@Component(modules = LocationModule.class, dependencies = AppComponent.class)
@LocationScope
public interface LocationComponent {
    void injectLocationReminderActivity(LocationReminder locationReminder);
}
