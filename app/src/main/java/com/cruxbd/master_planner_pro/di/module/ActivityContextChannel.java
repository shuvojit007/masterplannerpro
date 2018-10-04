package com.cruxbd.master_planner_pro.di.module;

import android.app.Activity;
import android.content.Context;

import com.cruxbd.master_planner_pro.di.scope.AppScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityContextChannel {
    private final Context context;

    ActivityContextChannel(Activity context){
        this.context = context;
    }
    @Named("activity_context")
    @AppScope
    @Provides
    public Context context(){
        return context;
    }
}
