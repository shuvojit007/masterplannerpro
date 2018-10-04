package com.cruxbd.master_planner_pro.view.activities.tags_adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.view.activities.nav_rec.rec_view_holder.childViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cruxbd.master_planner_pro.view.fragments.HomeFragment.REGENERATE_RECYCLER_VIEW;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private List<Tag_Model>model;
    RealmService realmService;
    private Context context;

    public TagAdapter(List<Tag_Model> model, Context context) {
        this.model = model;
        this.context = context;

    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_view_personal_tag, parent, false);

        return new TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        Tag_Model tag_model = model.get(position);
        holder.note.setText(tag_model.getTagTitle());

        holder.delete.setOnClickListener(v -> {
            RealmService.Delete(tag_model);
            childViewHolder.UpdateTag();
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class TagViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tag_title)
        TextView note;
        @BindView(R.id.tagdelete)
        ImageView delete;

        public TagViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



}
