package com.cruxbd.master_planner_pro.di.widget_data_provider_feature.component;

import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.widget_data_provider_feature.module.WidgetDataProviderModule;
import com.cruxbd.master_planner_pro.di.widget_data_provider_feature.scope.WidgetDataProviderScope;
import com.cruxbd.master_planner_pro.view.widgets.WidgetDataProvider;
import com.cruxbd.master_planner_pro.view.widgets.WidgetProvider;

import dagger.Component;

@Component (modules = WidgetDataProviderModule.class)
@WidgetDataProviderScope
public interface WidgetDataProviderComponent {
    void injectWidgetProvider(WidgetDataProvider widgetDataProvider);

}
