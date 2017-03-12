package br.ufrr.eng2.kanban.controller;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.ufrr.eng2.kanban.model.Projeto;
import br.ufrr.eng2.kanban.model.Usuario;

/**
 * Created by rafaelsa on 12/03/17.
 */

public class UsuarioController {
    public static void NewUser(String Id, Usuario user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user/" + Id);
        myRef.setValue(user);
    }


    public static void UpdateUserProjects(String Id, String pId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        final String userId = Id;
        final String projectId = pId;
        myRef.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Usuario user = dataSnapshot.getValue(Usuario.class);
                        if(user.getProjetos() == null) {
                            user.setProjetos(new ArrayList<String>()) ;
                        }
                        user.getProjetos().add(projectId);
                        NewUser(userId, user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }

}
