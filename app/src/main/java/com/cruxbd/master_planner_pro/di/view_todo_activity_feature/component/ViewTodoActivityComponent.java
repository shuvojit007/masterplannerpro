package com.cruxbd.master_planner_pro.di.view_todo_activity_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.view_todo_activity_feature.module.ViewTodoActivityModule;
import com.cruxbd.master_planner_pro.di.view_todo_activity_feature.scope.ViewTodoActivityScope;
import com.cruxbd.master_planner_pro.view.activities.ViewTodoActivity;

import dagger.Component;

@Component (modules = ViewTodoActivityModule.class, dependencies = AppComponent.class)
@ViewTodoActivityScope
public interface ViewTodoActivityComponent {
    void injectViewTodoActivity(ViewTodoActivity viewTodoActivity);
}
