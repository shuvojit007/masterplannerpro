package com.cruxbd.master_planner_pro.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.MasterPlannerProject;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;
import java.util.zip.Inflater;

public class MasterPlannerProjectsAdapter extends RecyclerView.Adapter<MasterPlannerProjectsAdapter.ProjectsViewHolder> {

    public static final String PROJECT_KEY = "project_key";
    private static final String TAG = "MPProjectAdapter";
    private List<MasterPlannerProject> mProjectList;
    private RealmService mRealmService;

    public MasterPlannerProjectsAdapter(List<MasterPlannerProject> projects, RealmService realmService) {
        this.mProjectList = projects;
        this.mRealmService = realmService;
    }

    @NonNull
    @Override
    public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rec_view_projects, parent, false);

        return new ProjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsViewHolder holder, int position) {
        MasterPlannerProject item = mProjectList.get(position);
        holder.tvProjectName.setText(item.getProjectName());
        if (item.getCardCount() > 1)
            holder.tvCardCount.setText(item.getCardCount() + " cards");
        else
            holder.tvCardCount.setText(item.getCardCount() + " card");

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MasterPlannerActivity.class);
            intent.putExtra(PROJECT_KEY, item);
            v.getContext().startActivity(intent);

        });

        //------- change project name on long click -------//
        holder.mView.setOnLongClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_edit_card_title, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

/*
            TextView tvTitle = dialogView.findViewById(R.id.tvEditTitle);
            tvTitle.setText(R.string.edit_project_name);*/



            EditText etEditTitle = dialogView.findViewById(R.id.etTitle);
            etEditTitle.setHint(R.string.edit_project_name_hint);
            etEditTitle.setText(item.getProjectName());
            int pos = etEditTitle.length();
            Editable etext = etEditTitle.getText();
            Selection.setSelection(etext, pos);

            Button btnSaveTitle = dialogView.findViewById(R.id.btnSaveTitle);

            btnSaveTitle.setOnClickListener(v2 -> {
                String projectName = etEditTitle.getText().toString().trim();

                if (TextUtils.isEmpty(projectName)) {
                    StyleableToast.makeText(dialogView.getContext(), "Project name can not be empty.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                } else {
                    mRealmService.updateProjectName(item.getId(), projectName);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }

            });

            dialog.show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mProjectList.size();
    }

    public void removeItem(int position) {

        mRealmService.deleteProjectListItem(mProjectList.get(position));
        notifyDataSetChanged();

//        notifyItemRemoved(position);
    }

    public void restoreItem(MasterPlannerProject project, int position) {

        Log.d(TAG, "restoreItem: item: " + project.toString());
        Log.d(TAG, "restoreItem: position: " + position);

        //Retrieve deleted project
        mRealmService.restoreDeletedProjectMP(project);
        //update adapter's MPProjectList
        mProjectList = mRealmService.getAllMasterPlannerProjects();

        notifyItemInserted(position);
    }

    public class ProjectsViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView tvProjectName, tvCardCount;
        public RelativeLayout viewBackground;
        public CardView view_foreground;

        public ProjectsViewHolder(View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvCardCount = itemView.findViewById(R.id.tvCardCount);
            viewBackground = itemView.findViewById(R.id.view_background_MPP);
            view_foreground = itemView.findViewById(R.id.cvCreateProject);
            mView = itemView;
        }

    }
}
