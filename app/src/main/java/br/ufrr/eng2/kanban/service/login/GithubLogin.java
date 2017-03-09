package br.ufrr.eng2.kanban.service.login;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GithubAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import br.ufrr.eng2.kanban.R;

/**
 * Created by edwino on 15/02/17.
 */

public class GithubLogin extends OAuthLogin {

    public static final int GITHUB_SIGN_INTENT = 101;

    protected String clientSecret;

    public GithubLogin(Context context, FirebaseAuth auth){
        super(context, auth);
        this.title = context.getString(R.string.title_signin_github);

        this.oAuthUrl = this.context.getString(R.string.github_oauth_url);
        this.redirectUrl = this.context.getString(R.string.github_redirect_url);
        this.tokenUrl = this.context.getString(R.string.github_token_url);
        this.clientId = this.context.getString(R.string.github_client_id);
        this.clientSecret = this.context.getString(R.string.github_client_secret);

        this.clientIdLabel = "client_id";
        this.redirectUrlLabel = "redirect_uri";
        this.authCodeLabel = "code";

    }

    @Override
    public void fbRegistry(Object info){

        if(info == null){
            ((LoginCallback) context).onLoginError(info, ERROR_RESULT_FAIL, this);
            return;
        }

        JSONObject json = (JSONObject) info;
        String token;

        try {
            token = json.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
            ((LoginCallback) context).onLoginError(info, ERROR_RESULT_FAIL, this);
            return;
        }

        final GithubLogin scope = this;
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        this.auth.signInWithCredential(credential)
                 .addOnCompleteListener(((Activity) this.context), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) ((LoginCallback) context).onSuccess(task, scope);
                        else  ((LoginCallback) context).onLoginError(task, ERROR_FIREBASE_FAIL, scope);
                    }
                 });
    }

    @Override
    public void onReceiveTokenResponse(String response){

        JSONObject json = null;

        try {
            json = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.fbRegistry(json);
    }

    @Override
    public Map<String, String> onTokenRequestGetParams(Map<String, String>  params ){
        params.put("code", this.authCode);
        params.put("client_id", this.clientId);
        params.put("redirectUri", this.redirectUrl);
        params.put("client_secret", this.clientSecret);
        return params;
    }

    @Override
    public Map<String, String> onTokenRequestGetHeaders(Map<String, String>  params) {
        params.put("Accept", "application/json");
        return params;
    }

    public int getProviderId(){
        return GithubLogin.GITHUB_SIGN_INTENT;
    }
}
