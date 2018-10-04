package com.cruxbd.master_planner_pro.di.backup_activity_feature.component;

import com.cruxbd.master_planner_pro.BackupRestore.BackupActivity;
import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.backup_activity_feature.module.BackupActivityModule;
import com.cruxbd.master_planner_pro.di.backup_activity_feature.scope.BackupActivityScope;

import dagger.Component;

@Component(modules = BackupActivityModule.class, dependencies = AppComponent.class)
@BackupActivityScope
public interface BackupActivityComponent {
    void injectBackupActivity(BackupActivity backupActivity);
}
