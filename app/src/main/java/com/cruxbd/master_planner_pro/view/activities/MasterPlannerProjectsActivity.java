package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.MasterPlannerProjectsAdapter;
import com.cruxbd.master_planner_pro.di.master_planner_projects_activity_feature.component.DaggerMasterPlannerProjectsActivityComponent;
import com.cruxbd.master_planner_pro.di.master_planner_projects_activity_feature.component.MasterPlannerProjectsActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.Card;
import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.model.realm_model.MasterPlannerProject;
import com.cruxbd.master_planner_pro.model.realm_model.TodoLocation;
import com.cruxbd.master_planner_pro.model.realm_model.TodoReminder;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.RecyclerItemTouchHelper;
import com.cruxbd.master_planner_pro.utils.master_planner.RecyclerItemTouchHelperMPProject;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmList;

import static com.cruxbd.master_planner_pro.adapters.TodoItemAdapter.EDIT_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.EDIT_TODO;
import static com.cruxbd.master_planner_pro.utils.StaticValues.EDIT_TODO_REQUEST;
import static com.cruxbd.master_planner_pro.utils.StaticValues.INTENT_FROM;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MAIN_ACTIVITY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NORMAL_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.TODO_COUNTDOWN;

public class MasterPlannerProjectsActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    //    private static final String TAG = "MPProjectActivity";
    private Context mContext;
    private Toolbar mToolbar;
    private RecyclerView mProjectRecyclerView;
    public MasterPlannerProject deleted_item;
    public RealmList<Card> deleted_card;
    public RealmList<CardItems> deleted_cardItems;
    private LinearLayout linearLayout;
    private TextView tvPrjectMsg;

    @Inject
    Realm realm;
    private RealmService realmService;
    private List<MasterPlannerProject> projectsList;
    private MasterPlannerProjectsAdapter mProjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_planner_projects);
        mContext = MasterPlannerProjectsActivity.this;
        setUpDagger2(mContext);
        init();
        customizeToolbar();
        linearLayout = findViewById(R.id.llMasterPlannerProject);
        generateRecyclerView();
    }

    //----------RecyclerView--------------//
    private void generateRecyclerView() {
        projectsList = getProjectListFromDB();
        if (projectsList.size() > 0) {
            mProjectRecyclerView.setLayoutManager(new LinearLayoutManager(MasterPlannerProjectsActivity.this));
            mProjectAdapter = new MasterPlannerProjectsAdapter(projectsList, realmService);
            mProjectRecyclerView.setAdapter(mProjectAdapter);

            //--------add onSwipe delete functionality to recyclerView-----//
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperMPProject(0, ItemTouchHelper.LEFT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mProjectRecyclerView);

        } else {
            tvPrjectMsg.setVisibility(View.VISIBLE);
            Toast.makeText(mContext, "You have not created any project yet!", Toast.LENGTH_SHORT).show();
        }
    }


    //--------Setup Dagger 2 --------------//
    private void setUpDagger2(Context context) {
        MasterPlannerProjectsActivityComponent masterPlannerProjectsActivityComponent =
                DaggerMasterPlannerProjectsActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        masterPlannerProjectsActivityComponent.injectMasterPlannerProjectsActivity((MasterPlannerProjectsActivity) context);
    }


    //-------Toolbar-------------//
    private void customizeToolbar() {
        mToolbar.setTitle("Planner");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //---------Initialize views & objects -------------//
    private void init() {

        mToolbar = findViewById(R.id.master_planner_projets_toolbar);
        tvPrjectMsg = findViewById(R.id.tvPrjectMsg);
        mProjectRecyclerView = findViewById(R.id.rvProjects);
        if (realm != null) {
            realmService = new RealmService(realm);
        }
    }


    //-------- Get data from RealmDB -------------//
    public List<MasterPlannerProject> getProjectListFromDB() {
        if (realmService != null)
            return realmService.getAllMasterPlannerProjects();
        else return null;
    }

    public void createProject() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_create_project_dialog, null);

        EditText etProjectName = view.findViewById(R.id.etProjectName);
        Button btnSaveProject = view.findViewById(R.id.btnSaveProject);

        Dialog dialog = new Dialog(mContext);
        dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);

        btnSaveProject.setOnClickListener(v1 -> {
            String projectName = etProjectName.getText().toString().trim();
            if (!projectName.equals("")) {
                int cardCount = 2;

                if (realmService != null) {
                    MasterPlannerProject project = new MasterPlannerProject();

                    RealmList<Card> cards = new RealmList<>();


                    for (int i = 0; i < cardCount; i++) {

                        RealmList<CardItems> cardItems = new RealmList<>();

                        CardItems item = new CardItems();

                        item.setDescription("Write something on card...");

                        CountdownModel countdownModel = new CountdownModel();
                        countdownModel.setProjectId(project.getId());
                        countdownModel.setCardItemId(item.getId());
                        countdownModel.setCountdownStatus(false);
                        countdownModel.setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
                        item.setCountdown(countdownModel);
                        item.setHasCountdown(false);
                        item.setAmount(0.0);
                        item.setDone(false);
                        cardItems.add(item);

                        //==create card ====
                        Card card = new Card();
                        item.setCardId(card.getId());
                        card.setProjectId(project.getId());
                        card.setName("List " + (i + 1));

                        //==add card todoItem for testing--- should be removed==
                        card.setCardItems(cardItems);
                        //==add card in page ===
                        cards.add(i, card);
                    }

                    //MasterPlannerProject project = new MasterPlannerProject(projectName, cardCount, cards, Calendar.getInstance().getTime());
                    project.setProjectName(projectName);
                    project.setCardCount(cardCount);
                    project.setCard(cards);
                    project.setCreatedAt(Calendar.getInstance().getTime());
                    realmService.addMasterPlannerProject(project);

                    generateRecyclerView();
                    tvPrjectMsg.setVisibility(View.GONE);
                    dialog.dismiss();

                } else {
                    Toast.makeText(v1.getContext(), "Data not saved!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, "Please give a project name.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof MasterPlannerProjectsAdapter.ProjectsViewHolder) {
            // get the removed todoItem name to display it in snack bar
            String name = projectsList.get(viewHolder.getAdapterPosition()).getProjectName();

            // backup of removed todoItem for undo purpose

//            final MasterPlannerProject deletedItem = projectsList.get(viewHolder.getAdapterPosition());
//            deleted_card = deletedItem.getCard();
//
//            int cardLength = deletedItem.getCard().size();
//            int i;
//
//            Card newCard;
//            RealmList<Card> newCardList = new RealmList<>();
//            RealmList<CardItems> newCardItemList;
//            CardItems newCardItem;
//
//            for (i = 0; i < cardLength; i++) {
//
//                newCardList = new RealmList<>();
//
//                for (Card card : deleted_card) {
//
//                    deleted_cardItems = card.getCardItems();
//                    newCardItemList = new RealmList<>();
//                    for (CardItems cardItems : deleted_cardItems) {
//                        newCardItem = new CardItems(cardItems.getCardId(), cardItems.getDescription(), cardItems.getBillFor(), cardItems.getAmount(), cardItems.getReminder(),
//                                cardItems.getList(), cardItems.getPriority(), cardItems.getLocked(), cardItems.getLocation(), cardItems.getDone(), cardItems.getDeleted(), cardItems.getHasCountdown(),
//                                cardItems.getCountdown(), cardItems.getCreatedAt());
//
//                        newCardItemList.add(newCardItem);
//                    }
//
//                    newCard = new Card(card.getProjectId(), card.getName(), newCardItemList);
//
//                    newCardList.add(newCard);
//
//                }
//
//                // deleted_card.add(i, new Card(deletedItem.getCard().get(i).getProjectId(), deletedItem.getCard().get(i).getName(), deletedItem.getCard().get(i).getCardItems()));
//
//            }
//            deleted_item = new MasterPlannerProject();
//            deleted_item.setProjectName(deletedItem.getProjectName());
//            deleted_item.setCard(newCardList);
//            deleted_item.setCardCount(newCardList.size());
//            deleted_item.setCreatedAt(deletedItem.getCreatedAt());

            // deleted_item = new MasterPlannerProject(deletedItem.getProjectName(), deletedItem.getCardCount(), deleted_card, deletedItem.getCreatedAt());

//           Log.d(TAG, "===============onSwiped: todoItem: " + deleted_item.toString());
//           Log.d(TAG, "===============onSwiped: todoItem: "+ deletedItem.toString());--- ***this line crashes
//            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the todoItem from recycler view

            //---show alert dialog----//

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Delete Project")
                    .setMessage("Are you sure you want to delete this project?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // continue with delete
                        mProjectAdapter.removeItem(viewHolder.getAdapterPosition());
                        if (projectsList.size() == 0) tvPrjectMsg.setVisibility(View.VISIBLE);

                       //  showing snack bar with Undo option
                        Snackbar snackbar = Snackbar
                                .make(linearLayout, name + " removed from list!", Snackbar.LENGTH_LONG);
//                        snackbar.setAction("UNDO", view -> {
//
//                            // undo is selected, restore the deleted todoItem
////                Log.d(TAG, "==========onSwiped: redo todoItem: " + deleted_item.toString());
//
//                            mProjectAdapter.restoreItem(deleted_item, deletedIndex);
//
//                            deleted_item = null;
//                            deleted_card = null;
//                        });
//                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                        mProjectRecyclerView.getAdapter().notifyDataSetChanged();

                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.masterplanner_create_projcet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mpcreate) {
            createProject();
        }

        return super.onOptionsItemSelected(item);
    }

}
