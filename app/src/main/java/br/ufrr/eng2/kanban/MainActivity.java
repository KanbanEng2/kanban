package br.ufrr.eng2.kanban;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import br.ufrr.eng2.kanban.adapter.CardsAdapter;
import br.ufrr.eng2.kanban.model.Tarefa;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private CardsAdapter mAdapterTODO;
    private List<Tarefa> mTarefasTODO = new ArrayList<>();
    private CardsAdapter mAdapterDOING;
    private List<Tarefa> mTarefasDOING = new ArrayList<>();
    private CardsAdapter mAdapterDONE;
    private List<Tarefa> mTarefasDONE = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private NavigationView navigationView;

    private FloatingActionButton mFab;
    private AlertDialog mAlertAddCard;
    private EditText mAlertTitleCard;
    private EditText mAlertDescCard;
    private BottomBar mBottomBar;

    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.auth = FirebaseAuth.getInstance();
        this.user = this.auth.getCurrentUser();

        if (this.user == null) {
            if (BuildConfig.FLAVOR.contentEquals("noFireBase")) {
                Log.d("NoFireBase", "Setting default alias");
                this.user = null;
            }

            else {
                gotoLoginActivity();
            }
        }
        else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mFab = (FloatingActionButton) findViewById(R.id.fab);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            this.navigationView = (NavigationView) findViewById(R.id.nav_view);
            this.navigationView.setNavigationItemSelectedListener(this);
            this.onCreateNavigationView();

//        mBottomBar = BottomBar.attach(this, savedInstanceState);
//        mBottomBar.setItems(R.menu.bottombar_menu);


            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


            ItemTouchHelper mIth = new ItemTouchHelper(
                    new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                        private float oldElevation = -1;
                        private boolean isElevated = false;

                        @Override
                        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                            if (!isElevated && isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                                CardView cardView = (CardView) viewHolder.itemView.findViewById(R.id.card_view);
                                oldElevation = oldElevation == -1 ? cardView.getCardElevation() : oldElevation;
                                ObjectAnimator anim = ObjectAnimator.ofFloat(cardView, "cardElevation", oldElevation, cardView.getMaxCardElevation());
                                anim.setDuration(200);
                                anim.start();

                                isElevated = true;
                            }
                        }

                        @Override
                        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                            super.clearView(recyclerView, viewHolder);
                            CardView cardView = (CardView) viewHolder.itemView.findViewById(R.id.card_view);
                            ObjectAnimator anim = ObjectAnimator.ofFloat(cardView, "cardElevation", cardView.getMaxCardElevation(), oldElevation);
                            anim.setDuration(100);
                            anim.start();
                            isElevated = false;

                        }

                        @Override
                        public boolean onMove(RecyclerView recyclerView,
                                              RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                            final int fromPos = viewHolder.getAdapterPosition();
                            final int toPos = target.getAdapterPosition();
                            // move item in `fromPos` to `toPos` in adapter.

                            switch (mBottomBar.getCurrentTabId()) {
                                case R.id.bottombar_todo:
                                    Tarefa fromTarefa = mTarefasTODO.get(fromPos);
                                    mTarefasTODO.remove(fromPos);
                                    mTarefasTODO.add(toPos, fromTarefa);

                                    break;
                                case R.id.bottombar_doing:
                                    Tarefa doingTarefa = mTarefasDOING.get(fromPos);
                                    mTarefasDOING.remove(fromPos);
                                    mTarefasDOING.add(toPos, doingTarefa);
                                    break;
                                case R.id.bottombar_done:
                                    Tarefa doneTarefa = mTarefasDONE.get(fromPos);
                                    mTarefasDONE.remove(fromPos);
                                    mTarefasDONE.add(toPos, doneTarefa);
                                    break;
                            }


                            recyclerView.getAdapter().notifyItemMoved(fromPos, toPos);

                            return true;// true if moved, false otherwise
                        }

                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            // remove from adapter
                            final int fromPos = viewHolder.getAdapterPosition();

                            switch (mBottomBar.getCurrentTabId()) {
                                case R.id.bottombar_todo:
                                    final Tarefa fromTarefa = mTarefasTODO.get(fromPos);
                                    mTarefasTODO.remove(fromPos);
                                    if (direction == ItemTouchHelper.LEFT) {
                                        Snackbar snackbar = Snackbar
                                                .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_moved), getString(R.string.column_done)), Snackbar.LENGTH_LONG);
                                         snackbar.setAction(R.string.snack_action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTarefasTODO.add(fromPos, fromTarefa);
                                                mTarefasDONE.remove(fromTarefa);
                                                mAdapterTODO.notifyItemInserted(fromPos);
                                                mAdapterDONE.notifyDataSetChanged();

                                            }
                                        });
                                        snackbar.show();
                                        mTarefasDONE.add(fromTarefa);
                                        mAdapterDONE.notifyDataSetChanged();
                                    } else if (direction == ItemTouchHelper.RIGHT) {
                                        Snackbar snackbar = Snackbar
                                                .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_moved), getString(R.string.column_doing)), Snackbar.LENGTH_LONG);

                                        snackbar.setAction(R.string.snack_action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTarefasTODO.add(fromPos, fromTarefa);
                                                mTarefasDOING.remove(fromTarefa);
                                                mAdapterTODO.notifyItemInserted(fromPos);
                                                mAdapterDOING.notifyDataSetChanged();
                                            }
                                        });
                                        snackbar.show();
                                        mTarefasDOING.add(fromTarefa);
                                        mAdapterDOING.notifyDataSetChanged();

                                    }
                                    break;
                                case R.id.bottombar_doing:
                                    final Tarefa doingTarefa = mTarefasDOING.get(fromPos);
                                    mTarefasDOING.remove(fromPos);
                                    if (direction == ItemTouchHelper.LEFT) {
                                        Snackbar snackbar = Snackbar
                                                .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_moved), getString(R.string.column_todo)), Snackbar.LENGTH_LONG);

                                        snackbar.setAction(R.string.snack_action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTarefasDOING.add(fromPos, doingTarefa);
                                                mTarefasTODO.remove(doingTarefa);
                                                mAdapterDOING.notifyItemInserted(fromPos);
                                                mAdapterTODO.notifyDataSetChanged();
                                            }
                                        });
                                        snackbar.show();
                                        mTarefasTODO.add(doingTarefa);
                                        mAdapterTODO.notifyDataSetChanged();
                                    } else if (direction == ItemTouchHelper.RIGHT) {
                                        Snackbar snackbar = Snackbar
                                                .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_moved), getString(R.string.column_done)), Snackbar.LENGTH_LONG);

                                        snackbar.setAction(R.string.snack_action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTarefasDOING.add(fromPos, doingTarefa);
                                                mTarefasDONE.remove(doingTarefa);
                                                mAdapterDOING.notifyItemInserted(fromPos);
                                                mAdapterDONE.notifyDataSetChanged();
                                            }
                                        });
                                        snackbar.show();
                                        mTarefasDONE.add(doingTarefa);
                                        mAdapterDONE.notifyDataSetChanged();

                                    }
                                    break;
                                case R.id.bottombar_done:
                                    final Tarefa doneTarefa = mTarefasDONE.get(fromPos);
                                    mTarefasDONE.remove(fromPos);
                                    if (direction == ItemTouchHelper.LEFT) {
                                        Snackbar snackbar = Snackbar
                                                .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_moved), getString(R.string.column_doing)), Snackbar.LENGTH_LONG);

                                        snackbar.setAction(R.string.snack_action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTarefasDONE.add(fromPos, doneTarefa);
                                                mTarefasDOING.remove(doneTarefa);
                                                mAdapterDONE.notifyItemInserted(fromPos);
                                                mAdapterDOING.notifyDataSetChanged();
                                            }
                                        });
                                        snackbar.show();
                                        mTarefasDOING.add(doneTarefa);
                                        mAdapterDOING.notifyDataSetChanged();
                                    } else if (direction == ItemTouchHelper.RIGHT) {
                                        Snackbar snackbar = Snackbar
                                                .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_moved), getString(R.string.column_todo)), Snackbar.LENGTH_LONG);

                                        snackbar.setAction(R.string.snack_action_undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTarefasDONE.add(fromPos, doneTarefa);
                                                mTarefasTODO.remove(doneTarefa);
                                                mAdapterDONE.notifyItemInserted(fromPos);
                                                mAdapterTODO.notifyDataSetChanged();
                                            }
                                        });
                                        snackbar.show();
                                        mTarefasTODO.add(doneTarefa);
                                        mAdapterTODO.notifyDataSetChanged();

                                    }
                                    break;
                            }

                            mRecyclerView.getAdapter().notifyItemRemoved(fromPos);

                        }
                    });

            mIth.attachToRecyclerView(mRecyclerView);

            mAdapterTODO = new CardsAdapter(mTarefasTODO, new CardsAdapter.ClickCallback() {
                @Override
                public void onClick(View v, Tarefa t) {
                    gotoTaskAcitivity(t);
                }

                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });

            mAdapterDOING = new CardsAdapter(mTarefasDOING, new CardsAdapter.ClickCallback() {
                @Override
                public void onClick(View v, Tarefa t) {
                    gotoTaskAcitivity(t);

                }

                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });

            mAdapterDONE = new CardsAdapter(mTarefasDONE, new CardsAdapter.ClickCallback() {
                @Override
                public void onClick(View v, Tarefa t) {
                    gotoTaskAcitivity(t);
                }

                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            mRecyclerView.setAdapter(mAdapterTODO);

            mBottomBar = (BottomBar) findViewById(R.id.bottom_bar);
            mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {

                @Override
                public void onTabSelected(@IdRes int tabId) {

                    Log.d("BAR CURRENT TAB: ", String.valueOf(mBottomBar.getCurrentTabPosition()));

                    mRecyclerView.getAdapter().notifyItemRangeRemoved(0, mRecyclerView.getAdapter().getItemCount());
                    switch (tabId) {
                        case R.id.bottombar_todo:
                            Log.d("BOTTOMBAR", "TODO");
                            mRecyclerView.swapAdapter(mAdapterTODO, false);
                            mAdapterTODO.notifyItemRangeInserted(0, mAdapterTODO.getItemCount());

                            break;
                        case R.id.bottombar_doing:
                            Log.d("BOTTOMBAR", "DOING");
                            mRecyclerView.swapAdapter(mAdapterDOING, false);

                            break;
                        case R.id.bottombar_done:
                            Log.d("BOTTOMBAR", "DONE");
                            mRecyclerView.swapAdapter(mAdapterDONE, false);

                            break;
                    }
//                mRecyclerView.getAdapter().notifyItemRangeInserted(0, );
                }
            });

            getWindow().getSharedElementExitTransition().excludeTarget(R.id.appBarLayout, true);
            getWindow().getSharedElementExitTransition().excludeChildren(R.id.appBarLayout, true);

            CreateDialogAddCard();
        }
    }


    private void onCreateNavigationView() {
        String name;
        String email;
        Uri photoUrl;

        if (BuildConfig.FLAVOR.contentEquals("noFireBase")) {
            name = "NoFireBaseMode";
            email = "";
            photoUrl = null;
        }
        else{
            name = this.user.getDisplayName();
            email = this.user.getEmail();
            photoUrl = user.getPhotoUrl();
        }

        View header = this.navigationView.getHeaderView(0);

        TextView userDisplayName = (TextView) header.findViewById(R.id.user_display_name);
        userDisplayName.setText(name);

        TextView userEmail = (TextView) header.findViewById(R.id.user_email);
        userEmail.setText(email);

        ImageView userPhoto = (ImageView) header.findViewById(R.id.user_photo);
        if(photoUrl != null) {
            new DownloadImageTask(userPhoto)
                    .execute(photoUrl.toString());
        }
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

        if (mAlertAddCard.getWindow() != null)
            mAlertAddCard.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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

//                        if (desc.isEmpty()) {
//                            mAlertDescCard.setError(getString(R.string.alert_dialog_string_empty_error));
//                            desc_ok = false;
//                        }

                        if (title_ok /*&& desc_ok*/) {
                            mTarefasTODO.add(new Tarefa(1234, title, desc, Tarefa.ESTADO_TODO, Tarefa.CATEGORIA_CORRECAO));
                            mAdapterTODO.notifyItemInserted(mTarefasTODO.size() - 1);

                            Snackbar snackbar = Snackbar
                                    .make(mCoordinatorLayout, String.format(getString(R.string.snack_task_added), getString(R.string.column_todo)), Snackbar.LENGTH_SHORT);

                            snackbar.show();

                            dialog.dismiss();
                        }
                    }
                });

            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertTitleCard.requestFocus();
                mAlertAddCard.show();
            }
        });

    }

    protected void gotoTaskAcitivity(Tarefa t) {
        Log.d("ACTIVITY", "going to task activity");
        Intent i = new Intent(this, TaskActivity.class);
        i.putExtra("id", t.getUuid());
        startActivity(i);
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

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        if (id == R.id.sign_out) {
            this.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void signOut() {

        if (this.user != null) {
            this.auth.signOut();
        }

        gotoLoginActivity();
    }

    protected void gotoLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                java.net.URL url = new java.net.URL(urldisplay);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                mIcon11 = BitmapFactory.decodeStream(input);
                return mIcon11;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }


        protected void onPostExecute(Bitmap bitmap) {

            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            BitmapShader shader = new BitmapShader (bitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            paint.setAntiAlias(true);
            Canvas c = new Canvas(circleBitmap);
            c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

            bmImage.setImageBitmap(circleBitmap);


        }
    }

}
