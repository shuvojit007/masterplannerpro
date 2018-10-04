package com.cruxbd.master_planner_pro.utils;

import com.google.firebase.database.FirebaseDatabase;

public class GetFirebaseRef {
    private static FirebaseDatabase mFirebaseDatabase;


    public static FirebaseDatabase GetDbIns(){
        if (mFirebaseDatabase ==null){
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);
            return mFirebaseDatabase;
        }
        return mFirebaseDatabase;
    }


}
