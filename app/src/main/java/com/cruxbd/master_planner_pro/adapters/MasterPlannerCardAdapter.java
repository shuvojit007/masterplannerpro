package com.cruxbd.master_planner_pro.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.TimeFormatString;
import com.cruxbd.master_planner_pro.utils.alarm_service.AlarmManagerUtil;
import com.cruxbd.master_planner_pro.view.activities.AddItemToCard;
import com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity;
import com.cruxbd.master_planner_pro.view.fragments.MasterPlannerRecyclerViewFragment;
import com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.cruxbd.master_planner_pro.adapters.MasterPlannerProjectsAdapter.PROJECT_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_POSITION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_TITLE;
import static com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity.VIEWPAGER_CARD_POSITION;
import static com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment.PROJECT_ID;
import static com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment.setTotalAmountRemote;

public class MasterPlannerCardAdapter extends RecyclerView.Adapter<MasterPlannerCardAdapter.CardViewHolder> {
    private static final String TAG = "MyMPCardAdapter";
    public static final String CARD_ITEM_KEY = "card_key";
    public static final String CARD_ID = "card_id";

    private DatePickerDialog.OnDateSetListener expireDateListener;
    private TimePickerDialog.OnTimeSetListener expireTimeListener;
    int year, month, day, hour, minute, second;
    Date countdownExpireDate;
    private List<CardItems> mCards;
    private String projectId;
    LayoutInflater inflater;
    private String cardId;
    private RealmService mRealmService;
    private Context context;
    private String cardTitle;

    public MasterPlannerCardAdapter(List<CardItems> mCards, String projectId, String cardId, String cardTitle, RealmService mRealmService, Context context) {
        this.mCards = mCards;
        this.projectId = projectId;
        this.cardTitle = cardTitle;
        this.cardId = cardId;
        this.mRealmService = mRealmService;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View view = inflater.inflate(R.layout.rec_view_card, parent, false);
        View view = inflater.inflate(R.layout.rec_view_card, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        CardItems item = mCards.get(position);
        holder.tvCardItemText.setText(item.getDescription());

        //------ bill for ------//
        if (item.getBillFor() != null) {
            holder.tvBillFor_cardItem.setVisibility(View.VISIBLE);
            holder.tvAmount_cardItem.setVisibility(View.VISIBLE);
            holder.tvBillFor_cardItem.setText(item.getBillFor());
            holder.tvAmount_cardItem.setText(": " + item.getAmount());
        } else {
            holder.tvBillFor_cardItem.setVisibility(View.INVISIBLE);
            holder.tvAmount_cardItem.setVisibility(View.INVISIBLE);
        }


        //------ reminder ------//
        if (item.getReminder() != null) {

            holder.llReminder.setVisibility(View.VISIBLE);
            holder.tvReminder_card_item.setText("Remind me at " + TimeFormatString.getStringTime(item.getReminder().getRemindDate()));
        } else {
            holder.llReminder.setVisibility(View.INVISIBLE);
        }

        //------ card-item done check ------//
        if (item.getDone() != null) {
            if (item.getDone()) {

                holder.checkBoxCardItem.setChecked(item.getDone());
                holder.tvCardItemText.setPaintFlags(holder.tvCardItemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.checkBoxCardItem.setChecked(item.getDone());
                holder.tvCardItemText.setPaintFlags(holder.tvCardItemText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        //------ check and set countdown status on or off --------//

        Log.d(TAG, "onBindViewHolder: card desc: " + item.getDescription());
        Log.d(TAG, "onBindViewHolder: hasCountdown()" + item.getHasCountdown());

        if (item.getHasCountdown()) {
            if (item.getCountdown().getCountdownStatus())
                holder.switchCountdown.setChecked(true);
            else
                holder.switchCountdown.setChecked(false);
            Log.d(TAG, "onBindViewHolder: change switch countdown status: " + item.getCountdown().getCountdownStatus());
        }

        //----- card item Checkbox Listener -------//
        holder.checkBoxCardItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mRealmService.setCardItemDone(item.getId(), true);

                Objects.requireNonNull(HollyViewPagerBus.get(context).getViewPager().getAdapter()).notifyDataSetChanged();
                setTotalAmountOnCard();
                notifyDataSetChanged();
//                StyleableToast.makeText(buttonView.getContext(), "Card item done: " + isChecked, Toast.LENGTH_SHORT, R.style.mytoast).show();
            } else {
                mRealmService.setCardItemDone(item.getId(), false);
                Objects.requireNonNull(HollyViewPagerBus.get(context).getViewPager().getAdapter()).notifyDataSetChanged();
                setTotalAmountOnCard();
                notifyDataSetChanged();
//                StyleableToast.makeText(buttonView.getContext(), "Card item done: " + isChecked, Toast.LENGTH_SHORT, R.style.mytoast).show();
            }
        });

        //----- card countdown Switch Listener ------//
        holder.switchCountdown.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {

                if (!item.getHasCountdown()) {

                    //----- ENABLE COUNTDOWN -----//

                    final View vv = inflater.inflate(R.layout.dialog_set_countdown, null);
                    EditText etTitle = vv.findViewById(R.id.etCountdownTitle_d);
                    TextView tvDate = vv.findViewById(R.id.tvCountdownDate_d);
                    Button btnEnableCountdown = vv.findViewById(R.id.ivBtnSaveCountdown);

                    Dialog dialog = new Dialog(view.getContext());
                    dialog.getWindow();
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(vv);
                    dialog.setCanceledOnTouchOutside(true);

                    //=====SET COUNTDOWN DATE=====//
                    tvDate.setOnClickListener(v -> {
                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);


                        expireTimeListener = (TimePicker view2, int hourOfDay, int minute) -> {

                            //save remindDateTime
                            Calendar c = Calendar.getInstance();
                            c.setTime(countdownExpireDate);
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            c.set(Calendar.SECOND, 0);

                            countdownExpireDate = c.getTime();
                            tvDate.setText(String.format("Countdown valid till %s", TimeFormatString.getStringTime(countdownExpireDate)));

                        };

                        expireDateListener = (view1, year, month, dayOfMonth) -> {
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.set(year, month, dayOfMonth);
                            countdownExpireDate = calendar1.getTime();
                            hour = calendar1.get(Calendar.HOUR);
                            minute = calendar1.get(Calendar.MINUTE);

                            new TimePickerDialog(context,
                                    expireTimeListener,
                                    hour,
                                    minute,
                                    false)
                                    .show();

                        };

                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, expireDateListener, year, month, day);

                        datePickerDialog.show();
                    });

                    //=====SAVE COUNTDOWN======//
                    btnEnableCountdown.setOnClickListener(v -> {

                        String title = etTitle.getText().toString().trim();
                        if (!title.equals("") && countdownExpireDate != null) {

                            mRealmService.createPlannerCountdown(projectId, item.getId(), title, countdownExpireDate);
                            holder.tvCardCountDown.setText(v.getContext().getString(R.string.turn_off_countdown));
                            dialog.dismiss();

                            StyleableToast.makeText(context, "Countdown added.", Toast.LENGTH_SHORT, R.style.mytoast).show();

                            //---set Master planner project countdown--//

                        } else {
                            StyleableToast.makeText(context, "Please input both fields.", Toast.LENGTH_SHORT, R.style.mytoast).show();
                        }
                    });


                    dialog.show();

                    dialog.setOnCancelListener(dialog1 -> holder.switchCountdown.setChecked(false));
                }

            } else {

                if (item.getHasCountdown()) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(view.getContext());
                    }
                    builder.setTitle("Disable countdown")
                            .setMessage("Are you sure you want to disable countdown for this card item?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                               // Log.d(TAG, "countdown disable: ");
                                if (item.getHasCountdown()) {
                                    mRealmService.deletePlannerCountdown(item.getId(), item.getCountdown().getId());
                                }
                                holder.switchCountdown.setChecked(false);
                                holder.tvCardCountDown.setText(view.getContext().getString(R.string.turn_on_countdown));
                            })
                            .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                // do nothing
                                holder.switchCountdown.setChecked(true);
                                StyleableToast.makeText(view.getContext(), "Countdown disabled", Toast.LENGTH_SHORT, R.style.mytoast).show();
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();

                }
            }
        });

        holder.mView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AddItemToCard.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(PROJECT_ID, projectId);
            intent.putExtra(CARD_ID, cardId);
            intent.putExtra(CARD_TITLE, mRealmService.getCardTitle(cardId));
            intent.putExtra(CARD_ITEM_KEY, item);
            Log.d(TAG, "onBindViewHolder: Position: " + VIEWPAGER_CARD_POSITION);
            intent.putExtra(CARD_POSITION, VIEWPAGER_CARD_POSITION);

            v.getContext().startActivity(intent);
            ((Activity) v.getContext()).finish();

        });

        //=====DELETE CARD======//
        holder.mView.setOnLongClickListener(v -> {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle("Delete Card")
                    .setMessage("Are you sure you want to delete this card from the list?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // continue with delete

                        if (mCards.size() > 1) {

                            if (item.getReminder() != null) {
                                AlarmManagerUtil alarmManagerUtil = new AlarmManagerUtil(context.getApplicationContext());
                                alarmManagerUtil.cancelPlannerAlarm(item.getReminder().getAlarm_req_code());
                            }

                            mRealmService.deleteCardItem(item.getId(), cardId);

                            StyleableToast.makeText(context, "Card deleted", Toast.LENGTH_SHORT, R.style.mytoast).show();


                            Intent intent = new Intent(context, MasterPlannerActivity.class);
                            intent.putExtra(PROJECT_KEY, mRealmService.getMasterPlannerProject(projectId));
                            Log.d(TAG, "createOrUpdateItem: card_position: " + VIEWPAGER_CARD_POSITION);

                            intent.putExtra(CARD_POSITION, VIEWPAGER_CARD_POSITION);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        } else {
                            StyleableToast.makeText(context, "Sorry, card can not be deleted.\nThe list must contain at least one card.", Toast.LENGTH_LONG, R.style.mytoast).show();
                        }


                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        });
    }

    private void setTotalAmountOnCard() {
        float totalAmount = mRealmService.getTotalAmount(cardId);

        Log.d(TAG, "setTotalAmountOnCard: " + totalAmount);
        setTotalAmountRemote(totalAmount);
        MasterPlannerRecyclerViewFragment.Refresh();
        MasterPlannerScrollViewFragment.generateRecyclerViewFromRemote(mRealmService.getCardItems(cardId), projectId, cardId, mRealmService, context);
        MasterPlannerRecyclerViewFragment.Refresh();
        HollyViewPagerBus.get(context).getViewPager().getAdapter().notifyDataSetChanged();
//        setTotalAmountRemote(totalAmount);
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {


        public TextView tvCardItemText;
        public TextView tvBillFor_cardItem;
        public TextView tvAmount_cardItem;
        public TextView tvReminder_card_item;
        public TextView tvCardCountDown;
        public Switch switchCountdown;
        public CheckBox checkBoxCardItem;

        public LinearLayout llReminder;

        public View mView;

        public CardViewHolder(View itemView) {
            super(itemView);
            tvCardItemText = itemView.findViewById(R.id.tvCardItemText);
            tvBillFor_cardItem = itemView.findViewById(R.id.tvBillFor_cardItem);
            tvAmount_cardItem = itemView.findViewById(R.id.tvAmount_cardItem);
            tvReminder_card_item = itemView.findViewById(R.id.tvReminder_card_item);
            tvCardCountDown = itemView.findViewById(R.id.tvCardCountDown);
            llReminder = itemView.findViewById(R.id.llRemindDate);
            switchCountdown = itemView.findViewById(R.id.switchCountdown);
            checkBoxCardItem = itemView.findViewById(R.id.checkBoxCardItem);

            mView = itemView;

        }
    }
}
