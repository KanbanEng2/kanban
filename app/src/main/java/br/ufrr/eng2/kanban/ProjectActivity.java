package br.ufrr.eng2.kanban;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrr.eng2.kanban.adapter.UsuarioAdapter;
import br.ufrr.eng2.kanban.model.Usuario;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


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
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
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

        setResult(1010, i);
        finish();

    }

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private UsuarioAdapter.ClickCallback mCallback;
    private UsuarioAdapter mAdapter;
    private List<Usuario> mUsuario;
    private List<String> mKeys;



}
