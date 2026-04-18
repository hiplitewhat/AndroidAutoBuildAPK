package com.lyric.sequencer;

import android.os.Bundle;

import android.view.WindowManager;

import android.webkit.WebSettings;

import android.webkit.WebView;

import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WebView wv = new WebView(this);

        setContentView(wv);

        WebSettings s = wv.getSettings();

        s.setJavaScriptEnabled(true);

        s.setDomStorageEnabled(true);

        wv.setWebViewClient(new WebViewClient());

        wv.loadUrl("file:///android_asset/index.html");

    }

}