package com.cruxbd.master_planner_pro.view.widgets;

/**
 * Created by Mridul on 13-Apr-18.
 */

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * WidgetService is the {@link RemoteViewsService} that will return our RemoteViewsFactory
 */
public class WidgetService extends RemoteViewsService {
    private static final String TAG = "MyWidgetService";
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory: ");
        return new WidgetDataProvider(this, intent);
    }


}