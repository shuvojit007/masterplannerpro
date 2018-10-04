package com.cruxbd.master_planner_pro.di.backup_activity_feature.module;

import com.cruxbd.master_planner_pro.BackupRestore.BackupActivity;

import dagger.Module;

@Module
public class BackupActivityModule {
    private final BackupActivity backupActivity;

    public BackupActivityModule(BackupActivity backupActivity) {
        this.backupActivity = backupActivity;
    }
}


