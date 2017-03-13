package br.ufrr.eng2.kanban;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

import br.ufrr.eng2.kanban.model.Tarefa;
import fr.ganfra.materialspinner.MaterialSpinner;

public class TaskActivity extends AppCompatActivity implements Transition.TransitionListener {

    private MaterialSpinner spinner;
    private EditText description;
    private Switch assignSwitch;
    private View mToolbarBackground;
    private boolean animationFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        mToolbarBackground = (View) findViewById(R.id.toolbar_background);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Retornar a animação ao pressionar o "up"
            case android.R.id.home:
                setResultActivity();
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        int assumed = assignSwitch.isChecked() ? 1 : 0;
        i.putExtra("assumed", assumed);
        i.putExtra("description", description.getText().toString());

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
}
