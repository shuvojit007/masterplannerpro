package com.cruxbd.master_planner_pro.view.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.adapters.TodoItemAdapter;
import com.cruxbd.master_planner_pro.models.TodoItem;
import com.cruxbd.master_planner_pro.realm_service.RealmService;

import java.util.List;

import io.realm.Realm;

public class LockTodoListActivity extends AppCompatActivity {
    private static final String TAG = "MyLockTodoListActivity";
    TodoItemAdapter adp;
    Toolbar tl;
    RecyclerView tlrec;
    Context cn;
    Realm realm;
    RealmService realmService;
    TextView tvMsgEmptyTodo;

    public  List<TodoItem> todoItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_todo_list);
        cn = this;
        realm = Realm.getDefaultInstance();

        todoItemList = RealmService.getLockTodo(realm);
        init(cn);

    }

    public void init(Context cn){

        tl = findViewById(R.id.tl_lock_todo);
        tl.setTitle("Locked To-do");
        tl.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvMsgEmptyTodo = findViewById(R.id.tvMsgEmptyLockTodo);
        tlrec = findViewById(R.id.lock_todo_rec);
        tlrec.setHasFixedSize(true);
        tlrec.setLayoutManager(new LinearLayoutManager(cn));
        realmService = new RealmService(realm);

        if(todoItemList.size()==0) tvMsgEmptyTodo.setVisibility(View.VISIBLE);
        else tvMsgEmptyTodo.setVisibility(View.GONE);

        adp = new TodoItemAdapter(cn, todoItemList, realmService);
        tlrec.setItemAnimator(new DefaultItemAnimator());

        tlrec.setAdapter(adp);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<TodoItem> list = RealmService.getLockTodo(realm);

//        Log.d(TAG, "onResume: list size: "+ list.size());
//        Log.d(TAG, "onResume: list: " + list.toString());

        if (list.isEmpty()) {
            tvMsgEmptyTodo.setVisibility(View.VISIBLE);
        } else {
            tvMsgEmptyTodo.setVisibility(View.GONE);
        }

        todoItemList.clear();
        todoItemList.addAll(list);
        adp.notifyDataSetChanged();
//        Log.d(TAG, "onResume: todoItemList: " + todoItemList);
    }
}
