package br.ufrr.eng2.kanban;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.ufrr.eng2.kanban.service.login.GithubLogin;
import br.ufrr.eng2.kanban.service.login.GoogleLogin;
import br.ufrr.eng2.kanban.service.login.LoginInterface;
import br.ufrr.eng2.kanban.service.login.LoginCallback;


public class LoginActivity extends AppCompatActivity implements LoginCallback {

    /**
     * TODO: Colocar uma barra de progresso durante os logins
     */

    ProgressBar mProgressBar;

    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;

    protected GoogleLogin googleLogin;
    protected GithubLogin githubLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        this.auth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) onSignedIn(user);
                else  onSignedOut();
            }
        };

        this.googleLogin = new GoogleLogin(this, this.auth);
        this.githubLogin = new GithubLogin(this, this.auth);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.authListener != null) {
            this.auth.addAuthStateListener(this.authListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.authListener != null) {
            this.auth.removeAuthStateListener(this.authListener);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GoogleLogin.GOOGLE_SIGN_INTENT:
                this.googleLogin.fbRegistry(data);
            break;

            case GithubLogin.GITHUB_SIGN_INTENT:
                this.githubLogin.onActivityResult(requestCode, resultCode, data);
            break;
        }
    }

    public void gotoMain(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onClickGoogleSign(View v) {
        this.googleLogin.singIn();
    }

    public void onClickGithubSign(View v) {
        this.githubLogin.singIn();
    }

    /**
     * TODO: Handling de erros
     */
    @Override
    public void onLoginError(Object info, int errorCode, LoginInterface loginInterface) {
        switch (loginInterface.getProviderId()){
            case GoogleLogin.GOOGLE_SIGN_INTENT:
                Log.d("Sign", "google fail; code: " + String.valueOf(errorCode));
            break;

            case GithubLogin.GITHUB_SIGN_INTENT:
                Log.d("Sign", "github fail; code: " + String.valueOf(errorCode));
            break;
        }
    }

    @Override
    public void onSuccess(Object info, LoginInterface loginInterface) {
        switch (loginInterface.getProviderId()){
            case GoogleLogin.GOOGLE_SIGN_INTENT:
                Log.d("Sign", "google success");
            break;

            case GithubLogin.GITHUB_SIGN_INTENT:
                Log.d("Sign", "github success");
            break;
        }
    }

    public void onSignedIn(FirebaseUser user){
        Log.d("Session", "Signed_in with " + user.getProviderData().get(1).getProviderId() + ": " + user.getUid());
        this.gotoMain(null);
    }

    public void onSignedOut(){
        Log.d("Session", "Signed_out");
    }
}
