package br.ufrr.eng2.kanban.controller;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.ufrr.eng2.kanban.model.Usuario;

/**
 * Created by rafaelsa on 12/03/17.
 */

public class UsuarioController {
    public static void NewUser(String Id, String name, String url) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user/" + Id);
        Usuario user = new Usuario(name, url);
        myRef.setValue(user);
    }

    public static void GetUser(String Id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String userId = Id;
        DatabaseReference myRef = database.getReference("user").child(userId);
        myRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Usuario user = dataSnapshot.getValue(Usuario.class);
                        Log.d("Firebase", user.getNomeUsuario());

                        // ...
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }

}
