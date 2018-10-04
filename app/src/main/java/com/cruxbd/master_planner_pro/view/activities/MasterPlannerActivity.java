package com.cruxbd.master_planner_pro.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.di.master_planner_activity.component.DaggerMasterPlannerActivityComponent;
import com.cruxbd.master_planner_pro.di.master_planner_activity.component.MasterPlannerActivityComponent;
import com.cruxbd.master_planner_pro.model.realm_model.Card;
import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.model.realm_model.MasterPlannerProject;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.view.fragments.MasterPlannerScrollViewFragment;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

import static com.cruxbd.master_planner_pro.adapters.MasterPlannerProjectsAdapter.PROJECT_KEY;
import static com.cruxbd.master_planner_pro.utils.StaticValues.CARD_POSITION;
import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;


public class MasterPlannerActivity extends AppCompatActivity {
    private static final String TAG = "MyMasterPlannerActivity";
    //Total page
    public static int pageCount = 2;
    private int card_position = 0;

    public static int VIEWPAGER_CARD_POSITION = 0;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.hollyViewPager)
    public HollyViewPager hollyViewPager;
    public static FragmentManager fragmentManager;

    MasterPlannerProject project;

    @Inject
    Realm realm;
    RealmService realmService;
    Context cn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_master_planner);
        ButterKnife.bind(this);
        cn = this;
        setupDagger2(cn);

        //==== get data from MasterPlannerProjectActivity ======//
        project = getIntent().getExtras().getParcelable(PROJECT_KEY);
        card_position = getIntent().getIntExtra(CARD_POSITION, 0);
//        Log.d(TAG, "onCreate: card_position: " + card_position);
        List<Card> cards = project.getCard();

        if (project != null) {
            pageCount = project.getCardCount();
        }

//        StyleableToast.makeText(this, "PageCount: " + pageCount, Toast.LENGTH_SHORT, R.style.mytoast).show();


        toolbar.setTitle(project.getProjectName());
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        fragmentManager = getSupportFragmentManager();

        hollyViewPager.getViewPager().setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));


        hollyViewPager.setConfigurator(page -> {
//            Log.d(TAG, "getHeightPercentForPage: page: " + page);
            return ((cards.get(page).getCardItems().size()) % 10) / 10f;
        });

        hollyViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return MasterPlannerScrollViewFragment.newInstance(project.getId(), cards.get(position), position);

            }

            @Override
            public int getCount() {
                return pageCount;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return project.getCard().get(position).getName();
            }
        });
//        Log.d(TAG, "onCreate: card_position: " + card_position);
        hollyViewPager.getViewPager().setCurrentItem(card_position);

        hollyViewPager.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                //Log.d(TAG, "onPageScrolled: viewpager: position: "+ position);
            }

            @Override
            public void onPageSelected(int position) {

                VIEWPAGER_CARD_POSITION = position;
//                Log.d(TAG, "onPageSelected: viewpager position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

//                // Log.d(TAG, "onPageScrollStateChanged: POSITION: "+ state);

            }
        });

    }


    private void setupDagger2(Context context) {
        MasterPlannerActivityComponent masterPlannerActivityComponent =
                DaggerMasterPlannerActivityComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        masterPlannerActivityComponent.injectMasterPlannerActivity((MasterPlannerActivity) context);

        realmService = new RealmService(realm);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.masterplanner_create_card, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mpcreateCard) {
            StyleableToast.makeText(this, "Card creating...", Toast.LENGTH_SHORT, R.style.mytoast).show();
            // profect-> card-> item
            // i have project, need add card then add item on this card

            //==create card todoItem for testing---should be removed==/
            RealmList<CardItems> cardItems = new RealmList<>();
            Card card = new Card();

            CardItems cardItem = new CardItems();
            cardItem.setDescription("Write something on card...");
            CountdownModel countdownModel = new CountdownModel();
            countdownModel.setProjectId(project.getId());
            countdownModel.setCardItemId(card.getId());
            countdownModel.setCountdownStatus(false);
            countdownModel.setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
            cardItem.setCountdown(countdownModel);

            cardItem.setHasCountdown(false);
            cardItem.setAmount(0.0);
            cardItem.setDone(false);
                                /*todoItem.setReminder(reminder);
                                todoItem.setLocation(location);*/
            cardItems.add(cardItem);

            //==create card ====

            cardItem.setCardId(card.getId());
            card.setProjectId(project.getId());
            card.setName("List " + (project.getCardCount() + 1));


            //==add card todoItem for testing--- should be removed==
            card.setCardItems(cardItems);
            //==add card in page ===
            project.getCard().add(project.getCardCount(), card);
            project.setCardCount(project.getCardCount() + 1);

//            Log.d(TAG, "onOptionsItemSelected: project: " + project.toString());

            realmService.addProjectCard(project);

            hollyViewPager.getViewPager().getAdapter().notifyDataSetChanged();

            Intent intent = getIntent();

            intent.putExtra(PROJECT_KEY, project);
            intent.putExtra(CARD_POSITION, project.getCardCount() - 1);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.toolbar)
    public void onToolbarClicked() {
        hollyViewPager.getViewPager().getAdapter().notifyDataSetChanged();
//        StyleableToast.makeText(this, "Toolbar clicked", Toast.LENGTH_SHORT, R.style.mytoast).show();

    }

    @OnClick(R.id.hollyViewPager)
    public void onHollyViewPagerClicked() {
        StyleableToast.makeText(this, "ViewPager Clicked", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }
}
