package br.ufrr.eng2.kanban;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class LoginActivity extends AppCompatActivity {

    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void gotoMain(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

}
