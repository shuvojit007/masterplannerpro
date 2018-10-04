package com.cruxbd.master_planner_pro.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;

//import com.cruxbd.todo_scrum.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmManagerUtil;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;
import com.cruxbd.master_planner_pro.view.activities.ViewTodoActivity;
import com.cruxbd.master_planner_pro.models.TodoItem;
import com.cruxbd.master_planner_pro.view.fragments.HomeFragment;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.cruxbd.master_planner_pro.utils.StaticValues.INTENT_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.LOCK_TODO_LIST_ACTIVITY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MAIN_ACTIVITY;


public class TodoItemAdapter extends RecyclerView.Adapter<TodoItemAdapter.ViewHolder> {
    public static final String ITEM_KEY = "item_key";
    public static final String EDIT_KEY = "edit_key";
    private List<TodoItem> mItems;
    private Context mContext;
    private RealmService mRealmService;


    public TodoItemAdapter(Context context, List<TodoItem> todoItemList, RealmService realmService) {
        this.mContext = context;
        this.mItems = todoItemList;
        this.mRealmService = realmService;


    }

    @Override
    public TodoItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.todo_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TodoItemAdapter.ViewHolder holder, int position) {


        TodoItem item = mItems.get(position);
        holder.tvTodo.setText(item.getTask());
        holder.tvList.setText(item.getList());

        //-----NOTE------//
        if(!TextUtils.isEmpty(item.getNote())){
            holder.ivNote.setVisibility(View.VISIBLE);
        }else {
            holder.ivNote.setVisibility(View.GONE);
        }

        //-----COUNTDOWN------//
        if(item.getHasCountdown()){
            holder.ivCountdown.setVisibility(View.VISIBLE);
        }else {
            holder.ivCountdown.setVisibility(View.GONE);
        }

        //-----REPEAT REMINDER------//
        if(item.getRepeatEnabled()){
            holder.ivRepeat.setVisibility(View.VISIBLE);
        }else{
            holder.ivRepeat.setVisibility(View.GONE);
        }

        //-----DUE DATE------//
        if (item.getDueDate() != null) {
            holder.tvTodoTime.setText(TimeFormatString.getDateString(item.getDueDate()));
            holder.tvTodoTime.setVisibility(View.VISIBLE);
        } else {

            holder.tvTodoTime.setVisibility(View.INVISIBLE);
        }

        //-----LOCATION------//
        if (!item.getLocationName().equals("")) {
            holder.tvLocation.setText(item.getLocationName());
            holder.ivLocation.setVisibility(View.VISIBLE);
            holder.tvLocation.setVisibility(View.VISIBLE);

        } else {
            holder.ivLocation.setVisibility(View.INVISIBLE);
            holder.tvLocation.setVisibility(View.INVISIBLE);
        }

        //-----REMIND DATE (ALARM TIME)------//
        if (item.getRemindDate() == null) {
            holder.ivClock.setVisibility(View.INVISIBLE);
            holder.tvAlarmTime.setVisibility(View.INVISIBLE);
        } else {
            holder.ivClock.setVisibility(View.VISIBLE);
            holder.tvAlarmTime.setVisibility(View.VISIBLE);
            holder.tvAlarmTime.setText(TimeFormatString.getTimeOnly(item.getRemindDate()));
        }



        //-----DONE------//
        if (item.getDone()) {
            holder.checkBox.setChecked(true);
            holder.tvTodo.setPaintFlags(holder.tvTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.checkBox.setChecked(false);
            holder.tvTodo.setPaintFlags(holder.tvTodo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        //-----PRIORITY------//
        int priority = item.getPriority();
        switch (priority) {
            case 0:
                holder.ivPriority.setBackgroundResource(R.drawable.circle);
                break;
            case 1:
                holder.ivPriority.setBackgroundResource(R.drawable.circle_low_priority);
                break;
            case 2:
                holder.ivPriority.setBackgroundResource(R.drawable.circle_medium_priority);
                break;
            case 3:
                holder.ivPriority.setBackgroundResource(R.drawable.circle_high_priority);
                break;
        }

        //-----TODO DONE LISTENER------//
        holder.checkBox.setOnClickListener(v -> {

            boolean checked = holder.checkBox.isChecked();
            if (checked) {

                mRealmService.setTodoDone(item.getId(), true);
                item.setDone(true);
                holder.tvTodo.setPaintFlags(holder.tvTodo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                //turn off alarm in the to-do has alarm
                if(item.getAlarm_status()){
                    AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(mContext.getApplicationContext());
                    alarmManagerUtil.cancelTodoAlarm(item.getAlarm_req_code());
                    StyleableToast.makeText(mContext, "This to-do's scheduled alarm has turned off.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                }

                HomeFragment.REGENERATE_RECYCLER_VIEW(mRealmService.GETTODOMODEL());

            } else {

                mRealmService.setTodoDone(item.getId(), false);
                item.setDone(false);
                holder.tvTodo.setPaintFlags(holder.tvTodo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                //turn on alarm in the to-do has alarm
                if(item.getRemindDate()!= null){
                    if(item.getRemindDate().getTime() >= System.currentTimeMillis()){
                        AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(mContext.getApplicationContext());
                        alarmManagerUtil.activateTodoAlarm(item);
                        StyleableToast.makeText(mContext, "This to-do's scheduled alarm has turned on.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                    }
                }

                HomeFragment.REGENERATE_RECYCLER_VIEW(mRealmService.GETTODOMODEL());
            }

        });


        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewTodoActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            if (mContext.getClass().equals(MainActivity.class)) {

                intent.putExtra(INTENT_FROM, MAIN_ACTIVITY);
            }else {
                intent.putExtra(INTENT_FROM, LOCK_TODO_LIST_ACTIVITY);
            }

            intent.putExtra(ITEM_KEY, item);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void removeItemByID(String id, int alarm_req_code) {
        Log.d(TAG, "removeItemByID: " + id);
        mRealmService.deletTodoListItemById(id);
        notifyDataSetChanged();
        AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(mContext.getApplicationContext());
        alarmManagerUtil.cancelTodoAlarm(alarm_req_code);


    }

    public void restoreItem(TodoItem item, int position) {
        mItems.add(position, item);
        notifyItemInserted(position);
        AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(mContext.getApplicationContext());
        alarmManagerUtil.initializeAlarm();
    }


    public void setFilter(List<TodoItem> filter) {
        mItems = new ArrayList<>();
        mItems.addAll(filter);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTodo, tvList, tvTodoTime, tvLocation, tvAlarmTime;
        public ImageView ivClock, ivLocation, ivPriority, ivRepeat, ivNote, ivCountdown;
        public View mView;
        public CheckBox checkBox;
        public RelativeLayout viewBackground;
        public CardView view_foreground;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTodo = itemView.findViewById(R.id.tvTodo);
            tvTodoTime = itemView.findViewById(R.id.tvTodoTime);
            tvAlarmTime = itemView.findViewById(R.id.tvAlarmTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvList = itemView.findViewById(R.id.tvListTitle);
            ivClock = itemView.findViewById(R.id.iv_alarmClock);
            ivLocation = itemView.findViewById(R.id.ivLocation);
            ivPriority = itemView.findViewById(R.id.viewPriority);
            ivRepeat = itemView.findViewById(R.id.ivRepeatAlarm);
            ivNote = itemView.findViewById(R.id.ivNote);
            ivCountdown = itemView.findViewById(R.id.ivTodoCountdown);
            checkBox = itemView.findViewById(R.id.checkBox);
            viewBackground = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);

            mView = itemView;


        }
    }


}
