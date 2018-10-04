package com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_view_holder;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.LockStatus;
import com.cruxbd.master_planner_pro.utils.MyDividerItemDecoration;
import com.cruxbd.master_planner_pro.view.activities.AddTodoDetails;
import com.cruxbd.master_planner_pro.view.activities.PinLock;
import com.cruxbd.master_planner_pro.view.activities.tags_adapter.TagAdapter;
import com.cruxbd.master_planner_pro.view.activities.tags_adapter.Tag_Model;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.RealmResults;

public class childViewHolder extends ChildViewHolder {
    private TextView childName;
    private RelativeLayout relativeLayout;
    private ImageView icon,dropdown,addlist;
    private TagAdapter tagAdapter;
    private static List<Tag_Model> tag = new ArrayList<>();



    public childViewHolder(View itemView) {
        super(itemView);
        childName =itemView.findViewById(R.id.rec_child_title);
        icon = itemView.findViewById(R.id.rec_child_icon);
        dropdown = itemView.findViewById(R.id.rec_child_dropdown);
        addlist = itemView.findViewById(R.id.rec_child_add);
        relativeLayout = itemView.findViewById(R.id.rec_child_relayout);

        relativeLayout.setOnClickListener((vv)->{
            if(childName.getText().equals("Tags")){
                Toast.makeText(vv.getContext(), "TAg", Toast.LENGTH_SHORT).show();
                ShowTagList(vv);
            }else if(childName.getText().equals("Add New Task")){
                    Intent intent = new Intent(vv.getContext(), AddTodoDetails.class);
                    vv.getContext().startActivity(intent);
            }else if(childName.getText().equals("Locked Todo")){

                // get

                vv.getContext().startActivity(new Intent(vv.getContext(), PinLock.class));
            }
            else{
                Toast.makeText(vv.getContext(), childName.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void UpdateTag(){
        tag.clear();
        RealmResults<Tag_Model> res = RealmService.ReadTagList();
        for (Tag_Model data:res){
            tag.add(data);
        }
    }

    private void ShowTagList(View v) {
        tag.clear();
        LayoutInflater inflater = LayoutInflater.from(v.getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final View dialogView = inflater.inflate(R.layout.personal_tag_list,null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        final ImageView fab = dialogView.findViewById(R.id.tag_fab);
        final EditText mtagEdit =dialogView.findViewById(R.id.tag_ed);
        final RecyclerView mrec = dialogView.findViewById(R.id.tag_rec);
        LinearLayoutManager lin = new LinearLayoutManager(v.getContext());
        mrec.setLayoutManager(lin);
        mrec.setItemAnimator(new DefaultItemAnimator());
        mrec.addItemDecoration(new MyDividerItemDecoration(v.getContext(), LinearLayoutManager.VERTICAL, 16));


         RealmResults<Tag_Model> res = RealmService.ReadTagList();
         for (Tag_Model data:res){
            tag.add(data);
         }

        tagAdapter = new TagAdapter(tag,v.getContext());
        mrec.setAdapter(tagAdapter);

        fab.setOnClickListener(view -> {

            RealmService.AddTag( Objects.requireNonNull(mtagEdit).getText().toString());
            tag.clear();
            RealmResults<Tag_Model> res1 = RealmService.ReadTagList();
            for (Tag_Model data: res1){
                tag.add(data);
            }

            tagAdapter.notifyDataSetChanged();

//            LayoutInflater inflater1 = LayoutInflater.from(view.getContext());
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
//            final View dview = inflater1.inflate(R.layout.add_tags_layout,null);
//            builder1.setView(dview);
//            final AlertDialog dialog1= builder1.create();
//            final EditText gettodo = dview.findViewById(R.id.todo_tag_edittex);
//            final Button canceltag = dview.findViewById(R.id.todo_tag_cancel);
//            final Button addtag = dview.findViewById(R.id.todo_tag_add);
//            canceltag.setOnClickListener((vv)->{
//                dialog.show();
//                dialog1.dismiss();
//            });
//
//            addtag.setOnClickListener((vv)->{
//                RealmService.AddTag(gettodo.getText().toString());
//                dialog1.dismiss();
//                tag.clear();
//                RealmResults<Tag_Model> res1 = RealmService.ReadTagList();
//                for (Tag_Model data: res1){
//                    tag.add(data);
//                }
//
//                tagAdapter.notifyDataSetChanged();
//
//            });
//
//            dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//            dialog1.show();
//
//            dialog1.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//                    dialog.show();
//                }
//            });
//            dialog.dismiss();

        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();
    }

    public void setIcon(int id){
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(id);
    }

    public void setChildName(String text){
        childName.setVisibility(View.VISIBLE);
        childName.setText(text);
    }

    public void setDropdown(int id){
        dropdown.setVisibility(View.VISIBLE);
        dropdown.setImageResource(id);

    }

    public void setAddlist(int id){
        addlist.setVisibility(View.VISIBLE);
        addlist.setImageResource(id);
    }
}
