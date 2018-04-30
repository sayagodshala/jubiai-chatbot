package com.jubi.ai.chatbot.views.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.jubi.ai.chatbot.R;
import com.jubi.ai.chatbot.models.WebViewData;

public class WebViewActivity extends AppCompatActivity {

    public static final String DATA = "data";
    private WebViewData data;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webview = findViewById(R.id.webview);
        if (getIntent() != null && getIntent().getExtras().get(DATA) != null) {
            data = getIntent().getParcelableExtra(DATA);
        }
        Log.d("Webview Data", new Gson().toJson(data));
        Log.d("Webview url", data.getUrl());
        loadWebPage();
    }

    private void loadWebPage() {
        String videoURL = "https://www.youtube.com/embed/" + data.getUrl() + "?rel=0&autoplay=1";
        String data_html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe style=\"position:absolute;\" width=\"100%\" height=\"100%\" src=\""+videoURL+"\" frameborder=\"0\"></iframe> </body> </html> ";
        webview.loadDataWithBaseURL("https://youtube.com", data_html ,"text/html", "UTF-8", null);
        webview.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(Uri.parse(data.getUrl()).getHost())) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    }

}
