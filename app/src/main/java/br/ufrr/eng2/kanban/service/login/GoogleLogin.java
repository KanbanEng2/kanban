package br.ufrr.eng2.kanban.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import br.ufrr.eng2.kanban.R;

/**
 * Created by edwino on 15/02/17.
 */

public class GoogleLogin implements LoginInterface, GoogleApiClient.OnConnectionFailedListener {

    public static final int GOOGLE_SIGN_INTENT = 100;

    public static final int ERROR_CONNETCTION_FAIL = 0;
    public static final int ERROR_RESULT_FAIL = 1;
    public static final int ERROR_FIREBASE_FAIL = 2;

    protected Context context;
    protected FirebaseAuth auth;
    protected GoogleApiClient client;
    protected GoogleSignInOptions gso;

    public GoogleLogin(Context context, FirebaseAuth auth){

        this.context = context;
        this.auth = auth;

        this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                          .requestIdToken(this.context.getString(R.string.default_web_client_id))
                                          .requestEmail()
                                          .build();

        this.client = new GoogleApiClient.Builder(this.context)
                                         .enableAutoManage((FragmentActivity) this.context, this)
                                         .addApi(Auth.GOOGLE_SIGN_IN_API, this.gso)
                                         .build();
    }

    @Override
    public void singIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.client);
        ((Activity) this.context).startActivityForResult(signInIntent, GoogleLogin.GOOGLE_SIGN_INTENT);
    }

    public void fbRegistry(Object info){

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent((Intent) info);

        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            final LoginInterface scope = this;

            this.auth.signInWithCredential(credential)
                     .addOnCompleteListener(((Activity) this.context), new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if(task.isSuccessful()) ((LoginCallback) context).onSuccess(task, scope);
                             else  ((LoginCallback) context).onLoginError(task, ERROR_FIREBASE_FAIL, scope);
                         }
                     });
        } else {
            ((LoginCallback) this.context).onLoginError(result, ERROR_RESULT_FAIL,this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ((LoginCallback) this.context).onLoginError(connectionResult, ERROR_CONNETCTION_FAIL, this);
    }

    public int getProviderId(){
        return GoogleLogin.GOOGLE_SIGN_INTENT;
    }
}
