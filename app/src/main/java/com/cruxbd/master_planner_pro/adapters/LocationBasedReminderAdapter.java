package com.cruxbd.master_planner_pro.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.LocationModel;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.view.activities.location_based_reminder.LocationBasedReminderDetails;

import java.util.List;

import io.realm.Realm;

import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.ITEM_KEY;

public class LocationBasedReminderAdapter extends RecyclerView.Adapter<LocationBasedReminderAdapter.ViewHolder>{
    private static final String TAG = "LBRAdapter";
    private List<LocationModel> locationModelList;
    private Context context;
    private Realm realm;

    public LocationBasedReminderAdapter(List<LocationModel> locationModelList, Realm realm, Context context) {
        this.locationModelList = locationModelList;
        this.realm = realm;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.rec_location_based_reminder, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationModel model = locationModelList.get(position);
        holder.tvTodo.setText(model.getTodo());
        holder.tvPlace.setText(model.getPlaceName());

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LocationBasedReminderDetails.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.putExtra(ITEM_KEY, model);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return locationModelList.size();
    }

    public void removeItem(int position) {

        RealmService.deleteLocationReminder(locationModelList.get(position),realm);
        notifyDataSetChanged();
    }

    public void restoreItem(LocationModel locationItem, int position) {

        Log.d(TAG, "restoreItem: item: " + locationItem.toString());
        Log.d(TAG, "restoreItem: position: " + position);

        //Retrieve deleted project
        RealmService.restoreDeletedLocationReminder(locationItem,realm);
        //update adapter's MPProjectList
        locationModelList = RealmService.getLocationModel(realm);

        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTodo, tvPlace;

        public RelativeLayout viewBackground;
        public CardView viewForeground;

        View mView;
        public ViewHolder(View itemView) {
            super(itemView);

            tvTodo = itemView.findViewById(R.id.tvTodoLcation);
            tvPlace = itemView.findViewById(R.id.tvLocationPlace);
            viewBackground = itemView.findViewById(R.id.view_background_LBR);
            viewForeground= itemView.findViewById(R.id.cvLocationReminder);
            mView = itemView;

        }
    }
}
