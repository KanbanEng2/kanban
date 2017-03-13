package br.ufrr.eng2.kanban;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import java.util.Date;

import br.ufrr.eng2.kanban.model.Tarefa;
import fr.ganfra.materialspinner.MaterialSpinner;

public class TaskActivity extends AppCompatActivity implements Transition.TransitionListener {

    private MaterialSpinner spinner;
    private EditText description;
    private Switch assignSwitch;
    private Switch callendarSwitch;
    private View mToolbarBackground;
    private DatePicker mDatePicker;
    private boolean animationFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        mToolbarBackground = (View) findViewById(R.id.toolbar_background);
        mDatePicker = (DatePicker) findViewById(R.id.datepicker);
        callendarSwitch = (Switch) findViewById(R.id.switchDatepicker);

        callendarSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDatePicker.setVisibility(View.VISIBLE);
                } else {
                    mDatePicker.setVisibility(View.GONE);
                }
            }
        });


        String[] ITEMS = {"Análise", "Correção", "Desenvolvimento"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (spinner.getSelectedItemPosition()) {
                    case 0:
                        changeToolbarColorAnimated(getColorFromCategory(Tarefa.CATEGORIA_ANALISE));
                        break;
                    case 1:
                        changeToolbarColorAnimated(getColorFromCategory(Tarefa.CATEGORIA_CORRECAO));
                        break;
                    case 2:
                        changeToolbarColorAnimated(getColorFromCategory(Tarefa.CATEGORIA_DESENVOLVIMENTO));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String tarefaTitle = "Título da Tarefa";

        description = (EditText) findViewById(R.id.task_description_text);
        assignSwitch = (Switch) findViewById(R.id.switchAssign);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            tarefaTitle = b.getString("title");
            int category = b.getInt("category");
            mToolbarBackground.setBackgroundColor(getColorFromCategory(category));
            switch (category) {
                case Tarefa.CATEGORIA_ANALISE:
                    spinner.setSelection(0);
                    break;
                case Tarefa.CATEGORIA_CORRECAO:
                    spinner.setSelection(1);
                    break;
                case Tarefa.CATEGORIA_DESENVOLVIMENTO:
                    spinner.setSelection(2);
                    break;
            }

            description.setText(b.getString("description", ""));
            int assumed = b.getInt("assumed", 0);
            if (assumed != 0) {
                assignSwitch.setChecked(true);
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.task_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getSharedElementEnterTransition().addListener(this);
        getSupportActionBar().setTitle(tarefaTitle);

        taskName = tarefaTitle;


        createEditTaskNameDialog();
    }

    private void changeToolbarColorAnimated(final int color) {
        if (!animationFinished)
            return;

        final View backgroundReveal = findViewById(R.id.toolbar_background_reveal);

        int cx = (backgroundReveal.getLeft() + backgroundReveal.getRight()) / 2;
        int cy = backgroundReveal.getBottom();

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(backgroundReveal, cx, cy, 0, backgroundReveal.getWidth());

        circularReveal.setDuration(500);

        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                backgroundReveal.setBackgroundColor(color);
                backgroundReveal.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mToolbarBackground.setBackgroundColor(color);
                backgroundReveal.setVisibility(View.GONE);
                super.onAnimationEnd(animation);
            }
        });
        circularReveal.start();
    }

    /**
     * Pega a cor do XML baseando-se na categoria
     *
     * @param category um dos seguintes inteiros {@link Tarefa#CATEGORIA_ANALISE} | {@link Tarefa#CATEGORIA_CORRECAO} |
     *                 {@link Tarefa#CATEGORIA_DESENVOLVIMENTO}
     * @return Um inteiro 0xAARRGGBB correspondente à cor.
     */
    private int getColorFromCategory(int category) {
        switch (category) {
            case Tarefa.CATEGORIA_ANALISE:
                return getResources().getColor(R.color.category_analysis);
            case Tarefa.CATEGORIA_CORRECAO:
                return getResources().getColor(R.color.category_fix);
            case Tarefa.CATEGORIA_DESENVOLVIMENTO:
                return getResources().getColor(R.color.category_development);
            default:
                return -1;
        }
    }

    private AlertDialog mAlertEditTaskName;
    private EditText mAlertTaskName;
    private String taskName;

    private void createEditTaskNameDialog() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.alert_dialog_task_name, null);
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setView(view);

        alert_builder.setTitle(getString(R.string.alert_dialog_edit_task_top_title));
        alert_builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert_builder.setPositiveButton(getString(android.R.string.ok), null);

        mAlertEditTaskName = alert_builder.create();
        mAlertTaskName = (EditText) view.findViewById(R.id.alert_edit_task_titulo);


        if (mAlertEditTaskName.getWindow() != null) {
            mAlertEditTaskName.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        mAlertEditTaskName.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                int color = ResourcesCompat.getColor(getResources(), R.color.secondary_text, null);
                mAlertEditTaskName.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(color);

                mAlertEditTaskName.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean title_ok = true;

                        String title = mAlertTaskName.getText().toString();

                        if (title.isEmpty()) {
                            mAlertTaskName.setError(getString(R.string.alert_dialog_string_empty_error));
                            title_ok = false;
                        }

                        if (title_ok /*&& desc_ok*/) {
                            taskName = title;
                            getSupportActionBar().setTitle(taskName);
                            mAlertEditTaskName.dismiss();
                        }
                    }
                });

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Retornar a animação ao pressionar o "up"
            case android.R.id.home:
                setResultActivity();
                supportFinishAfterTransition();
                return true;
            case R.id.action_delete:
                removeTask();
                supportFinishAfterTransition();
                return true;
            case R.id.action_rename:
                mAlertTaskName.setText(taskName);
                mAlertEditTaskName.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void removeTask() {
        Intent i = new Intent();
        i.putExtra("remove", true);

        setResult(1010, i);
    }


    private void setResultActivity() {
        Intent i = new Intent();
        switch (spinner.getSelectedItemPosition()) {
            case 0:
                i.putExtra("category", Tarefa.CATEGORIA_ANALISE);
                break;
            case 1:
                i.putExtra("category", Tarefa.CATEGORIA_CORRECAO);
                break;
            case 2:
                i.putExtra("category", Tarefa.CATEGORIA_DESENVOLVIMENTO);
                break;
        }

        i.putExtra("remove", false);
        int assumed = assignSwitch.isChecked() ? 1 : 0;
        i.putExtra("assumed", assumed);
        i.putExtra("title", taskName);
        i.putExtra("description", description.getText().toString());

        String estimate = null;
        if (callendarSwitch.isChecked()) {
            Date date = new Date(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            estimate = String.valueOf(date.getTime());
        }
        i.putExtra("estimate", estimate);
        setResult(1010, i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            setResultActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTransitionStart(Transition transition) {
        transition.excludeTarget(R.id.appBarLayout, true);
    }

    @Override
    public void onTransitionEnd(Transition transition) {
        transition.removeListener(this);
        View toolbarBg = findViewById(R.id.toolbar_background);
        View toolbar = findViewById(R.id.task_toolbar);

        int cx = (toolbarBg.getLeft() + toolbarBg.getRight()) / 2;
        int cy = (toolbarBg.getTop() + toolbarBg.getBottom()) / 2;

        Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(toolbarBg, cx, cy, 0, toolbar.getWidth());

        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationFinished = true;
                super.onAnimationEnd(animation);
            }
        });
        circularReveal.setDuration(500);

        circularReveal.start();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) toolbarBg.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        toolbarBg.setLayoutParams(params);
    }

    @Override
    public void onTransitionCancel(Transition transition) {

    }

    @Override
    public void onTransitionPause(Transition transition) {

    }

    @Override
    public void onTransitionResume(Transition transition) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task, menu);
        return true;
    }
}
