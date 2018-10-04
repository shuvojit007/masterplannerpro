package com.cruxbd.master_planner_pro.di.module;

import android.content.Context;

import com.cruxbd.master_planner_pro.di.scope.AppScope;
import com.cruxbd.master_planner_pro.di.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextChannel {
    Context cn;

    public ContextChannel(Context cn) {
        this.cn = cn;
    }
    @ApplicationContext
    @AppScope
    @Provides
    public Context context(){
        return cn.getApplicationContext();
    }
}
