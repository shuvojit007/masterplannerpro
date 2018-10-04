package com.cruxbd.master_planner_pro.di.add_todo_details_activity_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.add_todo_details_activity_feature.module.AddTodoDetailsActivityModule;
import com.cruxbd.master_planner_pro.di.add_todo_details_activity_feature.scope.AddTodoDetailsActivityScope;
import com.cruxbd.master_planner_pro.view.activities.AddTodoDetails;

import dagger.Component;

@Component (modules = AddTodoDetailsActivityModule.class, dependencies = AppComponent.class)
@AddTodoDetailsActivityScope
public interface AddTodoDetailsActivityComponent {

    void injectAddTodoDetailsActivity(AddTodoDetails addTodoDetails);

}
