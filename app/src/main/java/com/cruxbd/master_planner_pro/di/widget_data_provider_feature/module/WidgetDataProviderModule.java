package com.cruxbd.master_planner_pro.di.widget_data_provider_feature.module;

import com.cruxbd.master_planner_pro.di.scope.AppScope;
import com.cruxbd.master_planner_pro.di.widget_data_provider_feature.scope.WidgetDataProviderScope;
import com.cruxbd.master_planner_pro.view.widgets.WidgetDataProvider;
import com.cruxbd.master_planner_pro.view.widgets.WidgetProvider;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module
public class WidgetDataProviderModule {
    private final WidgetDataProvider widgetDataProvider;

    public WidgetDataProviderModule(WidgetDataProvider widgetDataProvider) {
        this.widgetDataProvider = widgetDataProvider;
    }

    @Provides
    @WidgetDataProviderScope
    Realm ProvideRealm(RealmConfiguration realmConfiguration){
        return Realm.getInstance(realmConfiguration);
    }

    @Provides
    @WidgetDataProviderScope
    RealmConfiguration ProvideConfiguration (){
        return  new RealmConfiguration.Builder()
                .name("todo.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }
}
