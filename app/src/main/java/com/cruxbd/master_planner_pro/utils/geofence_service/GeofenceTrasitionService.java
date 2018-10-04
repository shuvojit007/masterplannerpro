package com.cruxbd.master_planner_pro.utils.geofence_service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.GeoFenceDI.DaggerGeoFenceComponent;
import com.cruxbd.master_planner_pro.di.GeoFenceDI.GeoFenceComponent;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.component.DaggerHomeFragmentComponent;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.component.HomeFragmentComponent;
import com.cruxbd.master_planner_pro.model.realm_model.LocationModel;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;

import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class GeofenceTrasitionService extends IntentService {

    String id;
    Realm realm;

    private static final String TAG = "MyGeofenceTrasitionServ";
    private String notificationTitle;

    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTrasitionService() {

        super(TAG);
    }

    public void getRealm(){


        realm = Realm.getDefaultInstance();


    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent) {
        getRealm();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence go : triggeringGeofences){
                Log.d(TAG, "onHandleIntent: TriggeringGeofences: "+ go.getRequestId());
                    getNotificationData(go.getRequestId());
            }
        }
    }

    private void getNotificationData(String requestId) {
        if(realm!=null){
            LocationModel locationModel = RealmService.getNotificationDetails(requestId,realm);
            sendNotification(locationModel.getTodo(),locationModel.getPlaceName());

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String msg ,String place ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = MainActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, place,notificationPendingIntent));

    }

    // Create notification
    private Notification createNotification(String todo, String placename, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder
                .setSmallIcon(R.drawable.earth_location)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(todo)
                .setContentText(placename)
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
