package br.ufrr.eng2.kanban.controller;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseController {
    private static FirebaseDatabase mFirebaseDatabase = null;

    public static FirebaseDatabase getInstance() {
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);
        }

        return mFirebaseDatabase;
    }
}
