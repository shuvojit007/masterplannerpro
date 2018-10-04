package com.cruxbd.master_planner_pro.di.module;

import com.cruxbd.master_planner_pro.di.scope.AppScope;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module
public class RealmChannel {
    @Provides
    @AppScope
    Realm ProvideRealm(RealmConfiguration realmConfiguration){
               return Realm.getInstance(realmConfiguration);
          }

    @Provides
    @AppScope
    RealmConfiguration ProvideConfiguration (){
                return  new RealmConfiguration.Builder()
                    .name("todo.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            }
}
