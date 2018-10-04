package com.cruxbd.master_planner_pro;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.cruxbd.master_planner_pro.BackupRestore.Backup;
import com.cruxbd.master_planner_pro.BackupRestore.GoogleDriveBackup;
import com.cruxbd.master_planner_pro.di.AppComponent;
import com.cruxbd.master_planner_pro.di.DaggerAppComponent;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmReceiver;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class App extends Application {
    public static RealmConfiguration config;

    public static final String CHANNEL_1_ID = "todo_channel";
    public static final String CHANNEL_2_ID = "master_planner_channel";
    private AppComponent appComponent;

    public static App get(Activity activity) {
        return (App) activity.getApplication();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        createNotificationChannels();


        appComponent= DaggerAppComponent.builder()
                .build();

       Realm.init(this);

        //getConfig();
        //---for widgets ---//
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .name("todo.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

 /*       if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
*/

        //---- Action boot completed enable for working Alarm after boot ----//
        ComponentName receiver = new ComponentName(this, AlarmReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void createNotificationChannels() {
        //create notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Todo Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            //user can see this description
            channel1.setDescription("Todo reminder channel");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "MasterPlanner Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            //user can see this description
            channel2.setDescription("Master Planner Notification channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

        }

    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @NonNull
    public Backup getBackup() {
        return new GoogleDriveBackup();
    }

    public  void getConfig() {
        config = new RealmConfiguration.Builder()
                .name("location.realm")
                .build();


    }
}
