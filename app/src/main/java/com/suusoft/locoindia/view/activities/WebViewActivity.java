package com.suusoft.locoindia.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;
import com.suusoft.locoindia.globals.Constants;

/**
 * Created by SuuSoft.com on 24/12/2016.
 */

public class WebViewActivity extends com.suusoft.locoindia.base.BaseActivity {
    private WebView webView;
    private String url;
    String title;
    @Override
    protected ToolbarType getToolbarType() {
        return ToolbarType.NAVI;
    }

    @Override
    protected int getLayoutInflate() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void getExtraData(Intent intent) {
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString(Constants.KEY_TITLE);

        url = bundle.getString(Constants.KEY_URL);
    }

    @Override
    protected void initilize() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        AppController.onPause();
    }

    @Override
    protected void initView() {
        setToolbarTitle(title);
        webView = (WebView) findViewById(R.id.web_view);
    }

    @Override
    protected void onViewCreated() {
        setUpWebView(webView);
    }

    private void setUpWebView(WebView webView) {
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return super.shouldOverrideUrlLoading(view, request);
            }
        });

    }
}
