package com.cruxbd.master_planner_pro.view.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.MasterPlannerCardAdapter;
import com.cruxbd.master_planner_pro.di.master_planner_scrollview_fragment_feature.component.DaggerMasterPlannerScrollViewFragmentComponent;
import com.cruxbd.master_planner_pro.di.master_planner_scrollview_fragment_feature.component.MasterPlannerScrollViewFragmentComponent;
import com.cruxbd.master_planner_pro.model.realm_model.Card;
import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.cruxbd.master_planner_pro.adapters.MasterPlannerProjectsAdapter.PROJECT_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_POSITION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;
import static com.cruxbd.master_planner_pro.view.activities.MasterPlannerActivity.VIEWPAGER_CARD_POSITION;

/**
 * A simple {@link Fragment} subclass.
 */

public class MasterPlannerScrollViewFragment extends Fragment {
    private static final String TAG = "MyMPScrollViewFragment";
    private static final String PROJECT_CARD = "project_card";
    private static final String CARD_TITLE = "title";
    private static final String CARD_NUMBER = "card_number";
    private static final String CARD_ID = "card_id";
    public static final String PROJECT_ID = "project_id";



    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.tvTotalAmount)
    TextView totalAmount;



    @Inject
    Realm mRealm;
    private RealmService mRealmService;
    static List<CardItems> mCardItemsList;
    private static MasterPlannerCardAdapter mCardAdapter;
    private LinearLayout mLinearLayout;


    // For tracking each card
    int pageNumber;
    String cardID="";
    String projectID = "";

    CardView firstCard;
    EditText etText;
    TextView tvCardTitle;
    static TextView tvTotalAmount;

    RelativeLayout rlCardTitleBar;


    Button btnAddCard, btnAddPage;
    LinearLayout linearLayoutRoot;
    int viewPosition;
    private static RecyclerView recyclerView;

    Context context;

    private String cardTitle;

    public static MasterPlannerScrollViewFragment newInstance(String projectID, Card card, int pageNumber){

        Bundle args = new Bundle();
        args.putParcelable(PROJECT_CARD, card);

        if(card.getCardItems().get(0).getDescription() != null){
//            Log.d(TAG, "newInstance: " +  card.getCardItems().get(0).getDescription());
        }else{
//            Log.d(TAG, "newInstance: description null!!!!");
        }

        args.putString(PROJECT_ID, projectID);
        args.putString(CARD_ID, card.getId());
        args.putString(CARD_TITLE,card.getName());
        args.putInt(CARD_NUMBER, pageNumber);
        MasterPlannerScrollViewFragment fragment = new MasterPlannerScrollViewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_master_planner_scroll_view, container, false);

        ButterKnife.bind(this, view);

        tvCardTitle = view.findViewById(R.id.title);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        btnAddCard = view.findViewById(R.id.btnAddHorizontalCard);
        rlCardTitleBar = view.findViewById(R.id.rlCardTitleBar);

        //==== Edit card Title =====//
        tvCardTitle.setOnClickListener(v -> {
            Dialog dialog = new Dialog(view.getContext());
            final View view2 = getLayoutInflater().inflate(R.layout.dialog_edit_card_title, null);

            EditText etEditTitle = view2.findViewById(R.id.etTitle);
            etEditTitle.setText(tvCardTitle.getText().toString());
            int position = etEditTitle.length();
            Editable etext = etEditTitle.getText();
            Selection.setSelection(etext, position);

            Button btnSaveTitle = view2.findViewById(R.id.btnSaveTitle);

            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view2);
            dialog.setCanceledOnTouchOutside(true);

            btnSaveTitle.setOnClickListener(v2 -> {
                String cardTitle = etEditTitle.getText().toString().trim();

                if(TextUtils.isEmpty(cardTitle)){
                    StyleableToast.makeText(view2.getContext(), "List title can not be empty.", Toast.LENGTH_SHORT, R.style.mytoast ).show();
                }else{
                    mRealmService.updateCardTitle(cardID, cardTitle);
                    tvCardTitle.setText(cardTitle);
                    this.cardTitle = cardTitle;
                    dialog.dismiss();
                }

            });

            dialog.show();

        });

        //==== Add Card Item =====//
        btnAddCard.setOnClickListener(v -> {
            //------creat dialog for taking input from user ----//
            LayoutInflater inflater1 = getLayoutInflater();
            final View view1 = inflater1.inflate(R.layout.dialog_quick_create_planner, null);

            Button btnSave = view1.findViewById(R.id.btnSave);
            EditText etTask = view1.findViewById(R.id.etTask);


            //======= create dialogue ========//
            Dialog dialog = new Dialog(v.getContext());
            dialog.getWindow();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view1);
            dialog.setCanceledOnTouchOutside(true);

            btnSave.setOnClickListener(v1 -> {
                String description = etTask.getText().toString().trim();

                if(!description.equals("")){

//                    String list = spinnerList.getSelectedItem().toString();

                    addItemOnCard(description,  cardID);
                    MasterPlannerRecyclerViewFragment.Refresh();
                    generateRecyclerView();

                    dialog.dismiss();
                }else{
                    StyleableToast.makeText(v1.getContext(), "Please write something", Toast.LENGTH_SHORT, R.style.mytoast ).show();
                }

            });

            dialog.show();
        });

        //==== Delete Card ======//
        rlCardTitleBar.setOnLongClickListener(v -> {


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


                            mRealmService.deleteCard(cardID, projectID);

                            StyleableToast.makeText(context, "Card deleted", Toast.LENGTH_SHORT, R.style.mytoast ).show();


                            Intent intent = new Intent(context, MasterPlannerActivity.class);
                            intent.putExtra(PROJECT_KEY, mRealmService.getMasterPlannerProject(projectID));
//                            Log.d(TAG, "createOrUpdateItem: card_position: " + VIEWPAGER_CARD_POSITION);

                            intent.putExtra(CARD_POSITION, VIEWPAGER_CARD_POSITION);
                            context.startActivity(intent);

                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


            return true;
        });


        return view;
    }



    private void addItemOnCard(String description,  String cardID) {
        if(!cardID.equals("")){
            CardItems cardItem = new CardItems();
            cardItem.setDescription(description);
            cardItem.setDone(false);
            cardItem.setAmount(0.0);
            cardItem.setHasCountdown(false);

            CountdownModel countdownModel = new CountdownModel();

            countdownModel.setProjectId(projectID);
            countdownModel.setCardItemId(cardID);
            countdownModel.setCountdownStatus(false);
            countdownModel.setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
            cardItem.setCountdown(countdownModel);

//            TodoReminder reminder = new TodoReminder();
//
//            TodoLocation location = new TodoLocation();
//
//            cardItem.setReminder(reminder);
//            cardItem.setLocation(location);
//            cardItem.setList(list);

            mRealmService.addCardItem(cardItem, cardID);
        }else{
            StyleableToast.makeText(getContext(), "Data not saved. Please try again.", Toast.LENGTH_SHORT, R.style.mytoast ).show();
        }

    }

    private void setUpDagger2(Context context) {
        MasterPlannerScrollViewFragmentComponent masterPlannerScrollViewFragmentComponent = DaggerMasterPlannerScrollViewFragmentComponent
                .builder()
                .appComponent(App.get((Activity) context).getAppComponent())
                .build();
        masterPlannerScrollViewFragmentComponent.injectMasterPlannerScrollViewFragment(MasterPlannerScrollViewFragment.this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        cardTitle = getArguments().getString(CARD_TITLE);
        title.setText(getArguments().getString(CARD_TITLE));
        projectID = getArguments().getString(PROJECT_ID);
        pageNumber = getArguments().getInt(CARD_NUMBER);
        cardID = getArguments().getString(CARD_ID);
        context = view.getContext();

        setUpDagger2(view.getContext());
        mRealmService = new RealmService(mRealm);
        recyclerView = view.findViewById(R.id.rvCardItem);

        setTotalAmountOnCard();

        generateRecyclerView();

////        Log.d(TAG, "onCreateView: Lastpage"+pageNumber);
//        if(pageNumber == MasterPlannerActivity.pageCount-1){
////            Log.d(TAG, "onCreateView: Lastpage"+pageNumber);
//            btnAddPage.setVisibility(View.VISIBLE);
//            firstCard.setVisibility(View.GONE);
//            btnAddCard.setVisibility(View.GONE);
//        }

        HollyViewPagerBus.registerScrollView(getActivity(), scrollView);
    }

    private void setTotalAmountOnCard() {
        float totalAmount = mRealmService.getTotalAmount(cardID);
//        Log.d(TAG, "setTotalAmountOnCard: TotalAmount: "+ totalAmount);
        tvTotalAmount.setText("Total: "+ totalAmount);
    }

    public static void setTotalAmountRemote(float totalAmount){

//        Log.d(TAG, "setTotalAmountRemote: "+totalAmount);

        //ButterKnife.apply(tvTotalAmount, SET_TOTAL_AMOUNT2);

        tvTotalAmount.setText("Total: "+ totalAmount);

//        if (totalAmount == 0.0) {
//            tvTotalAmount.setVisibility(View.INVISIBLE);
//        }else {
//            tvTotalAmount.setText("Total: "+ totalAmount);
//            tvTotalAmount.setVisibility(View.VISIBLE);
//        }


    }

    private void generateRecyclerView() {
        mCardItemsList = mRealmService.getCardItems(cardID);
//        Log.d(TAG, "generateRecyclerView: cardItems: "+ mCardItemsList.size());
//        Log.d(TAG, "generateRecyclerView: projectId: "+ projectID);
//        Log.d(TAG, "generateRecyclerView: CardId: "+ cardID);
        mCardAdapter = new MasterPlannerCardAdapter(mCardItemsList, projectID, cardID, cardTitle,mRealmService, context);
        recyclerView.setAdapter(mCardAdapter);
    }

    public static void generateRecyclerViewFromRemote(List<CardItems> cardItemsList,String projectId,String cardId, RealmService realmService, Context cn) {
        mCardItemsList = realmService.getCardItems(cardId);
//        Log.d(TAG, "generateRecyclerViewFromRemote: cardItems: "+ cardItemsList.size());
//        Log.d(TAG, "generateRecyclerViewFromRemote: projectId: "+ projectId);
//        Log.d(TAG, "generateRecyclerViewFromRemote: CardId: "+ cardId);
        mCardAdapter.notifyDataSetChanged();

//        mCardAdapter = new MasterPlannerCardAdapter(cardItemsList, projectId, cardId, realmService, cn);
//        recyclerView.setAdapter(mCardAdapter);
//        recyclerView.refreshDrawableState();
    }



    static final ButterKnife.Action<TextView> SET_TOTAL_AMOUNT = (totalAmount, index) -> {

//        Log.d(TAG, "apply: butter knife action");
        //tvTotalAmount.setText("123456");

        totalAmount.setText("asdfasdf");

    };

    static final ButterKnife.Action<TextView> SET_TOTAL_AMOUNT2 = new ButterKnife.Action<TextView>() {
        @Override
        public void apply(@NonNull TextView view, int index) {
//            Log.d(TAG, "apply: butter knife action2");
            view.setText("1235456");
        }
    };


    //custom setter
    public static final ButterKnife.Setter<View, Integer> VISIBILITY = (view, value, index) -> view.setVisibility(value);

    @OnClick(R.id.tvTotalAmount)
    public void setTotalAmount() {
        totalAmount.setText("Total: " + mRealmService.getTotalAmount(cardID));
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: ============ Scroll view =====");
        setTotalAmountOnCard();
        //generateRecyclerView();
        MasterPlannerRecyclerViewFragment.Refresh();

        mCardAdapter = new MasterPlannerCardAdapter(mRealmService.getCardItems(cardID), projectID, cardID, cardTitle, mRealmService, context);
        recyclerView.setAdapter(mCardAdapter);

        mCardAdapter.notifyDataSetChanged();

        totalAmount.setText("Total: "+ mRealmService.getTotalAmount(cardID));
    }
}
