package com.cruxbd.master_planner_pro.realm_service;

import android.util.Log;


import com.cruxbd.master_planner_pro.model.realm_model.Card;
import com.cruxbd.master_planner_pro.model.realm_model.CardFields;
import com.cruxbd.master_planner_pro.model.realm_model.CardItems;
import com.cruxbd.master_planner_pro.model.realm_model.CardItemsFields;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModel;
import com.cruxbd.master_planner_pro.model.realm_model.CountdownModelFields;
import com.cruxbd.master_planner_pro.model.realm_model.LocationModel;
import com.cruxbd.master_planner_pro.model.realm_model.LocationModelFields;
import com.cruxbd.master_planner_pro.model.realm_model.MasterPlannerProject;
import com.cruxbd.master_planner_pro.model.realm_model.MasterPlannerProjectFields;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItem;
import com.cruxbd.master_planner_pro.model.realm_model.TodoItemFields;
import com.cruxbd.master_planner_pro.model.realm_model.TodoReminder;
import com.cruxbd.master_planner_pro.model.realm_model.TodoReminderFields;
import com.cruxbd.master_planner_pro.view.activities.tags_adapter.Tag_Model;
import com.cruxbd.master_planner_pro.view.activities.tags_adapter.Tag_ModelFields;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.cruxbd.master_planner_pro.utils.StaticValues.MASTER_PLANNER_COUNTDOWN;
import static com.cruxbd.master_planner_pro.utils.StaticValues.NORMAL_COUNTDOWN;

public class RealmService {
    public static List<TodoItem> td;
    public static List<com.cruxbd.master_planner_pro.models.TodoItem> tdd;


    public List<com.cruxbd.master_planner_pro.models.TodoItem> GETTODOMODEL() {

        tdd = new ArrayList<>();
        for (TodoItem item : td) {


            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }
        return tdd;
    }


    private static Realm realm;
    private static String TAG = "RealmService";

    public RealmService(Realm realm) {
        this.realm = realm;
        td = readTodos();
    }

    public static List<TodoItem> getTd() {
        return td;
    }

    public static RealmResults<Tag_Model> ReadTagList() {

        RealmResults<Tag_Model> result2 = realm.where(Tag_Model.class)
                .findAll();
        return result2;
    }

    public static void Delete(Tag_Model tag_model) {
        Tag_Model item = realm.where(Tag_Model.class).equalTo(Tag_ModelFields.TAG_TITLE, tag_model.getTagTitle()).findFirst();
        realm.executeTransaction(realm -> {
            assert item != null;
            item.deleteFromRealm();
        });
    }

    public static void AddTag(String tagname) {
        realm.executeTransaction(realm -> {

            Tag_Model tag_model1 = realm.createObject(Tag_Model.class);
            tag_model1.setTagTitle(tagname);
        });
    }

    public List<TodoItem> readTodos() {
        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .isNotNull(TodoItemFields.HAS_COUNTDOWN)
                .equalTo(TodoItemFields.LOCKED, false)
                .sort(TodoItemFields.DONE, Sort.ASCENDING)
                .findAll();
        return todoItems;
    }

    public void addTodo(final TodoItem todoItem) {

        realm.executeTransaction(realm1 -> {
            realm1.insertOrUpdate(todoItem);
        });
    }

    public void deletTodoListItem(TodoItem todoItem) {
        Log.d(TAG, "deletTodoListItem: " + todoItem.toString());
        Log.d(TAG, "deletTodoListItem: realm:" + realm.toString());
        Log.d(TAG, "deletTodoListItem: id: " + TodoItemFields.ID);

        TodoItem item = realm.where(TodoItem.class).equalTo(TodoItemFields.ID, todoItem.getId()).findFirst();
        realm.executeTransaction(realm -> {
            assert item != null;
            item.deleteFromRealm();
        });
    }

    public void deletTodoListItemById(String Id) {
        TodoItem item = realm.where(TodoItem.class).equalTo(TodoItemFields.ID, Id).findFirst();
        if (item != null) {
            Log.d(TAG, "deletTodoListItemById: " + item.toString());
            if (item.getHasCountdown()) {
                CountdownModel countdownModel = realm.where(CountdownModel.class).equalTo(CountdownModelFields.ID, item.getCountdown().getId()).findFirst();
                realm.executeTransaction(realm -> {
                    assert countdownModel != null;
                    countdownModel.deleteFromRealm();
                    item.deleteFromRealm();
                });

            } else {
                realm.executeTransaction(realm1 -> {
                    item.deleteFromRealm();
                });
            }
        }
    }

    public void addDeletedItem(TodoItem todoItem) {

        realm.executeTransaction(realm -> realm.insert(todoItem));
    }

    public void CloseRealm(Realm realm) {
        realm.close();
    }

    //todo========== Master Planner ================//
    public List<MasterPlannerProject> getAllMasterPlannerProjects() {
        RealmResults<MasterPlannerProject> projects = realm.where(MasterPlannerProject.class)
                .sort(MasterPlannerProjectFields.CREATED_AT, Sort.ASCENDING)
                .findAll();
        return projects;
    }

    public void addMasterPlannerProject(MasterPlannerProject project) {
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(project));
    }

    //---DELETE PROJECT :: OK ----//
    public void deleteProjectListItem(MasterPlannerProject projectItem) {

        realm.executeTransaction(realm1 -> {
            MasterPlannerProject project = realm1.where(MasterPlannerProject.class)
                    .equalTo(MasterPlannerProjectFields.ID, projectItem.getId())
                    .findFirst();
            List<Card> cards = project.getCard();

            for (Card card : cards) {
                List<CardItems> cardItems = card.getCardItems();
                for (CardItems item : cardItems) {
                    if (item.getLocation() != null)
                        item.getLocation().deleteFromRealm();
                    if (item.getReminder() != null)
                        item.getReminder().deleteFromRealm();
                    if (item.getCountdown() != null)
                        item.getCountdown().deleteFromRealm();
                    item.deleteFromRealm();
                    if (cardItems.size() == 0) break;
                }

                card.deleteFromRealm();
            }
            project.deleteFromRealm();
        });
    }

    public void restoreDeletedProjectMP(MasterPlannerProject project) {

        RealmList<Card> cards = project.getCard();

        Log.d(TAG, "restoreDeletedProjectMP: " + cards.toString());



        realm.executeTransaction(realm1 -> {
            MasterPlannerProject project1 = realm1.createObject(MasterPlannerProject.class, UUID.randomUUID().toString());

            project1.setProjectName(project.getProjectName());
            project1.setCardCount(project.getCardCount());
            project1.setCard(project.getCard());
            project1.setCreatedAt(project.getCreatedAt());
            realm1.insertOrUpdate(project1);
        });
    }

    public List<CardItems> getCardItems(String cardId) {

        Card card = realm.where(Card.class)
                .equalTo(CardFields.ID, cardId)
                .findFirst();

        return card.getCardItems();
    }

    public void addCardItem(CardItems cardItem, String cardID) {
        realm.executeTransaction(realm1 -> realm1.where(Card.class)
                .equalTo(CardFields.ID, cardID)
                .findFirst().getCardItems().add(cardItem));
    }

    public float getTotalAmount(String cardId) {
        Card card = realm.where(Card.class)
                .equalTo(CardFields.ID, cardId)
                .findFirst();


       /* return card.getCardItems().where()
                .sum(CardItemsFields.AMOUNT).floatValue();*/

        return card.getCardItems().where().notEqualTo(CardItemsFields.DONE, true)
                .sum(CardItemsFields.AMOUNT).floatValue();
    }

    public void updateCardItem(CardItems cardItem) {

        boolean delete_reminder = false;
        if (cardItem.getReminder() != null) {
            if (!cardItem.getReminder().getReminderEnabled()) {
                delete_reminder = true;
            }
        }

        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(cardItem));

        if (delete_reminder) {
            realm.executeTransaction(realm1 -> {
                CardItems item = realm1.where(CardItems.class)
                        .equalTo(CardItemsFields.ID, cardItem.getId())
                        .findFirst();
                if (item != null) {
                    item.getReminder().deleteFromRealm();
                }
            });
        }
    }

    //===== Edit Card Title ========//
    public void updateCardTitle(String cardId, String cardName) {
        Card card = realm.where(Card.class)
                .equalTo(CardFields.ID, cardId)
                .findFirst();
        realm.executeTransaction(realm1 -> card.setName(cardName));
    }


    //======= todo LocationBasedReminder=======//
    public static List<LocationModel> getLocationModel(Realm rr) {
        RealmResults<LocationModel> locationModels = rr.where(LocationModel.class).findAll();
        return locationModels;
    }

    public static void saveLocationReminder(LocationModel locationModel, Realm rr) {

        rr.executeTransaction(realm1 -> realm1.insertOrUpdate(locationModel));
    }

    public static LocationModel getNotificationDetails(String requestId, Realm rr) {

        return rr.where(LocationModel.class).equalTo(LocationModelFields.KEY, requestId)
                .findFirst();

    }
    //======= todo LocationBasedReminder=======//


    public void setTodoDone(String todoId, boolean done) {
        TodoItem item = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.ID, todoId)
                .findFirst();

        realm.executeTransaction(realm1 -> item.setDone(done));
    }

    public List<CountdownModel> getCountdownModels() {

        return realm.where(CountdownModel.class)
                .isNotNull(CountdownModelFields.TITLE)
                .isNotNull(CountdownModelFields.COUNT_DOWN_TIME)
                .isNotNull(CountdownModelFields.COUNTDOWN_FROM)
                .sort(CountdownModelFields.COUNT_DOWN_TIME, Sort.ASCENDING)
                .findAll();
    }

    public TodoItem getTodoItem(String todo_id) {
        TodoItem todoItem = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.ID, todo_id)
                .findFirst();
        return todoItem;
    }

    public void setCardItemDone(String itemId, boolean done) {
        CardItems item = realm.where(CardItems.class)
                .equalTo(CardItemsFields.ID, itemId)
                .findFirst();
        realm.executeTransaction(realm1 -> item.setDone(done));
    }

    public void createPlannerCountdown(String projectId, String cardItemId, String title, Date countdownDate) {
        CardItems cardItem = realm.where(CardItems.class)
                .equalTo(CardItemsFields.ID, cardItemId)
                .findFirst();

        realm.executeTransaction(realm1 -> {
            if (cardItem.getCountdown() != null) {
                cardItem.getCountdown().setProjectId(projectId);
                cardItem.getCountdown().setCardItemId(cardItemId);
                cardItem.getCountdown().setTitle(title);
                cardItem.getCountdown().setCountDownTime(countdownDate);
                cardItem.getCountdown().setCountdownStatus(true);
                cardItem.getCountdown().setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
            } else {
                CountdownModel countdownModel = realm1.createObject(CountdownModel.class, UUID.randomUUID().toString() );
                countdownModel.setProjectId(projectId);
                countdownModel.setCardItemId(cardItemId);
                countdownModel.setTitle(title);
                countdownModel.setCountDownTime(countdownDate);
                countdownModel.setCountdownStatus(true);
                countdownModel.setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
                cardItem.setCountdown(countdownModel);
            }

            cardItem.setHasCountdown(true);


//            if(cardItem.getHasCountdown()){
//                //already has countdown
//                cardItem.getCountdown().setTitle(title);
//                cardItem.getCountdown().setCountDownTime(countdownDate);
//                cardItem.getCountdown().setCountdownStatus(true);
//
//            }else{
//                if(cardItem.getCountdown()!=null){
//                    cardItem.getCountdown().setProjectId(projectId);
//                    cardItem.getCountdown().setCardItemId(cardItemId);
//                    cardItem.getCountdown().setTitle(title);
//                    cardItem.getCountdown().setCountDownTime(countdownDate);
//                    cardItem.getCountdown().setCountdownStatus(true);
//                    cardItem.getCountdown().setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
//
//                }else{
//                    CountdownModel countdownModel = new CountdownModel();
//                    countdownModel.setProjectId(projectId);
//                    countdownModel.setCardItemId(cardItemId);
//                    countdownModel.setTitle(title);
//                    countdownModel.setCountDownTime(countdownDate);
//                    countdownModel.setCountdownStatus(true);
//                    countdownModel.setCountdownFrom(MASTER_PLANNER_COUNTDOWN);
//
//                   realm1.executeTransaction(realm2 -> {
//                       cardItem.setCountdown(countdownModel);
//                   });
//                    return;
//                }
//            }
//            assert cardItem != null;
//            if (cardItem.getCountdown() != null) {
//                cardItem.getCountdown().setTitle(title);
//                cardItem.getCountdown().setCountdownStatus(true);
//                cardItem.getCountdown().setCountDownTime(countdownDate);
//                cardItem.getCountdown().setProjectId(projectId);
//                cardItem.getCountdown().setCardItemId(cardItemId);
//            }
            realm1.insertOrUpdate(cardItem);
        });
    }

    public void createTodoCountdown(String todoId, String title, Date countdownDate) {

        TodoItem todoItem = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.ID, todoId)
                .findFirst();

        realm.executeTransaction(realm1 -> {
            assert todoItem != null;
            if (todoItem.getCountdown() != null) {
                todoItem.getCountdown().setTitle(title);
                todoItem.getCountdown().setCountdownStatus(true);
                todoItem.getCountdown().setCountDownTime(countdownDate);
                todoItem.getCountdown().setProjectId(todoItem.getId());
            }


            realm1.insertOrUpdate(todoItem);
        });


    }

    public void createNormalCountdown(String title, Date countdownExpireDate) {
        CountdownModel countdownModel = new CountdownModel(NORMAL_COUNTDOWN, title, countdownExpireDate, true);
        realm.executeTransaction(realm1 -> realm1.insert(countdownModel));
    }

    public void updateCountdown(String id, String title, Date countdownExpireDate) {

        CountdownModel countdownModel = realm.where(CountdownModel.class).equalTo(CountdownModelFields.ID, id).findFirst();

        realm.executeTransaction(realm1 -> {
            countdownModel.setTitle(title);
            countdownModel.setCountDownTime(countdownExpireDate);
        });

    }

    public void deleteCountdown(String countdownId) {
        CountdownModel countdownModel = realm.where(CountdownModel.class)
                .equalTo(CountdownModelFields.ID, countdownId)
                .findFirst();

        realm.executeTransaction(realm1 -> countdownModel.deleteFromRealm());
    }

    public void deleteMasterPlannerCountdown(String countdownId, String cardItemId) {

        CountdownModel countdownModel = realm.where(CountdownModel.class)
                .equalTo(CountdownModelFields.ID, countdownId)
                .findFirst();

        CardItems item = realm.where(CardItems.class)
                .equalTo(CardItemsFields.ID, cardItemId)
                .findFirst();

        realm.executeTransaction(realm1 -> {
            assert item != null;
            item.getCountdown().deleteFromRealm();
            item.setHasCountdown(false);
//            assert countdownModel != null;
//            countdownModel.deleteFromRealm();
        });

    }

    public void deleteTodoCountdown(String countdownId, String todoId) {

        TodoItem item = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.ID, todoId)
                .findFirst();

        realm.executeTransaction(realm1 -> {

            assert item != null;

            if (item.getHasCountdown()) {

                if (item.getHasCountdown()) {
                    item.getCountdown().deleteFromRealm();
                    item.setHasCountdown(false);
                }
            }

//            assert countdownModel != null;
//            countdownModel.deleteFromRealm();
        });
    }

    public MasterPlannerProject getMasterPlannerProject(String projectId) {

        MasterPlannerProject project = realm.where(MasterPlannerProject.class)
                .equalTo(MasterPlannerProjectFields.ID, projectId)
                .findFirst();
        return project;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getTodayTodos() {

        long date = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date dateOne = new Date(calendar.getTimeInMillis());

        Date dateTwo = new Date(calendar.getTimeInMillis() + 1000 * 60 * 60 * 24);

        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .greaterThanOrEqualTo(TodoItemFields.DUE_DATE, dateOne)
                .equalTo(TodoItemFields.LOCKED, false)
                .lessThan(TodoItemFields.DUE_DATE, dateTwo)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getTomorrowTodos() {

        long date = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        Date dateOne = new Date(calendar.getTimeInMillis());

        Date dateTwo = new Date(calendar.getTimeInMillis() + 1000 * 60 * 60 * 24 * 2);

        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .greaterThanOrEqualTo(TodoItemFields.DUE_DATE, dateOne)
                .lessThan(TodoItemFields.DUE_DATE, dateTwo)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getAllTodos() {

        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .sort(TodoItemFields.DONE, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getWeekTodos() {
        long date = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        Date dateOne = new Date(calendar.getTimeInMillis());

        Date dateTwo = new Date(calendar.getTimeInMillis() + 1000 * 60 * 60 * 24 * 7);

        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .greaterThanOrEqualTo(TodoItemFields.DUE_DATE, dateOne)
                .lessThan(TodoItemFields.DUE_DATE, dateTwo)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getFinishedTodos() {
        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .equalTo(TodoItemFields.DONE, true)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getTodoWithCountdown() {
        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .equalTo(TodoItemFields.HAS_COUNTDOWN, true)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getTodoWithReminder() {
        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .isNotNull(TodoItemFields.REMIND_DATE)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public List<com.cruxbd.master_planner_pro.models.TodoItem> getTodoByTag(String tag) {
        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class)
                .equalTo(TodoItemFields.LOCKED, false)
                .equalTo(TodoItemFields.LIST, tag)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public void addProjectCard(MasterPlannerProject project) {

        MasterPlannerProject plannerProject = realm.where(MasterPlannerProject.class)
                .equalTo(MasterPlannerProjectFields.ID, project.getId())
                .findFirst();

        realm.executeTransaction(realm1 -> {
            if (plannerProject != null) {
                realm1.insertOrUpdate(project);
            }
        });


    }

    public static void deleteLocationReminder(LocationModel locationModel, Realm rr) {
        final LocationModel model = rr.where(LocationModel.class)
                .equalTo(LocationModelFields.ID, locationModel.getId())
                .findFirst();

        rr.executeTransaction(realm1 -> model.deleteFromRealm());
    }

    public static void restoreDeletedLocationReminder(LocationModel locationItem, Realm rr) {
        rr.executeTransaction(realm1 -> realm1.insert(locationItem));
    }
    //--- DELETE CARD ITEM :: TESTING ----//

    public void deleteCardItem(String itemId, String cardId) {

        Card card = realm.where(Card.class).equalTo(CardFields.ID, cardId).findFirst();

        if (card != null) {
            CardItems cardItem = card.getCardItems().where().equalTo(CardItemsFields.ID, itemId).findFirst();

//        CardItems cardItems = realm.where(CardItems.class).equalTo(CardItemsFields.ID, itemId).findFirst();

            realm.executeTransaction(realm1 -> {
                if (cardItem != null) {

                    if (cardItem.getCountdown() != null) {
                        cardItem.getCountdown().deleteFromRealm();
                    }
                    if (cardItem.getReminder() != null) {
                        cardItem.getReminder().deleteFromRealm();
                    }
                    if (cardItem.getLocation() != null) {
                        cardItem.getLocation().deleteFromRealm();
                    }

                    cardItem.deleteFromRealm();

                }


            });

        }
    }

    public void deleteCard(String cardID, String projectID) {
        MasterPlannerProject project = realm.where(MasterPlannerProject.class).equalTo(MasterPlannerProjectFields.ID, projectID).findFirst();

        realm.executeTransaction(realm1 -> {
            Card card = project.getCard().where().equalTo(CardFields.ID, cardID).findFirst();

            List<CardItems> cardItem = card.getCardItems();

            for (CardItems item : cardItem) {
                if (item.getLocation() != null) item.getLocation().deleteFromRealm();
                if (item.getReminder() != null) item.getReminder().deleteFromRealm();
                if (item.getCountdown() != null) item.getCountdown().deleteFromRealm();

                item.deleteFromRealm();
                if (cardItem.size() == 0) break;
            }
            card.deleteFromRealm();
            project.setCardCount(project.getCardCount() - 1);
        });

//        assert project != null;
//        Card card = project.getCard().where().equalTo(CardFields.ID, cardID).findFirst();
//
//        Log.d(TAG, "deleteCard: card Details: "+ card.getName()+ card.getCardItems().toString());
//
//        assert card != null;
////        List<CardItems> itemsList = card.getCardItems();
//        List<CardItems> itemsList = realm.where(CardItems.class).equalTo(CardItemsFields.CARD_ID, cardID).findAll();
//
//        for (CardItems item : itemsList){
//            if(item!=null){
//                if(item.getHasCountdown()){
//                    CountdownModel countdownModel = item.getCountdown();
//                    countdownModel.deleteFromRealm();
//                }
//                if(item.getReminder() != null){
//                    TodoReminder reminder = item.getReminder();
//                    reminder.deleteFromRealm();
//                }
//
//                realm.executeTransaction(realm1 -> item.deleteFromRealm());
//
//
//            }
//        }
//
//
//        realm.executeTransaction(realm1 -> card.deleteFromRealm());
//
//        project.setCardCount(project.getCardCount()-1);


    }


    public static List<String> getTodoList(Realm r) {
        List<String> todo_list = new ArrayList<>();

        List<TodoItem> todo_items = r.where(TodoItem.class)
                .equalTo(TodoItemFields.DONE, false)
                .sort(TodoItemFields.CREATED_AT, Sort.ASCENDING)
                .findAll();

        int i = 0;
        if (todo_items != null)
            for (TodoItem todo : todo_items) {
                todo_list.add(todo.getTask());
                Log.d(TAG, "getTodoList: " + todo_list.get(i));
                i++;
            }
        return todo_list;
    }


    public RealmResults<TodoItem> getAllTodoAlarm() {

        return realm.where(TodoItem.class)
                .greaterThanOrEqualTo(TodoItemFields.REMIND_DATE, new Date(System.currentTimeMillis()))
                .equalTo(TodoItemFields.ALARM_STATUS, true)
                .sort(TodoItemFields.REMIND_DATE, Sort.ASCENDING)
                .findAll();
    }

    public RealmResults<TodoReminder> getAllPlannerReminder() {
        return realm.where(TodoReminder.class)
                .greaterThanOrEqualTo(TodoReminderFields.REMIND_DATE, new Date(System.currentTimeMillis()))
                .equalTo(TodoReminderFields.REMINDER_ENABLED, true)
                .sort(TodoReminderFields.REMIND_DATE, Sort.ASCENDING)
                .findAll();
    }

    public static List<com.cruxbd.master_planner_pro.models.TodoItem> getLockTodo(Realm realm) {


        RealmResults<TodoItem> todoItems = realm.where(TodoItem.class).equalTo(TodoItemFields.LOCKED, true).findAll();

        tdd = new ArrayList<>();
        for (TodoItem item : todoItems) {
            tdd.add(new com.cruxbd.master_planner_pro.models.TodoItem(item.getId(), item.getUserId(), item.getTask(),
                    item.getNote(), item.getDueDate(), item.getRemindDate(), item.getRepeatEnabled(), item.getRepeatType(),
                    item.getRepeatCustom(), item.getAlarm_req_code(), item.getAlarm_status(), item.getList(), item.getPriority(), item.isLocked(), item.getLocationName(),
                    item.getLat(), item.getLng(), item.getDone(), item.getDeleted(), item.getHasCountdown(), item.getCountdown(),
                    item.getCreatedAt()));
        }

        return tdd;
    }

    public String getCardTitle(String cardId) {
        return realm.where(Card.class).equalTo(CardFields.ID, cardId).findFirst().getName();
    }

    public void updateProjectName(String id, String projectName) {
        MasterPlannerProject project = realm.where(MasterPlannerProject.class)
                                            .equalTo(MasterPlannerProjectFields.ID, id)
                                            .findFirst();

        realm.executeTransaction(realm1 -> project.setProjectName(projectName));
    }

    public void disableTodoAlarm(int alarm_req_code) {
        realm.executeTransaction(realm1 -> Objects.requireNonNull(realm1.where(TodoItem.class).equalTo(TodoItemFields.ALARM_REQ_CODE, alarm_req_code)
                .findFirst()).setAlarm_status(false));
    }

    public void deletePlannerCountdown(String cardItemId, String countdownId) {

        CardItems cardItem = realm.where(CardItems.class).equalTo(CardItemsFields.ID, cardItemId).findFirst();

        CountdownModel countdownModel = realm.where(CountdownModel.class)
                .equalTo(CountdownModelFields.ID, countdownId)
                .findFirst();

        realm.executeTransaction(realm1 -> {
            if (cardItem != null) {
                cardItem.setHasCountdown(false);
            }
            if (countdownModel != null) {
                countdownModel.deleteFromRealm();
            }
        });

    }

    public List<CountdownModel> getActiveCountdownModels() {

        Date date = Calendar.getInstance().getTime();

        return realm.where(CountdownModel.class)
                .isNotNull(CountdownModelFields.TITLE)
                .isNotNull(CountdownModelFields.COUNT_DOWN_TIME)
                .isNotNull(CountdownModelFields.COUNTDOWN_FROM)
                .greaterThan(CountdownModelFields.COUNT_DOWN_TIME, date)
                .sort(CountdownModelFields.COUNT_DOWN_TIME, Sort.ASCENDING)
                .findAll();
    }

    public int getCountdownCount() {
        return getCountdownModels().size();
    }
}