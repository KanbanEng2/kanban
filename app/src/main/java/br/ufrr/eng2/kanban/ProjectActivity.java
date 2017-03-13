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
import br.ufrr.eng2.kanban.model.Usuario;
import br.ufrr.eng2.kanban.widget.RecyclerViewEmpty;

public class ProjectActivity extends AppCompatActivity {

    private RecyclerViewEmpty mRecyclerView;
    private UsuarioAdapter.ClickCallback mCallback;
    private UsuarioAdapter mAdapter;
    private List<Usuario> mUsuario;
    private List<String> mKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        mRecyclerView = (RecyclerViewEmpty) findViewById(R.id.add_members_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setEmptyView(findViewById(R.id.empty_add_members_text));

        // TODO: Popular com os usuários deste projeto
        RecyclerViewEmpty membersRecyclerView = (RecyclerViewEmpty) findViewById(R.id.current_members_list);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        membersRecyclerView.setEmptyView(findViewById(R.id.empty_members_list_text));
        membersRecyclerView.setAdapter(new UsuarioAdapter(new ArrayList<Usuario>(), null));


        mCallback = new UsuarioAdapter.ClickCallback() {
            @Override
            public void onClick(View v, Usuario user) {
                int index = mUsuario.indexOf(user);
                onAddMember(mKeys.get(index));
            }
        };

        ValueEventListener tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsuario = new ArrayList<>();
                mKeys = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mKeys.add(postSnapshot.getKey());
                    Usuario user = postSnapshot.getValue(Usuario.class);
                    mUsuario.add(user);
                }

                mAdapter = new UsuarioAdapter(mUsuario, mCallback);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user");
        myRef.addValueEventListener(tasksListener);

    }

    private void onAddMember(String key) {
        Intent i = new Intent();
        i.putExtra("user", key);

        setResult(1011, i);
        finish();

    }


}
