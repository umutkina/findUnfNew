package com.umutkina.findunfollowersapp;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umutkina.findunfollowersapp.modals.Const;

public class WebViewActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = (WebView) findViewById(R.id.wv);
        Bundle extras = getIntent().getExtras();
        String url = extras.getString("url");
        webView.setWebViewClient(new CustomViewClient());
        webView.loadUrl(url);


    }

    private class CustomViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            System.out.println("webview url : " + url);
            if (url.contains(Const.CALLBACK_URL_PUNCH)) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("url", url);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
            return true;
        }

        public void onPageFinished(WebView view, String url) {


        }
    }

}
