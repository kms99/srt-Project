package com.example.srt_project;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class FindFireStore {
    public static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public boolean getCollection (String id) {
        String[] array_id = id.split("");
        String check_id ="";
        for(int i = 0 ; i<4; i++){
            check_id += array_id[i];
        }

        Log.d("please",check_id+check_id.length());
        if(check_id.equals("srt")) {
            return true;
        }
        return false;
    }

}
