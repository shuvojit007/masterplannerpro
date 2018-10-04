package com.cruxbd.master_planner_pro.di.GeoFenceDI;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.module.HomeFragmentModule;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.scope.HomeFragmentScope;
import com.cruxbd.master_planner_pro.utils.geofence_service.GeofenceTrasitionService;
import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;

import dagger.Component;

@Component(modules = GeoFenceModule.class, dependencies = AppComponent.class)
@HomeFragmentScope
public interface GeoFenceComponent {
    void injectGeoFence(GeofenceTrasitionService geofenceTrasitionService);
}
