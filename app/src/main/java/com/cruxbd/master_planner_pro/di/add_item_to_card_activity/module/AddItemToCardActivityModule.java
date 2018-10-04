package com.cruxbd.master_planner_pro.di.add_item_to_card_activity.module;

import com.cruxbd.master_planner_pro.view.activities.AddItemToCard;

import dagger.Module;

@Module
public class AddItemToCardActivityModule {
    private final AddItemToCard addItemToCard;
    public AddItemToCardActivityModule(AddItemToCard addItemToCard){
        this.addItemToCard = addItemToCard;
    }
}
