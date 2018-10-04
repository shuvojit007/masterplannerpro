package com.cruxbd.master_planner_pro.view.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.cruxbd.master_planner_pro.R;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItemFields;
import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by Mridul on 13-Apr-18.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = "WidgetDataProvider";
    Realm realm;

    //  List<TodoItem> todoItemList = SampleData.todoItemList;

    List<String> todo_list = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {

        Log.d(TAG, "WidgetDataProvider: ");
        mContext = context;

    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        initData();
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged: ");
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {

        Log.d(TAG, "getCount: " + todo_list.size());
        return todo_list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        /*RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        view.setTextViewText(android.R.id.text1, todo_list.get(position));
        return view;*/

        //Todo-scrum

        Log.d(TAG, "getViewAt: position = " + position);

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);

        remoteView.setTextViewText(R.id.tvTodo_widget, todo_list.get(position));

        //===================================================================================

        /*Intent i=new Intent();
        Bundle extras=new Bundle();
        extras.putString(WidgetProviderFavorite.sExtra_Guid, mGuid[position]);
        extras.putString(WidgetProviderFavorite.sExtra_Url, mUrl[position]);
        i.putExtras(extras);
        row.setOnClickFillInIntent(android.R.id.text1, i);*/


        // Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putInt(WidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        remoteView.setOnClickFillInIntent(R.id.tvTodo_widget, fillInIntent);

        //===================================================================================


        return remoteView;


    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {

        Log.d(TAG, "getItemId: position: " + position);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        Log.d(TAG, "hasStableIds: " + true);
        return true;
    }


    private void initData() {
        todo_list.clear();

        realm = Realm.getDefaultInstance();// realm open

        List<com.cruxbd.master_planner_pro.model.realm_model.TodoItem> todo_items = realm.where(com.cruxbd.master_planner_pro.model.realm_model.TodoItem.class)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();
        todo_list.clear();

        int len = todo_items.size();

        Log.d(TAG, "initData: length: " + len);


        for (int i = 0; i < len; i++) {
            todo_list.add(todo_items.get(i).getTask());
            Log.d(TAG, "initData: Todo: " + todo_list.get(i));
        }

        if (todo_list.size() == 0) todo_list.add("You have no to-do");

        Log.d(TAG, "initData: todo_list: " + todo_list.toString());
        realm.close();

//        if(todo_items!=null) {
//            for (com.cruxbd.todo_scrum.model.realm_model.TodoItem todo: todo_items){
//                todo_list.add(todo.getTask());
//            }
//        }

        //realm.close();

//        todo_list.clear();
//
//        int len = todoItemList.size();
//
//        Log.d(TAG, "initData: length: "+ len);
//
//
//        for (int i=0; i<len; i++){
//            todo_list.add(todoItemList.get(i).getTask());
//            Log.d(TAG, "initData: Todo: "+ todo_list.get(i));
//        }


//
//        for (int i = 1; i <= 10; i++) {
//            todo_list.add("ListView item " + i);
//        }
    }

}
