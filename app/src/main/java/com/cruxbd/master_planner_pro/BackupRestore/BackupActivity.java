package com.cruxbd.master_planner_pro.BackupRestore;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.backup_activity_feature.component.BackupActivityComponent;
import com.cruxbd.master_planner_pro.di.backup_activity_feature.component.DaggerBackupActivityComponent;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.realm.Realm;

public class BackupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICKER = 2;
    private static final int REQUEST_CODE_PICKER_FOLDER = 4;

    private static final String TAG = "crux_drive_backup";
    private static final String BACKUP_FOLDER_KEY = "backup_folder";

    private Backup backup;
    Button csvExport;
    private GoogleApiClient mGoogleApiClient;
    private TextView folderTextView;
    private IntentSender intentPicker;
    RealmBackupRestore realmBackupRestore;
    @Inject
    Realm realm;
    private String backupFolder;
    private ExpandableHeightListView backupListView;

    private SharedPreferences sharedPref;
    Context cn;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        App Application = (App) getApplicationContext();
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        cn = this;
        setUpDagger2( cn);

        findViewById(R.id.csv_export).setOnClickListener(v->{
            new RealmBackupRestore((Activity)cn,realm).backup();
        });


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_backup_drive));

        backup = Application.getBackup();
        backup.init(this);
        connectClient();
        mGoogleApiClient = backup.getClient();

        Button backupButton = findViewById(R.id.activity_backup_drive_button_backup);
        TextView manageButton = findViewById(R.id.activity_backup_drive_button_manage_drive);
        folderTextView = findViewById(R.id.activity_backup_drive_textview_folder);
        LinearLayout selectFolderButton = findViewById(R.id.activity_backup_drive_button_folder);
        backupListView = findViewById(R.id.activity_backup_drive_listview_restore);

        backupListView.setExpanded(true);

        backupButton.setOnClickListener(v -> {
            // Open Folder picker, then upload the file on Drive
            ProgressDialog dialog = new ProgressDialog(cn);
            dialog.setTitle("Backup");
            dialog.setMessage("Please wait ...!");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            openFolderPicker(true);
            dialog.dismiss();
        });

        selectFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check first if a folder is already selected
                if (!"".equals(backupFolder)) {
                    //Start the picker to choose a folder
                    //False because we don't want to upload the backup on drive then
                    openFolderPicker(false);
                }
            }
        });

        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOnDrive(DriveId.decodeFromString(backupFolder));
            }
        });

        // Show backup folder, if exists
        backupFolder = sharedPref.getString(BACKUP_FOLDER_KEY, "");
        if (!("").equals(backupFolder)) {
            setBackupFolderTitle(DriveId.decodeFromString(backupFolder));
            manageButton.setVisibility(View.VISIBLE);
        }

        // Populate backup list
        if (!("").equals(backupFolder)) {
            getBackupsFromDrive(DriveId.decodeFromString(backupFolder).asDriveFolder());
        }
    }

    private void setBackupFolderTitle(DriveId id) {
        id.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                new ResultCallback<DriveResource.MetadataResult>() {
                    @Override
                    public void onResult(@NonNull DriveResource.MetadataResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showErrorDialog();
                            return;
                        }
                        Metadata metadata = result.getMetadata();
                        folderTextView.setText(metadata.getTitle());
                    }
                }
        );
    }

    private void openFolderPicker(boolean uploadToDrive) {
        if (uploadToDrive) {
            // First we check if a backup folder is set
            if (TextUtils.isEmpty(backupFolder)) {
                try {
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                        if (intentPicker == null)
                            intentPicker = buildIntent();
                        //Start the picker to choose a folder
                        startIntentSenderForResult(
                                intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
                    }
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Unable to send intent", e);
                    showErrorDialog();
                }
            } else {
                uploadToDrive(DriveId.decodeFromString(backupFolder));
            }
        } else {
            try {
                intentPicker = null;
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    if (intentPicker == null)
                        intentPicker = buildIntent();
                    //Start the picker to choose a folder
                    startIntentSenderForResult(
                            intentPicker, REQUEST_CODE_PICKER_FOLDER, null, 0, 0, 0);
                }
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
                showErrorDialog();
            }
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }

    private void getBackupsFromDrive(DriveFolder folder) {
        final Activity activity = this;
        SortOrder sortOrder = new SortOrder.Builder()
                .addSortDescending(SortableField.MODIFIED_DATE).build();
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "todo.realm"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .setSortOrder(sortOrder)
                .build();
        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {

                    private ArrayList<ReminderBackup> backupsArray = new ArrayList<>();

                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        int size = buffer.getCount();
                        for (int i = 0; i < size; i++) {
                            Metadata metadata = buffer.get(i);
                            DriveId driveId = metadata.getDriveId();
                            Date modifiedDate = metadata.getModifiedDate();
                            long backupSize = metadata.getFileSize();
                            backupsArray.add(new ReminderBackup(driveId, modifiedDate, backupSize));
                        }
                        backupListView.setAdapter(new BackupAdapter(activity, R.layout.activity_backup_drive_restore_item, backupsArray));
                    }
                });
    }

    public void downloadFromDrive(DriveFile file) {
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(result -> {
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }

                    // DriveContents object contains pointers
                    // to the actual byte stream
                    DriveContents contents = result.getDriveContents();
                    InputStream input = contents.getInputStream();

                    try {
                        File file1 = new File(realm.getPath());
                        OutputStream output = new FileOutputStream(file1);
                        try {
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }
                                output.flush();
                            } finally {
                                safeCloseClosable(input);
                            }
                        } catch (Exception e) {
                            reportToFirebase(e, "Error downloading backup from drive");
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        reportToFirebase(e, "Error downloading backup from drive, file not found");
                        e.printStackTrace();
                    } finally {
                        safeCloseClosable(input);
                    }

                    StyleableToast.makeText(getApplicationContext(),"Restarting to apply changes...", Toast.LENGTH_LONG, R.style.mytoast).show();

                    // Reboot app
                    Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mgr.setExact(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    } else {
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    }
                    System.exit(0);
                });
    }

    private void safeCloseClosable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            reportToFirebase(e, "Error downloading backup from drive, IO Exception");
            e.printStackTrace();
        }
    }

    private void uploadToDrive(DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.e(TAG, "Error while trying to create new file contents");
                                showErrorDialog();
                                return;
                            }
                            final DriveContents driveContents = result.getDriveContents();

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();


                                    File dbFile;

                                    dbFile = new File(realm.getPath());

                                    FileInputStream inputStream = null;
//                                    try {
//                                        Log.d(TAG, "run: realm.dbpath = "+ realm.getPath());
//
//
//
//                                        inputStream = new FileInputStream(new File(realm.getPath()));
//
//
//                                    } catch (FileNotFoundException e) {
//                                        Log.d(TAG, "run: FileNotFoundException: "+ e.getMessage());
//                                        reportToFirebase(e, "Error uploading backup from drive, file not found");
//                                        e.printStackTrace();
//                                    } finally {
//                                        if (inputStream != null) {
//                                            try {
//                                                Log.d(TAG, "run: inputstream not null. "+ inputStream.toString());
//
//                                            inputStream.close();
//                                        } catch (IOException ignored) {
//                                            //ignored
//                                                ignored.printStackTrace();
//                                                Log.d(TAG, "run: IOException");
//                                            }
//                                        }
//                                    }

                                    try {
                                        inputStream = new FileInputStream(dbFile);
                                        byte[] buf = new byte[1024];
                                        int bytesRead;

                                        if (inputStream != null) {
                                            while ((bytesRead = inputStream.read(buf)) != -1) {
                                                outputStream.write(buf, 0, bytesRead);
                                            }
                                        }


                                    } catch (FileNotFoundException e) {
                                        Log.d(TAG, "run: FileNotFoundException");
                                        e.printStackTrace();
                                    }
                                    catch (IOException e) {
                                        Log.d(TAG, "run: IOException: "+ e.getMessage());
                                        Log.d(TAG, "run: IOException: "+ e.toString());

                                        e.printStackTrace();
                                    }


                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle("todo.realm")
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in selected folder
                                    folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                                                    if (!result.getStatus().isSuccess()) {
                                                        Log.d(TAG, "Error while trying to create the file");
                                                        showErrorDialog();
                                                        finish();
                                                        return;
                                                    }
                                                    showSuccessDialog();
                                                    finish();
                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        }
    }

    private void openOnDrive(DriveId driveId) {
        driveId.asDriveFolder().getMetadata((mGoogleApiClient)).setResultCallback(
                result -> {
                    if (!result.getStatus().isSuccess()) {
                        showErrorDialog();
                        return;
                    }
                    Metadata metadata = result.getMetadata();
                    String url = metadata.getAlternateLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
        );
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    backup.start();
                }
                break;
            // REQUEST_CODE_PICKER
            case 2:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());

                    uploadToDrive(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;
            // REQUEST_CODE_PICKER_FOLDER
            case 4:
                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    saveBackupFolder(mFolderDriveId.encodeToString());
                    // Restart activity to apply changes
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                break;
        }
    }

    private void saveBackupFolder(String folderPath) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(BACKUP_FOLDER_KEY, folderPath);
        editor.apply();
    }

    private void showSuccessDialog() {
        StyleableToast.makeText(getApplicationContext(), "Database successfully backed up to Drive", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }

    private void showErrorDialog() {
        StyleableToast.makeText(getApplicationContext(), "Successfully restored database from Drive", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }

    private void reportToFirebase(Exception e, String message) {
//        FirebaseCrash.log(message);
//        FirebaseCrash.report(e);
    }

    public void connectClient() {
        backup.start();
    }

    public void disconnectClient() {
        backup.stop();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void setUpDagger2(Context cn) {
        BackupActivityComponent backupActivityComponent = DaggerBackupActivityComponent
                .builder()
                .appComponent(App.get((Activity)cn).getAppComponent())
                .build();

        backupActivityComponent.injectBackupActivity((BackupActivity) cn);
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}
