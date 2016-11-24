package br.ufrr.eng2.kanban;

import android.animation.Animator;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

public class TaskActivity extends AppCompatActivity implements Transition.TransitionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().getSharedElementEnterTransition().addListener(this);
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