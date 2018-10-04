package com.cruxbd.master_planner_pro.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.App;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.CountdownAdapter;
import com.cruxbd.master_planner_pro.adapters.TodoItemAdapter;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.component.DaggerHomeFragmentComponent;
import com.cruxbd.master_planner_pro.di.home_fragment_feature.component.HomeFragmentComponent;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.models.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;
import com.cruxbd.master_planner_pro.utils.GetFirebaseRef;
import com.cruxbd.master_planner_pro.utils.ItemDecoration;
import com.cruxbd.master_planner_pro.utils.RecyclerItemTouchHelper;
import com.cruxbd.master_planner_pro.view.activities.CountdownViewAllActivity;
import com.cruxbd.master_planner_pro.view.activities.MainActivity;
import com.cruxbd.master_planner_pro.view.activities.tags_adapter.Tag_Model;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;



public class HomeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, View.OnTouchListener {

    private static final String TAG = "MyHomeFragment";
    private Context cn;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Inject
    Realm realm;
    private RealmService realmService;
    RecyclerView recycler;
    private List<TodoItem> ar;

    public static List<TodoItem> todoItemList;
    //    List<com.cruxbd.todo_scrum.model.realm_model.TodoItem> todoItems;
    private static TodoItemAdapter mAdapter;
    private LinearLayout linearLayout;

    //---- countdown ----//
    private List<CountdownModel> mCountdownModels;
    private CountdownAdapter mCountdownAdapter;
    private RecyclerView mCountdownRecyclerView;

    //Title
    public static TextView tvTitleCountdown, tvTitleTodo, tvMsgEmptyTodo, tvFilterText;
    TextView tvCountDownViewAll;
    public ImageView ivFilterIcon;


    static ViewGroup.LayoutParams params;

    Boolean chk = false;
    private static int px;

    private DatabaseReference mDm;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private String date;
    private String Name;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> listArray;

    private PopupMenu popupMenu;

    public HomeFragment() {
    }

    private View rootView;


    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        cn = rootView.getContext();
        setUpDagger2(cn);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(cn);

        px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 120,
                cn.getResources().getDisplayMetrics()
        );

        GetUserInformation();
        init(cn);


        return rootView;
    }

    private void GetUserInformation() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDm = GetFirebaseRef.GetDbIns().getReference()
                .child("Users")
                .child(mUser.getUid());

        mDm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Name = dataSnapshot.child("name").getValue().toString();
                date = dataSnapshot.child("member_since").getValue().toString();
//                StyleableToast.makeText(cn, Name, Toast.LENGTH_SHORT, R.style.mytoast ).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init(Context cn) {
        //----initialize views---//
        tvTitleCountdown = rootView.findViewById(R.id.tvTitleCountdown);
        tvCountDownViewAll = rootView.findViewById(R.id.tvCountDownViewAll);
        tvTitleTodo = rootView.findViewById(R.id.tvTitleTodo);
        tvMsgEmptyTodo = rootView.findViewById(R.id.tvMsgEmptyTodo);

        mCountdownRecyclerView = rootView.findViewById(R.id.rvCountdown);
        recycler = rootView.findViewById(R.id.rvTodoList);
        linearLayout = rootView.findViewById(R.id.linearLayout_home_fragment);
        tvFilterText = rootView.findViewById(R.id.tvFilterText);
        ivFilterIcon = rootView.findViewById(R.id.ivFilterIcon);

        //---- Todo:: show todo----//
        params = mCountdownRecyclerView.getLayoutParams();
        if (mCountdownModels.isEmpty()) {
            tvTitleCountdown.setVisibility(View.GONE);
            mCountdownRecyclerView.setVisibility(View.GONE);

            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mCountdownRecyclerView.setLayoutParams(params);

        } else {
            tvTitleCountdown.setVisibility(View.VISIBLE);
            mCountdownRecyclerView.setVisibility(View.VISIBLE);
            if (mCountdownModels.size() > 3) {
                params.height = (int) getResources().getDimension(R.dimen.countdown_rec_height);
                mCountdownRecyclerView.setLayoutParams(params);
            }
        }
        if (todoItemList.isEmpty()) {
            tvTitleTodo.setVisibility(View.GONE);
            recycler.setVisibility(View.GONE);
            tvMsgEmptyTodo.setVisibility(View.VISIBLE);
            tvFilterText.setVisibility(View.GONE);
            ivFilterIcon.setVisibility(View.GONE);

        } else {
            tvTitleTodo.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);
            tvMsgEmptyTodo.setVisibility(View.GONE);
            tvFilterText.setVisibility(View.VISIBLE);
            ivFilterIcon.setVisibility(View.VISIBLE);
        }


        mAdapter = new TodoItemAdapter(rootView.getContext(), todoItemList, realmService);
        recycler.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recycler.setAdapter(mAdapter);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.e("DY", "" + dy);
                if (dy > 0) {
                    MainActivity.Quick(1);
                } else {
                    MainActivity.Quick(0);
                }
            }
        });
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new ItemDecoration(5, 10, 10, 5));

        //---- Todo:: delete todo ------//
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler);
        setHasOptionsMenu(true);

        //----Todo:: countdown ------//
//        Log.d(TAG, "onCreateView: " + mCountdownModels.toString());
        mCountdownAdapter = new CountdownAdapter(mCountdownModels, cn);

        mCountdownRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        mCountdownRecyclerView.setAdapter(mCountdownAdapter);
        mCountdownRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCountdownRecyclerView.addItemDecoration(new ItemDecoration(5, 5, 10, 5));

        tvCountDownViewAll.setOnClickListener(v -> {
            startActivity(new Intent(cn, CountdownViewAllActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        });

    }

    public void settingUpCountdownRecyclerView() {
//        mCountdownModels = realmService.getCountdownModels();
        mCountdownModels = realmService.getActiveCountdownModels();

        if (mCountdownModels.isEmpty()) {
            tvTitleCountdown.setVisibility(View.GONE);
            mCountdownRecyclerView.setVisibility(View.GONE);
            tvCountDownViewAll.setVisibility(View.GONE);

            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mCountdownRecyclerView.setLayoutParams(params);

        } else {
            tvTitleCountdown.setVisibility(View.VISIBLE);
            mCountdownRecyclerView.setVisibility(View.VISIBLE);
            if (mCountdownModels.size() > 3) {
                tvCountDownViewAll.setVisibility(View.VISIBLE);
                params.height = (int) getResources().getDimension(R.dimen.countdown_rec_height);
                mCountdownRecyclerView.setLayoutParams(params);
            } else {
                tvCountDownViewAll.setVisibility(View.GONE);
            }
        }

        mCountdownAdapter = new CountdownAdapter(mCountdownModels, cn);
        mCountdownRecyclerView.setAdapter(mCountdownAdapter);

    }


    private void setUpDagger2(Context context) {
        //================Set Up Dagger 2 ==========================
        HomeFragmentComponent homeFragmentComponent =
                DaggerHomeFragmentComponent
                        .builder()
                        .appComponent(App.get((Activity) context).getAppComponent())
                        .build();
        homeFragmentComponent.injectHomeFragment((HomeFragment) HomeFragment.this);


        realmService = new RealmService(realm);
        todoItemList = realmService.GETTODOMODEL();
//        Log.d(TAG, "setUpDagger2: TodoItemList: " + realmService.GETTODOMODEL().toString());
//        mCountdownModels = realmService.getCountdownModels();
        mCountdownModels = realmService.getActiveCountdownModels();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof TodoItemAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = todoItemList.get(viewHolder.getAdapterPosition()).getTask();

            // backup of removed item for undo purpose
            final TodoItem deletedItem = todoItemList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();


            int pos = viewHolder.getAdapterPosition();
            todoItemList.remove(pos);
            if (todoItemList.isEmpty()) {
                tvTitleTodo.setVisibility(View.GONE);
                tvMsgEmptyTodo.setVisibility(View.VISIBLE);
                tvFilterText.setVisibility(View.GONE);
                ivFilterIcon.setVisibility(View.GONE);
            }
            recycler.getAdapter().notifyItemRemoved(pos);
            chk = false;

            //Todo=================SnackBar=====================//
            Snackbar snackbar = Snackbar
                    .make(linearLayout, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", view -> {

                mAdapter.restoreItem(deletedItem, deletedIndex);
                chk = true;
                tvTitleTodo.setVisibility(View.VISIBLE);
                tvMsgEmptyTodo.setVisibility(View.GONE);
                tvFilterText.setVisibility(View.VISIBLE);
                ivFilterIcon.setVisibility(View.VISIBLE);
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (chk == false)
                        mAdapter.removeItemByID(deletedItem.getId(), deletedItem.getAlarm_req_code());
                    settingUpCountdownRecyclerView();
                }
            });
            //Todo=================SnackBar=====================//
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);

//
//        MenuItem item = menu.findItem(R.id.menuFilterTag);
//
//
//        Spinner spinner = (Spinner) item.getActionView();
//
//        listArray = new ArrayList<>();
//        listArray.add("Default");
//        RealmResults<Tag_Model> res = RealmService.ReadTagList();
//
////        Log.d(TAG, "onCreateOptionsMenu: tags: " + res.toString());
//        for (Tag_Model data : res) {
//            listArray.add(data.getTagTitle());
//        }
//
////        Log.d(TAG, "onCreateOptionsMenu: listArray: " + listArray.toString());
//
//
//        listAdapter = new ArrayAdapter<>(cn, android.R.layout.simple_list_item_1, listArray);
//
//        spinner.setAdapter(listAdapter);
//
//        listAdapter.notifyDataSetChanged();


        //---------- Tag list

        try {
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // do your search on change or save the last string or...
                    final List<TodoItem> filteredModelList = filter(todoItemList, s);

                    mAdapter.setFilter(filteredModelList);
                    return false;
                }

                private List<TodoItem> filter(List<TodoItem> models, String query) {
                    query = query.toLowerCase();
                    final List<TodoItem> filteredModelList = new ArrayList<>();
                    for (TodoItem model : models) {
                        final String text = model.getTask().toLowerCase();
                        if (text.contains(query)) {
                            filteredModelList.add(model);
                        }
                    }
                    return filteredModelList;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFilterToday:
                tvFilterText.setText("Today");
                ivFilterIcon.setImageResource(R.drawable.filter_calendar_today);
                REGENERATE_RECYCLER_VIEW(realmService.getTodayTodos());
                break;

            case R.id.menuFilterTomorrow:
                tvFilterText.setText("Tomorrow");
                ivFilterIcon.setImageResource(R.drawable.filter_calendar);
                REGENERATE_RECYCLER_VIEW(realmService.getTomorrowTodos());
                break;
            case R.id.menuFilterWeek:
                tvFilterText.setText("Next 7 Days");
                ivFilterIcon.setImageResource(R.drawable.filter_calendar_today);
                REGENERATE_RECYCLER_VIEW(realmService.getWeekTodos());
                break;
            case R.id.menuFilterFinished:
                tvFilterText.setText("Finished");
                ivFilterIcon.setImageResource(R.drawable.filter_check_all);
                REGENERATE_RECYCLER_VIEW(realmService.getFinishedTodos());
                break;
            case R.id.menuFilterAll:
                tvFilterText.setText("All");
                ivFilterIcon.setImageResource(R.drawable.select_all);
                REGENERATE_RECYCLER_VIEW(realmService.getAllTodos());
                break;

            case R.id.menuFilterCountdown:
                tvFilterText.setText("Todo with Countdown");
                ivFilterIcon.setImageResource(R.drawable.filter_countdown);
                REGENERATE_RECYCLER_VIEW(realmService.getTodoWithCountdown());
                break;
            case R.id.menuFilterReminder:
                tvFilterText.setText("Todo with Reminder");
                ivFilterIcon.setImageResource(R.drawable.alarm_check);
                REGENERATE_RECYCLER_VIEW(realmService.getTodoWithReminder());
                break;

            case R.id.menuFilterTag:
                tvFilterText.setText("Filter by Tag");
                ivFilterIcon.setImageResource(R.drawable.format_list_bulleted);

                //---show popupMenu menu ----//

                popupMenu = new PopupMenu(cn, tvFilterText);
                popupMenu.getMenuInflater().inflate(R.menu.tag_filter_popup_menu, popupMenu.getMenu());
                popupMenu.setGravity(Gravity.END);

                popupMenu.getMenu().add(0, Menu.FIRST, Menu.NONE, "Default").setIcon(R.drawable.format_list_bulleted);

                RealmResults<Tag_Model> res = RealmService.ReadTagList();
                int i = 1;
                for (Tag_Model data : res) {

//                    Log.d(TAG, "onOptionsItemSelected: tag: "+ data.getTagTitle());
                    popupMenu.getMenu().add(0, Menu.FIRST + i, Menu.NONE, data.getTagTitle()).setIcon(R.drawable.format_list_bulleted);
                    i++;
                }

                popupMenu.setOnMenuItemClickListener(popup_item -> {
                    tvFilterText.setText("Filter by Tag: " + popup_item.getTitle().toString());
                    ivFilterIcon.setImageResource(R.drawable.format_list_bulleted);
                    REGENERATE_RECYCLER_VIEW(realmService.getTodoByTag(popup_item.getTitle().toString()));
                    return true;
                });

                popupMenu.show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    public static void REGENERATE_RECYCLER_VIEW(List<TodoItem> list) {
//        Log.d(TAG, "REGENERATE_RECYCLER_VIEW: List size: " + list.size());
//        Log.d(TAG, "REGENERATE_RECYCLER_VIEW: List: " + list.toString());
        if (list.isEmpty()) {
            tvMsgEmptyTodo.setVisibility(View.VISIBLE);
            tvTitleTodo.setVisibility(View.GONE);
        } else {
            tvMsgEmptyTodo.setVisibility(View.GONE);
            tvTitleTodo.setVisibility(View.VISIBLE);
        }
        todoItemList.clear();
        todoItemList.addAll(list);

        mAdapter.notifyDataSetChanged();

//        Log.d(TAG, "REGENERATE_RECYCLER_VIEW: " + todoItemList.size());

    }


    @Override
    public void onResume() {
        super.onResume();
        settingUpCountdownRecyclerView();
//        Log.d(TAG, "onResume: TodoModel: " + realmService.GETTODOMODEL().toString());

        REGENERATE_RECYCLER_VIEW(realmService.GETTODOMODEL());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


}

