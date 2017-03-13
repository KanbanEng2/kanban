package br.ufrr.eng2.kanban.service.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

import br.ufrr.eng2.kanban.OAuthActivity;

/**
 * Created by edwino on 15/02/17.
 */

public abstract class OAuthLogin implements LoginInterface {

    public static final int ERROR_CONNETCTION_FAIL = 0;
    public static final int ERROR_RESULT_FAIL = 1;
    public static final int ERROR_FIREBASE_FAIL = 2;

    protected Context context;
    protected FirebaseAuth auth;

    protected Class oAuthActivity = OAuthActivity.class;

    protected String title = "OAuth";
    protected String authCodeLabel = "code";

    protected String oAuthUrl;
    protected String clientId;
    protected String redirectUrl;
    protected String tokenUrl;

    protected String clientIdLabel;
    protected String redirectUrlLabel;

    protected String authCode;

    public OAuthLogin(Context context, FirebaseAuth auth){
        this.context = context;
        this.auth = auth;
    }

    @Override
    public void signIn(){
        Intent signInIntent = new Intent(this.context, OAuthActivity.class);

        signInIntent.putExtra("windowTitle", this.title);
        signInIntent.putExtra("authCodeLabel", this.authCodeLabel);

        signInIntent.putExtra("oAuthUrl", this.oAuthUrl);
        signInIntent.putExtra("clientId", this.clientId);
        signInIntent.putExtra("redirectUrl", this.redirectUrl);

        signInIntent.putExtra("clientIdLabel", this.clientIdLabel);
        signInIntent.putExtra("redirectUrlLabel", this.redirectUrlLabel);

        ((Activity) this.context).startActivityForResult(signInIntent, this.getProviderId());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode == Activity.RESULT_OK){
            this.onAfterActivityResult(requestCode, resultCode, data);
        }
        else{
            ((LoginCallback) this.context).onLoginError(data, ERROR_RESULT_FAIL, this);
        }
    }

    protected abstract void onAfterActivityResult(int requestCode, int resultCode, Intent data);

    protected void requestToken(String authCode){
        this.authCode = authCode;
        RequestQueue queue = Volley.newRequestQueue(this.context);
        StringRequest request = this.buildStringRequest(this.tokenUrl, Request.Method.POST);
        queue.add(request);
    }

    protected StringRequest buildStringRequest(String url, int method){

        final OAuthLogin scope = this;

        StringRequest stringRequest = new StringRequest(method, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {scope.onReceiveTokenResponse(response);}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { ((LoginCallback) scope.context).onLoginError(error, ERROR_CONNETCTION_FAIL, scope); }
                }
        ){
            @Override
            protected Map<String, String> getParams(){ return scope.onTokenRequestGetParams(new HashMap<String, String>()); }

            @Override
            public Map<String, String> getHeaders() { return scope.onTokenRequestGetHeaders(new HashMap<String, String>()); }
        };

        return stringRequest;
    }

    public abstract void onReceiveTokenResponse(String response);

    public Map<String, String> onTokenRequestGetParams(Map<String, String>  params ){
        return params;
    }

    public Map<String, String> onTokenRequestGetHeaders(Map<String, String>  params) {
        return params;
    }
}
