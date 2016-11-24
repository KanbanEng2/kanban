package br.ufrr.eng2.kanban;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import br.ufrr.eng2.kanban.adapter.CardsAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private CardsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private FloatingActionButton mFab;
    private AlertDialog mAlertAddCard;
    private EditText mAlertTitleCard;
    private EditText mAlertDescCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CardsAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getWindow().getSharedElementExitTransition().excludeTarget(R.id.appBarLayout, true);
        getWindow().getSharedElementExitTransition().excludeChildren(R.id.appBarLayout, true);

        mAdapter.setOnItemClickListener(new CardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, long position) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this,
                                android.support.v4.util.Pair.create(view.findViewById(R.id.card_view), "task_background"),
                                android.support.v4.util.Pair.create(view.findViewById(R.id.card_tag_color), "task_color")
                        );

                startActivity(new Intent(MainActivity.this, TaskActivity.class), options.toBundle());
            }
        });

        CreateDialogAddCard();
    }

    private void CreateDialogAddCard() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.alert_dialog_add_card, null);
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setView(view);

        alert_builder.setTitle(getString(R.string.alert_dialog_add_card_top_title));
        alert_builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert_builder.setPositiveButton(getString(R.string.add), null);

        mAlertAddCard = alert_builder.create();
        mAlertTitleCard = (EditText) view.findViewById(R.id.alert_add_card_titulo);
        mAlertDescCard = (EditText) view.findViewById(R.id.alert_add_card_descricao);

        mAlertAddCard.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mAlertTitleCard.setText("");
                mAlertDescCard.setText("");

            }
        });

        mAlertAddCard.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                int color = ResourcesCompat.getColor(getResources(), R.color.secondary_text, null);
                mAlertAddCard.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);

                mAlertAddCard.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean title_ok = true;
                        boolean desc_ok = true;

                        String title = mAlertTitleCard.getText().toString();
                        String desc = mAlertDescCard.getText().toString();

                        if (title.isEmpty()) {
                            mAlertTitleCard.setError(getString(R.string.alert_dialog_string_empty_error));
                            title_ok = false;
                        }

                        if (desc.isEmpty()) {
                            mAlertDescCard.setError(getString(R.string.alert_dialog_string_empty_error));
                            desc_ok = false;
                        }

                        if (title_ok && desc_ok) {
//                          TODO: Add to adapter
                            dialog.dismiss();
                        }
                    }
                });

            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertAddCard.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
