package com.cruxbd.master_planner_pro.di.view_todo_activity_feature.module;

import com.cruxbd.master_planner_pro.view.activities.ViewTodoActivity;

import dagger.Module;

@Module
public class ViewTodoActivityModule {
    private final ViewTodoActivity viewTodoActivity;
    public ViewTodoActivityModule (ViewTodoActivity viewTodoActivity){
        this.viewTodoActivity = viewTodoActivity;
    }
}
