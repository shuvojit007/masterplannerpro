package com.cruxbd.master_planner_pro.sample_data;

import com.cruxbd.master_planner_pro.sample_data.TodoItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mridul on 11-Apr-18.
 */

public class SampleData {
    public static List<TodoItem> todoItemList;
    public static Map<String, TodoItem> todoItemMap;

    static{

        todoItemList = new ArrayList<>();
        todoItemMap = new HashMap<>();

        List<Double> point = new ArrayList<>();
        point.add(25.124565);
        point.add(90.124565);
        Location location = new Location("point", point, "Zindabazar, Sylhet." );

        addItem(new TodoItem(location, false, false,
                "12 April 2018", "121548af54f", "45456dfa45", "Send Email", "Work"));

        addItem(new TodoItem(location, false, false,
                "12 April 2018", "121548af52f", "45456dfa45", "Buy Shirt", "Personal"));

        addItem(new TodoItem(location, false, false,
                "11 April 2018", "121548af40f", "45456dfa45", "Finish App", "Work"));

        addItem(new TodoItem(location, false, false,
                "11 April 2018", "121548af51f", "45456dfa45", "Read Book", "Personal"));

        addItem(new TodoItem(location, false, false,
                "11 April 2018", "121548af50f", "45456dfa45", "Cleaning", "Personal"));


        addItem(new TodoItem(location, false, false,
                "10 April 2018", "121548af39f", "45456dfa45", "Push code to Bitbucket", "Work"));

        addItem(new TodoItem(location, false, false,
                "10 April 2018", "121548af35f", "45456dfa45", "Debug app", "Work"));

        addItem(new TodoItem(location, false, false,
                "9 April 2018", "121548af30f", "45456dfa45", "Add Login system to app", "Work"));

        addItem(new TodoItem(location, false, false,
                "8 April 2018", "121548af23f", "45456dfa45", "Finish UI Design", "Work"));


    }

    private static void addItem(TodoItem item){

        todoItemList.add(item);
        todoItemMap.put(item.getId(), item);

    }

}
