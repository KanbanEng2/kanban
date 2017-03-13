package br.ufrr.eng2.kanban;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import br.ufrr.eng2.kanban.model.Tarefa;
import fr.ganfra.materialspinner.MaterialSpinner;

public class TaskActivity extends AppCompatActivity implements Transition.TransitionListener {

    private MaterialSpinner spinner;
    private EditText description;
    private Switch assignSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        View colorView = (View) findViewById(R.id.toolbar_background);
        String[] ITEMS = {"Análise", "Correção", "Desenvolvimento"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        String tarefaTitle = "Título da Tarefa";

        description = (EditText) findViewById(R.id.task_description_text);
        assignSwitch = (Switch) findViewById(R.id.switchAssign);


        Bundle b = getIntent().getExtras();
        if (b != null){
            tarefaTitle = b.getString("title");
            int category = b.getInt("category");
            switch (category) {
                case Tarefa.CATEGORIA_ANALISE:
                    colorView.setBackgroundColor(Color.parseColor("#9C27B0"));
                    spinner.setSelection(0);
                    break;
                case Tarefa.CATEGORIA_CORRECAO:
                    colorView.setBackgroundColor(Color.parseColor("#FF5722"));
                    spinner.setSelection(1);
                    break;
                case Tarefa.CATEGORIA_DESENVOLVIMENTO:
                    colorView.setBackgroundColor(Color.parseColor("#9E9E9E"));
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
        switch(spinner.getSelectedItemPosition()) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {

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
