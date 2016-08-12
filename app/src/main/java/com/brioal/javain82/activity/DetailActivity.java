package com.brioal.javain82.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.brioal.javain82.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.detail_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.detail_progress)
    ProgressBar mProgress;
    @Bind(R.id.detail_webview)
    WebView mWebview;

    private int color;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        initData();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initData() {
        mUrl = getIntent().getStringExtra("mUrl");
        SharedPreferences preferences = getSharedPreferences("DemoManager", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
        int colorIndex = preferences.getInt("LauncherBack", 0);
        int backColor = getResources().getColor(R.color.colorPrimary);
        switch (colorIndex) {
            case 0:
                backColor = getResources().getColor(R.color.colorTheme1);
                break;
            case 1:
                break;
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(backColor);
        color = backColor;
        initView();
    }

    private void initView() {
        mToolbar.setBackgroundColor(color);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(mToolbar);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebview.setWebChromeClient(new WebChromeClient() {



            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgress.setProgress(newProgress);
            }
        });
        mWebview.getSettings().setJavaScriptEnabled(true);
        WebSettings mWebSettings = mWebview.getSettings();
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebview.loadUrl(mUrl);

    }
}
