package br.ufrr.eng2.kanban.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.ufrr.eng2.kanban.model.Projeto;

/**
 * Created by rafaelsa on 12/03/17.
 */

public class ProjetoController {
    public static void NewProject(Projeto projeto) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        String pId = myRef.push().getKey();
        myRef.child(pId).setValue(projeto);
    }

    public static void UpdateProject(String pId, Projeto projeto) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects/" + pId);
        myRef.setValue(projeto);
    }

}
