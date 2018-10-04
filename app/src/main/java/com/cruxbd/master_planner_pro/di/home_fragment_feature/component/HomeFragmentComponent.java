package com.cruxbd.master_planner_pro.di.home_fragment_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.module.HomeFragmentModule;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.scope.HomeFragmentScope;
import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;

import dagger.Component;

@Component(modules = HomeFragmentModule.class, dependencies = AppComponent.class)
@HomeFragmentScope
public interface HomeFragmentComponent {
    void injectHomeFragment(HomeFragment homeFragment);
}
