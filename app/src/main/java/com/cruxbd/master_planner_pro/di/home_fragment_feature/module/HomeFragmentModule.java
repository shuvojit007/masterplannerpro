package com.cruxbd.master_planner_pro.di.home_fragment_feature.module;

import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;

import dagger.Module;

@Module
public class HomeFragmentModule {
    private final HomeFragment homeFragment;
    public HomeFragmentModule(HomeFragment homeFragment){
        this.homeFragment = homeFragment;
    }
}
