package com.cruxbd.master_planner_pro.view.activities.location_based_reminder;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;

import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.location_based_reminder_details_activity.component.DaggerLocationBasedReminderDetailsComponent;
import com.cruxbd.master_planner_pro.di.location_based_reminder_details_activity.component.LocationBasedReminderDetailsComponent;
import com.cruxbd.master_planner_pro.model.realm_model.LocationModel;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.ITEM_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.ACTION_TYPE_CALL;
import static com.cruxbd.master_planner_pro.utils.StaticValues.ACTION_TYPE_MESSAGE;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFY_BOTH;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFY_ENTER;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NOTIFY_EXIT;

public class LocationBasedReminderDetails extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "LBRDetails";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static final String LOCATION_KEY = "location_key";
    private LocationModel locationModel;
    private Context cn;
    Realm realm;

    private Toolbar toolbar;
    private TextView tvPlace, tvRadious;
    private ImageView ivRadiusPopupMenu;
    private EditText etNote;
    private ImageView ivNavigate;
    private SeekBar sbRadious;

    private RadioGroup radioGroup;
    private RadioButton rb_notify_both, rb_notify_exit, rb_notify_enter;
    ArrayAdapter<String> actionAdapter;
    private Spinner spinnerAction;
    private EditText etPhone, etMessage;
    private TextView tvValidity;
    private String notifyWhen;

    //---- notification validity ---//
    private Date validity;
    DatePickerDialog.OnDateSetListener remindDateListener;
    TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private int year, month, day, hour, minute;
    int am_pm;


    //====Map====//

    private static final int PLACE_PICKER_REQUEST = 1;
    private MapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;

    private String locationName;
    private double lat = 0.0;
    private double lng = 0.0;
    private float radious = 500.0f;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );
    private int SELECT_PHONE_NUMBER = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_based_reminder_layout);
        cn = this;
        setupDagger2();
        initGMaps();
        createGoogleApi();
        init();


        if (getIntent().getExtras() != null) {
            Log.d(TAG, "onCreate: update reminder");
            locationModel = getIntent().getExtras().getParcelable(ITEM_KEY);
            if (locationModel != null && locationModel.getPlaceName() != null) {
                Log.d(TAG, "onCreate: Intent Data found.");
                Log.d(TAG, "onCreate: Location data: " + locationModel.toString());
                locationName = locationModel.getPlaceName();
                tvPlace.setText(locationModel.getPlaceName());
                etNote.setText(locationModel.getTodo());

                if (locationModel.getRadius() < 1000) {
                    tvRadious.setText(locationModel.getRadius() + " m");
                } else {
                    tvRadious.setText(locationModel.getRadius() / 1000.0f + " km");
                }

                //--- set seek bar progress --//
                int progress = (int) ((100.0f / 30000.0f) * (locationModel.getRadius()));
                sbRadious.setProgress(progress);

                Log.d(TAG, "onCreate: notifyWhen = " + locationModel.getNotifyWhen());

                radioGroup.clearCheck();
                String notify = locationModel.getNotifyWhen();

                if (notify.equals(NOTIFY_BOTH)) radioGroup.check(R.id.radio_btn_both);
                else if (notify.equals(NOTIFY_ENTER)) radioGroup.check(R.id.radio_btn_entering);
                else radioGroup.check(R.id.radio_btn_exiting);

                int action_position = actionAdapter.getPosition(locationModel.getActionType());

                spinnerAction.setSelection(action_position);

                if (locationModel.getActionPhone() != null)
                    etPhone.setText(locationModel.getActionPhone());

                if (locationModel.getActionMessage() != null)
                    etMessage.setText(locationModel.getActionMessage());
                else {
                    switch (locationModel.getActionType()) {
                        case ACTION_TYPE_CALL:
                            etMessage.setHint("Write something about conversation, we notify you that.");
                            break;
                        case ACTION_TYPE_MESSAGE:
                            etMessage.setHint("Write your message here");
                            break;
                    }
                }

                if (locationModel.getValidity() != null) {
                    String validity_time = TimeFormatString.getStringTime(locationModel.getValidity());
                    tvValidity.setText(validity_time.replace("\n", ", "));
                }
            }

        } else {
            Log.d(TAG, "onCreate: new reminder");
        }

        ivNavigate.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(locationName)){
                //Todo------show navigation on google map--------------//
                Uri gmmIntentUri = Uri.parse(String.format(getString(R.string.google_map_navigation_query), locationName));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(getString(R.string.google_map_package));
                startActivity(mapIntent);
            }
        });

        setListenersInViews();
    }


    private void setListenersInViews() {
        //======== Click Listeners ========//

        //--- set location ---/
        tvPlace.setOnClickListener(v -> {
            if (isServiceOk()) {
                getLocation();
            }
        });

        //--- set radius from popup menu ---//
        ivRadiusPopupMenu.setOnClickListener(v -> {
            // show popup menu.. then dialog and take input from user
            PopupMenu popupMenu = new PopupMenu(cn, v);

            popupMenu.getMenuInflater().inflate(R.menu.radius_input_popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.popup_item_radius) {

                    showRadiusCustomInputDialog();
                }


                return true;
            });

            popupMenu.show();

        });


        //--- set radius ---/
        sbRadious.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0)
                    radious = 300.0f;
                else
                    radious = progress * 300.0f;

                tvRadious.setText(radious / 1000.0f + " km");

                LatLng place = new LatLng(lat, lng);
                map.clear();
                markerLocation(place);
                drawCircle(place, radious);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //--- set notify when ---/
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.radio_btn_both:
                    notifyWhen = NOTIFY_BOTH;
                    break;
                case R.id.radio_btn_entering:
                    notifyWhen = NOTIFY_ENTER;
                    break;
                case R.id.radio_btn_exiting:
                    notifyWhen = NOTIFY_EXIT;
                    break;
            }
        });

        //--- Action spinner ---/

        spinnerAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        etMessage.setHint("Write something about conversation, we notify you that.");
                        break;
                    case 1:
                        etMessage.setHint("Write your message here");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //--- set validity ---/
        tvValidity.setOnClickListener(v -> {
            showDateAndTimePickerDialog();
        });

    }

    private void showRadiusCustomInputDialog() {


        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_radius_custiom_input_dialog, null);

        EditText etCustomRadius = view.findViewById(R.id.etCustomRadius);
        Button btnSetRadius = view.findViewById(R.id.btnSetRadius);

        Dialog dialog = new Dialog(cn);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        btnSetRadius.setOnClickListener(v1 -> {
            String custom_radius = etCustomRadius.getText().toString().trim();

            if (!TextUtils.isEmpty(custom_radius)) {
                radious = Float.parseFloat(custom_radius);

                if (radious < 50.0 || radious > 50000.0) {
                    StyleableToast.makeText(cn, "Radius must be grater than 50m and less than 50000m.", Toast.LENGTH_LONG, R.style.mytoast).show();
                } else {

                    tvRadious.setText(radious / 1000.0f + " km");

                    LatLng place = new LatLng(lat, lng);
                    map.clear();
                    markerLocation(place);
                    drawCircle(place, radious);
                    dialog.dismiss();
                }

            } else {
                StyleableToast.makeText(cn, "Please input radius.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

        });

        dialog.show();
    }


    private void setupDagger2() {

        getRealm();
    }

    //==== new realm db for notification ===//
    public void getRealm() {
        realm = Realm.getDefaultInstance();


    }


    private void init() {
        //--- toolbar ---/
        toolbar = findViewById(R.id.toolbar_reminder_details);
        toolbar.setTitle("Add Location Reminder");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //--- initialize views --- //
        tvPlace = findViewById(R.id.tvLocationName_lbr);
        tvRadious = findViewById(R.id.tvRadius_lbr);
        ivRadiusPopupMenu = findViewById(R.id.ivRadiusPopupMenu);
        etNote = findViewById(R.id.add_todo_Notes_lbr);
        ivNavigate = findViewById(R.id.ivNavigation_lbr);
        sbRadious = findViewById(R.id.sbRadius_lbr);
        radioGroup = findViewById(R.id.radio_group);
        spinnerAction = findViewById(R.id.spinnerActionList_lbr);
        etPhone = findViewById(R.id.etPhoneNumber);
        etMessage = findViewById(R.id.etMessage);
        tvValidity = findViewById(R.id.tvValidity);

        findViewById(R.id.pickcontact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, SELECT_PHONE_NUMBER);
            }
        });

        //--- Action spinner ---/
        ArrayList<String> actionListArray = new ArrayList<>();
        actionListArray.add("Remind me to call");
        actionListArray.add("Remind me to send message");
        actionAdapter = new ArrayAdapter<>(cn, R.layout.layout_spinner_list, R.id.text, actionListArray);
        spinnerAction.setAdapter(actionAdapter);
    }


    private void showDateAndTimePickerDialog() {


        mTimeSetListener = (TimePicker view, int hourOfDay, int minute) -> {

            //save remindDateTime
            Calendar c = Calendar.getInstance();

            c.set(Calendar.AM_PM, am_pm);
            c.setTime(validity);
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);

            validity = c.getTime();

            String validity_time = TimeFormatString.getStringTime(validity);
            tvValidity.setText(validity_time.replace("\n", ", "));

        };

        remindDateListener = (view, year, month, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, month, dayOfMonth);
            validity = calendar1.getTime();
            hour = calendar1.get(Calendar.HOUR);
            minute = calendar1.get(Calendar.MINUTE);

            if (calendar1.get(Calendar.HOUR_OF_DAY) > 11) {
                am_pm = Calendar.PM;
            } else {
                am_pm = Calendar.AM;
            }


            new TimePickerDialog(this,
                    mTimeSetListener,
                    hour,
                    minute,
                    false)
                    .show();

        };

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, remindDateListener, year, month, day);

        datePickerDialog.show();

    }


    //todo -------------initialzie map and google api----------------
    private void initGMaps() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map02);
        mapFragment.getMapAsync(this);
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public boolean isServiceOk() {
        Log.d(TAG, "isServiceOk: ");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LocationBasedReminderDetails.this);

        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            Log.d(TAG, "isServiceOk: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occured but we can resolve it
            Log.d(TAG, "isServiceOk: an error occared but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LocationBasedReminderDetails.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            StyleableToast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT, R.style.mytoast).show();
        }
        return false;
    }

    private void getLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(LocationBasedReminderDetails.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                if (place == null) {
                    Log.i(TAG, "No place selected");
                    return;
                }
                locationName = place.getName() + ", " + place.getAddress();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                tvPlace.setText(locationName);

                markerLocation(place.getLatLng());
                drawCircle(place.getLatLng(), radious);

            } else {
                StyleableToast.makeText(this, "Can not get Location. Please try again.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }

        }

        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = cn.getContentResolver().query(contactUri, projection,
                    null, null, null);


            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                //  StyleableToast.makeText(cn, number, Toast.LENGTH_SHORT, R.style.mytoast).show();
                etPhone.setText(number);

            }

            cursor.close();
        }

    }
    //todo--------------- Place Picker scope---------------------


    //====================Marker Location ==========//
    private Marker locationMarker;

    private void markerLocation(LatLng latLng) {
        Log.i(TAG, "markerLocation(" + latLng + ")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if (map != null) {
            if (locationMarker != null)
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
            Log.d(TAG, "markerLocation: marker set on " + latLng);
        }
    }


    //todo -----------Activity lifecycle-------------------
    @Override
    protected void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }
    //todo -----------Activity lifecycle-------------------


    //todo ---------Map Ready , Map Onclick, Map on Marker Click-----------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMapClickListener(null);
        map.setOnMarkerClickListener(this);
        map.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

        if (locationModel != null) {

            lat = locationModel.getLat();
            lng = locationModel.getLng();
            LatLng latLng = new LatLng(lat, lng);
            markerLocation(latLng);
            radious = locationModel.getRadius();
            drawCircle(latLng, radious);
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition());
        return false;
    }
    //todo ---------Map Ready , Map Onclick, Map on Marker Click-----------


    private void drawCircle(LatLng point, float radius) {

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(radius);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        map.addCircle(circleOptions);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.activity_add_todo_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveLocationReminder();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveLocationReminder() {
        if (!TextUtils.isEmpty(locationName)) {
            String note = etNote.getText().toString().trim();
            if (!TextUtils.isEmpty(note)) {
                String actionType = spinnerAction.getSelectedItem().toString();
                String actionPhone = etPhone.getText().toString().trim();
                String actionMessage = etMessage.getText().toString().trim();

                if (notifyWhen == null || notifyWhen.equals("")) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.radio_btn_both:
                            notifyWhen = NOTIFY_BOTH;
                            break;
                        case R.id.radio_btn_entering:
                            notifyWhen = NOTIFY_ENTER;
                            break;
                        case R.id.radio_btn_exiting:
                            notifyWhen = NOTIFY_EXIT;
                            break;
                    }
                }
                //validity is not need to verify here.

                LocationModel model = new LocationModel();

                if (locationModel != null) {
                    Log.d(TAG, "saveLocationReminder: setting id & key of older location model");
                    model.setId(locationModel.getId());
                    model.setKey(locationModel.getKey());
                } else {
                    Log.d(TAG, "saveLocationReminder: createing new key for location model.");
                    model.setKey(UUID.randomUUID().toString());
                }

                model.setTodo(note);
                model.setPlaceName(locationName);
                model.setLat(lat);
                model.setLng(lng);
                model.setRadius(radious);


                model.setNotifyWhen(notifyWhen);

                if (!actionType.equals(""))
                    model.setActionType(actionType);
                if (!actionPhone.equals(""))
                    model.setActionPhone(actionPhone);
                if (!actionMessage.equals(""))
                    model.setActionMessage(actionMessage);
                if (validity != null)
                    model.setValidity(validity);


                Log.d(TAG, "saveLocationReminder: model=" + model.toString());
                RealmService.saveLocationReminder(model, realm);
                StyleableToast.makeText(cn, "Reminder saved.", Toast.LENGTH_SHORT, R.style.mytoast).show();

                //for onActivityResult
                Intent returnIntent = new Intent();
                returnIntent.putExtra(LOCATION_KEY, model);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            } else {
                Log.d(TAG, "saveLocationReminder: faild. Lat:" + lat + ", lng: " + lng + ", note: " + note);
                StyleableToast.makeText(cn, "Please add a note.", Toast.LENGTH_SHORT, R.style.mytoast).show();
            }


        } else {
            StyleableToast.makeText(cn, "Please choose a location", Toast.LENGTH_SHORT, R.style.mytoast).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
