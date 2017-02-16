package br.ufrr.eng2.kanban;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuthActivity extends AppCompatActivity {

    protected WebView webView;

    protected String windowTitle;
    protected String authCodeLabel;

    protected String oAuthUrl;
    protected String cliendId;
    protected String redirectUrl;

    protected String clientIdLabel;
    protected String redirectUrlLabel;

    protected boolean authComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        Intent intent = getIntent();

        this.windowTitle = intent.getStringExtra("windowTitle");
        this.authCodeLabel = intent.getStringExtra("authCodeLabel");

        this.oAuthUrl = intent.getStringExtra("oAuthUrl");
        this.cliendId = intent.getStringExtra("clientId");
        this.redirectUrl = intent.getStringExtra("redirectUrl");

        this.clientIdLabel = intent.getStringExtra("clientIdLabel");
        this.redirectUrlLabel = intent.getStringExtra("redirectUrlLabel");

        final OAuthActivity scope = this;

        this.webView = (WebView) findViewById(R.id.oauth_webview);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                scope.onPageFinished(view, url);
            }
        });

        this.setTitle(this.windowTitle);
    }

    @Override
    public void onStart() {
        super.onStart();
        String url = this.getUrl();
        Log.d("OAuth", url);
        this.authComplete = false;
        this.webView.loadUrl(url);
    }

    protected String getUrl(){
        String url = this.oAuthUrl;
        url += "?"+this.clientIdLabel+"="+this.cliendId;
        url += "&"+this.redirectUrlLabel+"="+this.redirectUrl;
        return url;
    }

    protected void onReceiveAuthCode(String authCode){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(this.authCodeLabel, authCode);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onPageFinished(WebView view, String url){
        if (url.contains("?"+this.authCodeLabel+"=") && this.authComplete != true) {
            Uri uri = Uri.parse(url);
            String authCode = uri.getQueryParameter(this.authCodeLabel);
            this.authComplete = true;
            this.onReceiveAuthCode(authCode);
        }
    }
}
