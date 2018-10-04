package com.cruxbd.master_planner_pro.view.activities.location_based_reminder;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.LocationBasedReminderAdapter;
import com.cruxbd.master_planner_pro.di.location_reminder_activity.component.DaggerLocationComponent;
import com.cruxbd.master_planner_pro.di.location_reminder_activity.component.LocationComponent;
import com.cruxbd.master_planner_pro.model.realm_model.LocationModel;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.ItemDecoration;
import com.cruxbd.master_planner_pro.utils.RecyclerItemTouchHelper;
import com.cruxbd.master_planner_pro.utils.RecyclerItemTouchHelperLocatonReminder;
import com.cruxbd.master_planner_pro.utils.geofence_service.GeofenceTrasitionService;
import com.cruxbd.master_planner_pro.utils.master_planner.RecyclerItemTouchHelperMPProject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.ITEM_KEY;
import static com.cruxbd.master_planner_pro.view.activities.location_based_reminder.LocationBasedReminderDetails.LOCATION_KEY;

public class LocationReminder extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = "LocationReminder";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int TAG_CODE_PERMISSION_LOCATION = 007;
    private static final int LOCATION_REMINDER_REQUEST = 357;

    private RecyclerView location_rec;
    private Toolbar location_toolbar;
    private TextView tvMsgEmptyLocationReminder;
    private SwitchCompat switchAlert;
    private FloatingActionButton location_fab;
    private Context cn;
    private LocationBasedReminderAdapter adapter;
    private List<LocationModel> modelList;
    private LocationModel deleted_item;
    private CoordinatorLayout coordinatorLayout;
    private SharedPreferences preferences;
    PendingIntent mGeofencePendingIntent;

    private GoogleApiClient googleApiClient;

    Realm realm;
    private static String SWITCH_ALERT = "location_alert";
    private GeofencingClient mGeofencingClient;
    private List<Geofence> geofenceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_reminder);
        cn = this;

        init();

        createGoogleApi();

        if (preferences.getBoolean(SWITCH_ALERT, false)) {
            switchAlert.setChecked(true);
        } else {
            switchAlert.setChecked(false);
        }


    }


    private void createGoogleApi() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();


        }
    }

    public boolean isServiceOk() {

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LocationReminder.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LocationReminder.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void init() {
        coordinatorLayout = findViewById(R.id.cl_location_reminder);
        location_toolbar = findViewById(R.id.toolbar_location);
        location_toolbar.setTitle("Location Reminder");
        location_toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(location_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvMsgEmptyLocationReminder = findViewById(R.id.tvMsgEmptyLocationReminder);

        location_fab = findViewById(R.id.location_fab);
        location_rec = findViewById(R.id.location_rec);
        switchAlert = findViewById(R.id.switch_alert);
        mGeofencingClient = LocationServices.getGeofencingClient(cn);

        preferences = getPreferences(cn.MODE_PRIVATE);
        location_rec.setLayoutManager(new LinearLayoutManager(cn));


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperLocatonReminder(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(location_rec);

        location_rec.setItemAnimator(new DefaultItemAnimator());
        location_rec.addItemDecoration(new ItemDecoration(5, 5, 5, 5));

        location_fab.setOnClickListener(v -> {
            if (isServiceOk())
                startActivity(new Intent(cn, LocationBasedReminderDetails.class));
            else
                Toast.makeText(cn, "Please enable location service for this app.", Toast.LENGTH_SHORT).show();
        });

        switchAlert.setOnClickListener(v -> setAlertMode());
    }

    private void setAlertMode() {
        SharedPreferences.Editor editor = preferences.edit();
        boolean check = switchAlert.isChecked();
        if (check) {

            editor.putBoolean(SWITCH_ALERT, check);
            Toast.makeText(cn, "Alert: On", Toast.LENGTH_SHORT).show();
            AddGeofence();
        } else {
            Log.d(TAG, "setAlertMode: false");
            editor.putBoolean(SWITCH_ALERT, check);
            Toast.makeText(cn, "Alert: Off", Toast.LENGTH_SHORT).show();
            UnregisterGeoFence();

        }

        editor.apply();
        editor.commit();
    }

    private void UnregisterGeoFence() {
        LocationServices.GeofencingApi.removeGeofences(
                googleApiClient,
                getGeofencePendingIntent()
        ).setResultCallback(status -> {
            if (status.isSuccess()) {
                Toast.makeText(cn, "Alert Remove", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void AddGeofence() {

        if (modelList.isEmpty()) {
            switchAlert.setChecked(false);
            tvMsgEmptyLocationReminder.setVisibility(View.VISIBLE);


        } else {
            tvMsgEmptyLocationReminder.setVisibility(View.GONE);
            geofenceList.clear();
            for (LocationModel md : modelList) {
                geofenceList.add(CreateGeofence(md.getLat(), md.getLng(), md.getKey(), md.getRadius()));
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(status -> Toast.makeText(cn, "Alert Added", Toast.LENGTH_SHORT).show());

        }

    }

    private GeofencingRequest getGeofencingRequest() {

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();


        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER );
//                                | GeofencingRequest.INITIAL_TRIGGER_EXIT
//                                | GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private Geofence CreateGeofence(double lat, double lng, String key, float radius) {
        return new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(lat, lng, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }


    //==== new realm db for notification ===//
    public void getRealm() {

        realm = Realm.getDefaultInstance();
//        if(modelList!=null){
//            modelList.clear();
//        }
        modelList = RealmService.getLocationModel(realm);

        if(modelList.size()>0) tvMsgEmptyLocationReminder.setVisibility(View.GONE);
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTrasitionService.class);

        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isServiceOk();


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        }

        googleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof LocationBasedReminderAdapter.ViewHolder) {
            // get the removed todoItem name to display it in snack bar
            String name = modelList.get(viewHolder.getAdapterPosition()).getPlaceName();

            // backup of removed todoItem for undo purpose
            final LocationModel deletedItem = modelList.get(viewHolder.getAdapterPosition());


            deleted_item = new LocationModel(deletedItem.getLat(), deletedItem.getLng(), deletedItem.getPlaceName(),
                    deletedItem.getRadius(), deletedItem.getTodo(), deletedItem.getNotifyWhen(),
                    deletedItem.getActionType(), deletedItem.getActionPhone(), deletedItem.getActionMessage(),
                    deletedItem.getValidity(), deletedItem.getKey());


            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the todoItem from recycler view

            adapter.removeItem(viewHolder.getAdapterPosition());

            if (modelList.size() == 0) tvMsgEmptyLocationReminder.setVisibility(View.VISIBLE);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from list!", Snackbar.LENGTH_LONG);

            snackbar.setAction("UNDO", view -> {
                adapter.restoreItem(deleted_item, deletedIndex);

                tvMsgEmptyLocationReminder.setVisibility(View.GONE);

                deleted_item = null;
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        getRealm();



        if (preferences.getBoolean(SWITCH_ALERT, false)) {
            adapter = new LocationBasedReminderAdapter(modelList, realm, cn);
            location_rec.setAdapter(adapter);
            UnregisterGeoFence();
            AddGeofence();
        } else {
            switchAlert.setChecked(false);
            adapter = new LocationBasedReminderAdapter(modelList, realm, cn);
            location_rec.setAdapter(adapter);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


