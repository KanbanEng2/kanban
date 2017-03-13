package br.ufrr.eng2.kanban;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.ufrr.eng2.kanban.adapter.UsuarioAdapter;
import br.ufrr.eng2.kanban.model.Projeto;
import br.ufrr.eng2.kanban.model.Usuario;
import br.ufrr.eng2.kanban.widget.RecyclerViewEmpty;

public class ProjectActivity extends AppCompatActivity {

    private RecyclerViewEmpty mRecyclerView;
    private UsuarioAdapter.ClickCallback mCallbackUsers;
    private UsuarioAdapter.ClickCallback mCallbackMembers;
    private UsuarioAdapter mAdapter;
    private List<Usuario> mUsuario;
    private List<String> mKeys;
    private List<Usuario> members;
    private List<String> membersKeys;
    private RecyclerViewEmpty membersRecyclerView;

    String projectId = "";
    private Projeto mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                            getSupportActionBar().setTitle(String.format(getString(R.string.project_members_title), mProject.getNomeProjeto()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // Usuários do sistema para serem adicionados
        mRecyclerView = (RecyclerViewEmpty) findViewById(R.id.add_members_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setEmptyView(findViewById(R.id.empty_add_members_text));

        // Membros deste projeto
        membersRecyclerView = (RecyclerViewEmpty) findViewById(R.id.current_members_list);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        membersRecyclerView.setEmptyView(findViewById(R.id.empty_members_list_text));


        mCallbackUsers = new UsuarioAdapter.ClickCallback() {
            @Override
            public void onClick(View v, Usuario user) {
                int index = mUsuario.indexOf(user);
                onAddMember(mKeys.get(index));
            }
        };

        mCallbackMembers = new UsuarioAdapter.ClickCallback() {
            @Override
            public void onClick(View v, Usuario user) {
                int index = members.indexOf(user);
                onRemoveMember(membersKeys.get(index));
            }
        };


        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsuario = new ArrayList<>();
                mKeys = new ArrayList<>();

                members = new ArrayList<>();
                membersKeys = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Usuario user = postSnapshot.getValue(Usuario.class);

                    if (user.getProjetos() != null) {
                        if (user.getProjetos().contains(projectId)) {
                            // Não adiciona na lista se o usuário for o proprietário
                            if (!mProject.getOwnerUuid().equals(postSnapshot.getKey())) {
                                members.add(user);
                                membersKeys.add(postSnapshot.getKey());
                            }
                        } else {
                            mKeys.add(postSnapshot.getKey());
                            mUsuario.add(user);
                        }
                    } else {
                        mKeys.add(postSnapshot.getKey());
                        mUsuario.add(user);
                    }
                }

                mAdapter = new UsuarioAdapter(mUsuario, mCallbackUsers);
                mRecyclerView.setAdapter(mAdapter);


                membersRecyclerView.setAdapter(new UsuarioAdapter(members, mCallbackMembers, /*remove*/ true));
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

        setResult(1011, i);
        finish();

    }

    private void onRemoveMember(String key) {
        Intent i = new Intent();
        i.putExtra("user", key);
        i.putExtra("remove", true);

        setResult(1011, i);
        finish();
    }
}
