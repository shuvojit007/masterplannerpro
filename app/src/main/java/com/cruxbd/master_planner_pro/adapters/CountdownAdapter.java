package com.cruxbd.master_planner_pro.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;
import com.cruxbd.master_planner_pro.view.activities.ViewCountdownActivity;
import com.cruxbd.master_planner_pro.view.activities.ViewTodoActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.cruxbd.master_planner_pro.utils.StaticValues.COUNTDOWN_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.COUNTDOWN_ITEM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.INTENT_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MAIN_ACTIVITY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NORMAL_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_COUNTDOWN;

public class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.ViewHolder> {
    private static final String TAG = "MyCountdownAdtr";
    private List<CountdownModel> countdownList;
    private Context context;


    public CountdownAdapter(List<CountdownModel> countdownModelList, Context context) {
        this.countdownList = countdownModelList;
        this.context = context;
//        Log.d(TAG, "CountdownAdapter: constructor: modelList=" + countdownList.size());
//        Log.d(TAG, "CountdownAdapter: modelLIst=" + countdownList.toString());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.rec_countdown, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            CountdownModel model = countdownList.get(position);
//            Log.d(TAG, "onBindViewHolder: Title: " + model.getTitle());
//            Log.d(TAG, "onBindViewHolder: Date: " + model.getCountDownTime().getTime());
//            Log.d(TAG, "onBindViewHolder: cardId: " + model.getCardItemId());

            if(model.getCountdownFrom().equals(MASTER_PLANNER_COUNTDOWN)){
                holder.ivIcon.setImageResource(R.drawable.history_planner);
            } else if (model.getCountdownFrom().equals(TODO_COUNTDOWN)) {
                holder.ivIcon.setImageResource(R.drawable.history_todo);
            }else{
                holder.ivIcon.setImageResource(R.drawable.history);
            }

            holder.tvCountdownTitle.setText(model.getTitle());

            //----calculate remaining time---//
            long remingTime = model.getCountDownTime().getTime() - System.currentTimeMillis();


            new CountDownTimer(remingTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;
                    long months = days / 30;
                    long years = months / 12;

                    String strYears, strMonth, strDays, strHours, strMin, strSec;
                    String time = "";


                    int sec = (int) seconds % 60;
                    int min = (int) minutes % 60;
                    int hour = (int) hours % 24;


                    int day = (int) days % 30;
                    int month = (int) months % 12;


                    int year = (int) years % 12;

                    if (sec < 10) strSec = "0" + sec;
                    else strSec = "" + sec;

                    if (min < 10) strMin = "0" + min;
                    else strMin = "" + min;

                    if (hour < 10) strHours = "0" + hour;
                    else strHours = "" + hour;

                    if (day < 10) strDays = "0" + day;
                    else strDays = "" + day;

                    if (month < 10) strMonth = "0" + month;
                    else strMonth = "" + month;

                    if (year < 10) strYears = "0" + year;
                    else strYears = "" + year;


                    if (year != 0) {
                        time = strYears + "y " + strMonth + "m " + strDays + "d " + strHours + "h " + strMin + "m " + strSec + "s";
                    } else if (month != 0) {
                        time = strMonth + "m " + strDays + "d " + strHours + "h " + strMin + "m " + strSec + "s";
                    } else if (day != 0) {
                        time = strDays + "d " + strHours + "h " + strMin + "m " + strSec + "s";
                    } else {
                        time = strHours + "h " + strMin + "m " + strSec + "s";
                    }

               /* time = strYears + "y:" + strMonth+ "m:" + strDays + "d\n" +
                        strHours + "h:" + strMin + "m:" + strSec + "s";*/


                    holder.tvCountdownTimer.setText(time);
                }

                public void onFinish() {
                    holder.tvCountdownTimer.setText(R.string.finish);
                }
            }.start();

            holder.mView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ViewCountdownActivity.class);

                if (context.getClass().equals(MainActivity.class)) {
                    intent.putExtra(INTENT_FROM, MAIN_ACTIVITY);
                }else {
                    intent.putExtra(INTENT_FROM, COUNTDOWN_FROM);
                }

                intent.putExtra(COUNTDOWN_FROM, model.getCountdownFrom());
                intent.putExtra(COUNTDOWN_ITEM, model);
                v.getContext().startActivity(intent);

            });
    }


    @Override
    public int getItemCount() {

        if (context.getClass().equals(MainActivity.class)) {

            if(countdownList.size()<3){
                return countdownList.size();
            }
            return 3;

        }else {
            return countdownList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCountdownTitle, tvCountdownTimer;
        public ImageView ivIcon;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivCountDown);
            tvCountdownTitle = itemView.findViewById(R.id.tvCountdownTitle);
            tvCountdownTimer = itemView.findViewById(R.id.tvCountdownTime);
            mView = itemView;
        }

    }


}
