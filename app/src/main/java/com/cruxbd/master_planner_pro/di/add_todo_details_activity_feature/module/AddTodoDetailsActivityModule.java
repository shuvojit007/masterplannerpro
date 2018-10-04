package com.cruxbd.master_planner_pro.di.add_todo_details_activity_feature.module;

import com.cruxbd.master_planner_pro.view.activities.AddTodoDetails;

import dagger.Module;

@Module
public class AddTodoDetailsActivityModule {
    private final AddTodoDetails addTodoDetails;

    public AddTodoDetailsActivityModule(AddTodoDetails addTodoDetails) {
        this.addTodoDetails = addTodoDetails;
    }
}
