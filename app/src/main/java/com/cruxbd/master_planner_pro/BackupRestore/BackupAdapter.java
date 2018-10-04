package com.cruxbd.master_planner_pro.BackupRestore;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cruxbd.master_planner_pro.R;
import com.google.android.gms.drive.DriveId;

import java.util.List;

public class BackupAdapter extends ArrayAdapter<ReminderBackup> {

    private Context context;
    private FormatDateTime formatDateTime;

    public BackupAdapter(Context context, int resource, List<ReminderBackup> items) {
        super(context, resource, items);
        this.context = context;
        formatDateTime = new FormatDateTime(context);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_backup_drive_restore_item, parent, false);
        }

        ReminderBackup p = getItem(position);
        if (p != null) {
            final DriveId driveId = p.getDriveId();
            final String modified = formatDateTime.formatDate(p.getModifiedDate());
            final String size = Formatter.formatFileSize(getContext(), p.getBackupSize());

            TextView modifiedTextView = v.findViewById(R.id.item_history_time);
            TextView typeTextView =  v.findViewById(R.id.item_history_type);
            modifiedTextView.setText(modified);
            typeTextView.setText(size);

            v.setOnClickListener(v12 -> {
                // Show custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_backup_restore);
                TextView createdTextView =  dialog.findViewById(R.id.dialog_backup_restore_created);
                TextView sizeTextView =  dialog.findViewById(R.id.dialog_backup_restore_size);
                Button restoreButton =  dialog.findViewById(R.id.dialog_backup_restore_button_restore);
                Button cancelButton =  dialog.findViewById(R.id.dialog_backup_restore_button_cancel);

                createdTextView.setText(modified);
                sizeTextView.setText(size);

                restoreButton.setOnClickListener(v121 -> ((BackupActivity) context).downloadFromDrive(driveId.asDriveFile()));

                cancelButton.setOnClickListener(v1 -> dialog.dismiss());

                dialog.show();
            });
        }

        return v;
    }
}
