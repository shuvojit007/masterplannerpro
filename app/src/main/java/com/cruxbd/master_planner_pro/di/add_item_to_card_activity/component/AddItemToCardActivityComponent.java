package com.cruxbd.master_planner_pro.di.add_item_to_card_activity.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.add_item_to_card_activity.module.AddItemToCardActivityModule;
import com.cruxbd.master_planner_pro.di.add_item_to_card_activity.scope.AddItemToCardActivityScope;
import com.cruxbd.master_planner_pro.view.activities.AddItemToCard;

import dagger.Component;

@Component (modules = AddItemToCardActivityModule.class, dependencies = AppComponent.class)
@AddItemToCardActivityScope
public interface AddItemToCardActivityComponent {
    void injectAddTodoItemToCardActivity(AddItemToCard addItemToCard);
}
