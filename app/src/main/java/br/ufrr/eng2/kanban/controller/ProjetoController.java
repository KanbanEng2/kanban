package br.ufrr.eng2.kanban.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.ufrr.eng2.kanban.model.Projeto;

/**
 * Created by rafaelsa on 12/03/17.
 */

public class ProjetoController {
    public static String NewProject(Projeto projeto) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        List<String> membros = new ArrayList<>();
        membros.add(projeto.getOwnerUuid());
        projeto.setMembrosProjeto(membros);
        String pId = myRef.push().getKey();
        myRef.child(pId).setValue(projeto);

        UsuarioController.UpdateUserProjects(projeto.getOwnerUuid(), pId);
        return pId;
    }

    public static void RemoveProjectMember(String pId, Projeto projeto, String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        if (projeto.getMembrosProjeto() != null) {
            projeto.getMembrosProjeto().remove(userId);
        }

        myRef.child(pId).setValue(projeto);
        UsuarioController.RemoveUserFromProject(userId, pId);
    }

    public static void UpdateProject(String pId, Projeto projeto) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects/" + pId);
        myRef.setValue(projeto);
    }





}
