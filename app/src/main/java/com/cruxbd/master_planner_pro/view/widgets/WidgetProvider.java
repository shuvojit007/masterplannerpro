package com.cruxbd.master_planner_pro.view.widgets;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.view.activities.AddTodoDetails;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;

/**
 * Created by Mridul on 13-Apr-18.
 */

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = "MyWidgetProvider";
    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.d(TAG, "updateAppWidget: appWidgetId" + appWidgetId);

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Set up the collection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, remoteView);
        } else {
            setRemoteAdapterV11(context, remoteView);
        }

        /*
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        views.setOnClickPendingIntent(R.id.tvWidgetCreateTodo, pendingIntent);*/

        // open activity from widget
        Intent showActivityIntent = new Intent(context, AddTodoDetails.class);
        PendingIntent showActivityPendingIntent = PendingIntent.getActivity(context,
                0, showActivityIntent, 0);

        remoteView.setOnClickPendingIntent(R.id.tvWidgetCreateTodo, showActivityPendingIntent);

        //===================================================================================
        Intent intent = new Intent (context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteView.setOnClickPendingIntent(R.id.tv_widget_title, pendingIntent);


        //---another

        // to create unique behavior on an item-by-item basis.
        Intent toastIntent = new Intent(context, WidgetProvider.class);
        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting TOAST_ACTION.
        toastIntent.setAction(WidgetProvider.TOAST_ACTION);
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setPendingIntentTemplate(R.id.widget_img_launcher, toastPendingIntent);



        //===================================================================================





        // Instruct the widget manager to update the widget
      //  appWidgetManager.updateAppWidget(appWidgetId, remoteView);


        ComponentName component=new ComponentName(context,WidgetProvider.class);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
        appWidgetManager.updateAppWidget(component, remoteView);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }

        super.onReceive(context, intent);
        Log.d(TAG, "onReceive: ");



    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d(TAG, "onUpdate: ");



        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "onUpdate: widgetID: " + appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled: " + context.getPackageName());
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled: " + context.getPackageName());
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, WidgetService.class));
        Log.d(TAG, "setRemoteAdapter: ");
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private static void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, WidgetService.class));
        Log.d(TAG, "setRemoteAdapterV11: ");
    }

}
