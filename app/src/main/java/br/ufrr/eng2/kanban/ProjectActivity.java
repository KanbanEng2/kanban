package br.ufrr.eng2.kanban;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.ufrr.eng2.kanban.adapter.UsuarioAdapter;
import br.ufrr.eng2.kanban.controller.FirebaseController;
import br.ufrr.eng2.kanban.controller.ProjetoController;
import br.ufrr.eng2.kanban.controller.UsuarioController;
import br.ufrr.eng2.kanban.model.Projeto;
import br.ufrr.eng2.kanban.model.Tarefa;
import br.ufrr.eng2.kanban.model.Usuario;
import br.ufrr.eng2.kanban.widget.RecyclerViewEmpty;

public class ProjectActivity extends AppCompatActivity {

    String projectId = "";
    private RecyclerViewEmpty mUsersRecyclerView;
    private RecyclerViewEmpty mMembersRecyclerView;
    private RecyclerView mOwnerRecyclerView;
    private UsuarioAdapter.ClickCallback mCallbackUsers;
    private UsuarioAdapter.ClickCallback mCallbackMembers;
    private UsuarioAdapter mUsersAdapter;
    private List<Usuario> mUsuariosList;
    private List<String> mUsuariosKeys;
    private List<Usuario> mMembersList;
    private List<String> mMembersKeys;
    private Projeto mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        FirebaseDatabase database = FirebaseController.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            projectId = b.getString("projectId", "");
        }

        DatabaseReference projectReference = database.getReference("projects");

        projectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals(projectId)) {
                        mProject = child.getValue(Projeto.class);
                        if (getSupportActionBar() != null)
                            getSupportActionBar().setTitle(mProject.getNomeProjeto());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Usuários do sistema para serem adicionados
        mUsersRecyclerView = (RecyclerViewEmpty) findViewById(R.id.add_members_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mUsersRecyclerView.setLayoutManager(mLayoutManager);
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mUsersRecyclerView.setEmptyView(findViewById(R.id.empty_add_members_text));

        // Membros deste projeto
        mMembersRecyclerView = (RecyclerViewEmpty) findViewById(R.id.current_members_list);
        mMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMembersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMembersRecyclerView.setEmptyView(findViewById(R.id.empty_members_list_text));

        // Proprietário do projeto
        mOwnerRecyclerView = (RecyclerView) findViewById(R.id.owners_list);
        mOwnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOwnerRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mCallbackUsers = new UsuarioAdapter.ClickCallback() {
            @Override
            public void onClick(View v, Usuario user) {
                int index = mUsuariosList.indexOf(user);
                onAddMember(mUsuariosKeys.get(index));
            }
        };

        mCallbackMembers = new UsuarioAdapter.ClickCallback() {
            @Override
            public void onClick(View v, Usuario user) {
                int index = mMembersList.indexOf(user);
                onRemoveMember(mMembersKeys.get(index));
            }
        };


        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsuariosList = new ArrayList<>();
                mUsuariosKeys = new ArrayList<>();

                mMembersList = new ArrayList<>();
                mMembersKeys = new ArrayList<>();

                ArrayList<Usuario> ownersList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Usuario user = postSnapshot.getValue(Usuario.class);

                    if (user.getProjetos() != null) {
                        if (user.getProjetos().contains(projectId)) {
                            // Não adiciona na lista se o usuário for o proprietário
                            if (mProject.getOwnerUuid().equals(postSnapshot.getKey())) {
                                ownersList.add(user);
                            } else {
                                mMembersList.add(user);
                                mMembersKeys.add(postSnapshot.getKey());
                            }
                        } else {
                            mUsuariosKeys.add(postSnapshot.getKey());
                            mUsuariosList.add(user);
                        }
                    } else {
                        mUsuariosKeys.add(postSnapshot.getKey());
                        mUsuariosList.add(user);
                    }
                }

                mUsersAdapter = new UsuarioAdapter(mUsuariosList, mCallbackUsers);
                mUsersRecyclerView.setAdapter(mUsersAdapter);


                mMembersRecyclerView.setAdapter(new UsuarioAdapter(mMembersList, mCallbackMembers, UsuarioAdapter.ACTION_REMOVE));
                mOwnerRecyclerView.setAdapter(new UsuarioAdapter(ownersList, null, UsuarioAdapter.ACTION_NONE));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        DatabaseReference userReference = database.getReference("user");
        userReference.addValueEventListener(usersListener);

    }

    private void onAddMember(String key) {
        Intent i = new Intent();
        i.putExtra("user", key);
        i.putExtra("remove", false);

        UsuarioController.UpdateUserProjects(key, projectId);
        mProject.getMembrosProjeto().add(key);

        ProjetoController.UpdateProject(projectId, mProject);

        setResult(1011, i);
    }

    private void onRemoveMember(String key) {
        Intent i = new Intent();
        i.putExtra("user", key);
        i.putExtra("remove", true);

        UsuarioController.RemoveUserFromProject(key, projectId);
        mProject.getMembrosProjeto().remove(key);
        for (Tarefa tarefa : mProject.getTarefasProjeto()) {
            // Desatribui tarefas do membro removido
            if (tarefa.getOwnedId() != null && tarefa.getOwnedId().equals(key)) {
                tarefa.setOwnedId(null);
            }
        }

        ProjetoController.UpdateProject(projectId, mProject);

        setResult(1011, i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }
}
