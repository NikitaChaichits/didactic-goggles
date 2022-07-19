package com.cyberself.vpn.util.view

import android.webkit.WebView
import android.webkit.WebViewClient

fun WebView.openWebView(url: String) {
    run {
        webViewClient = WebViewClient()
        loadUrl(url)
        settings.javaScriptEnabled = true
        settings.setSupportZoom(true)
        visible()
    }
}
