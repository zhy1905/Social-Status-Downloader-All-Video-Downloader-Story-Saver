package com.techhive.statussaver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;

import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.techhive.statussaver.utils.InstaPref;
import com.techhive.statussaver.utils.Utils;

public class FBLoginActivity extends AppCompatActivity {
    FBLoginActivity activity;
    private String cookies;
    SwipeRefreshLayout swipeRefreshLayout;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_fb_login);
        activity = this;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);
        loadPage();
        swipeRefreshLayout.setOnRefreshListener(() -> loadPage());
    }

    public void loadPage() {
        webView.clearCache(true);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        WebStorage.getInstance().deleteAllData();
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportMultipleWindows(true);
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(activity, "Android");
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webSettings.setMixedContentMode(2);
//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setLoadWithOverviewMode(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });

        webView.setWebViewClient(new MyBrowser());

        webView.loadUrl("https://www.facebook.com/");
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
            webView.loadUrl(webResourceRequest.getUrl().toString());
            cookies = CookieManager.getInstance().getCookie(webResourceRequest.getUrl().toString());
            if (!Utils.isNullOrEmpty(cookies) && cookies.contains("c_user")) {
                InstaPref.getInstance(activity).putString(InstaPref.FBCOOKIES, cookies);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            cookies = CookieManager.getInstance().getCookie(str);
            webView.loadUrl("javascript:Android.resultOnFinish();");
            webView.loadUrl("javascript:var el = document.querySelectorAll('input[name=fb_dtsg]');Android.resultOnFinish(el[0].value);");
        }
    }

    @JavascriptInterface
    public void resultOnFinish(String key) {
        if (key.length() < 15) {
            return;
        }
        try {
            if (!Utils.isNullOrEmpty(cookies) && cookies.contains("c_user")) {
                InstaPref.getInstance(activity).putString(InstaPref.FBKEY, key);
                InstaPref.getInstance(activity).putBoolean(InstaPref.ISFBLOGIN, true);
                System.out.println("Key - " + key);
                Intent intent = new Intent();
                intent.putExtra("result", "result");
                setResult(RESULT_OK, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}