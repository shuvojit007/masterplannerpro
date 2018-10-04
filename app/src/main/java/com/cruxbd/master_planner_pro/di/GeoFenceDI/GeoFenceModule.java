package com.cruxbd.master_planner_pro.di.GeoFenceDI;

import com.cruxbd.master_planner_pro.utils.geofence_service.GeofenceTrasitionService;
import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;

import dagger.Module;

@Module
public class GeoFenceModule {
    private final GeofenceTrasitionService geofenceTrasitionService;
    public GeoFenceModule(GeofenceTrasitionService geofenceTrasitionService){
        this.geofenceTrasitionService = geofenceTrasitionService;
    }
}
