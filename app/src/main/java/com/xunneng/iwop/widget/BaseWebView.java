package com.xunneng.iwop.widget;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.xunneng.iwop.utils.Env;

public class BaseWebView extends WebView {
	public BaseWebView(Context context) {
		super(context);
		init();
	}
	
	public BaseWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public BaseWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("NewApi") private void init(){
		WebSettings webSettings = this.getSettings();
		Class clazz = webSettings.getClass();
        int API = Build.VERSION.SDK_INT;
        if (API < Env.ANDROID_4_3) {
        	webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        	webSettings.setSavePassword(false);
		}
		if (API > Env.ANDROID_2_3 && API < Env.ANDROID_4_2) {
			//webSettings.setEnableSmoothTransition(true);
		}
		if (API > Env.ANDROID_4_1) {
			try {
				Method setMediaPlaybackRequiresUserGesture = clazz.getDeclaredMethod("setMediaPlaybackRequiresUserGesture", Boolean.class);
				setMediaPlaybackRequiresUserGesture.invoke(clazz, true);
			} catch (Exception e) {
			}
		}
		
		if(API >= Env.ANDROID_3_0)
			webSettings.setDisplayZoomControls(false);
		
		if (API >= Env.ANDROID_4_1) {
			try {
				Method setAllowFileAccessFromFileURLs = clazz.getDeclaredMethod("setAllowFileAccessFromFileURLs", Boolean.class);
				setAllowFileAccessFromFileURLs.invoke(clazz, false);
				Method setAllowUniversalAccessFromFileURLs = clazz.getDeclaredMethod("setAllowUniversalAccessFromFileURLs", Boolean.class);
				setAllowUniversalAccessFromFileURLs.invoke(clazz, false);
			} catch (Exception e) {
			}
		}

		if (API >= Env.ANDROID_2_2 && API < Env.ANDROID_4_4) {
			webSettings.setPluginState(WebSettings.PluginState.ON);
		}
		if (API >= Env.ANDROID_2_1) {
			webSettings.setDomStorageEnabled(true);
		}
		
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		webSettings.setBlockNetworkImage(false);
		webSettings.setSupportMultipleWindows(false);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setAppCacheEnabled(false);
		webSettings.setDatabaseEnabled(false);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setAllowContentAccess(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		webSettings.setGeolocationEnabled(false);
	}

}
