package com.xunneng.iwop

import android.Manifest
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.xunneng.iwop.recognize.LongRecogHelper
import com.xunneng.iwop.recognize.RecogHelper
import com.xunneng.iwop.recognize.TtsHelper
import com.xunneng.iwop.recognize.WakeUpHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URISyntaxException


open class MainActivity : AppCompatActivity() {

    private var mRecogHelper: RecogHelper? = null
    private var mLongRecogHelper: LongRecogHelper? = null
    private var mWakeUpHelper: WakeUpHelper? = null

//    private var url = "file:///android_asset/iwop.html"
    private var url = "https://mmm.chengxinheyi.com/Plugin/wappay/pay.jsp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
        initTitle()
        initWebView()
        initRecog()
        initTts()
        initWakeUp()
    }

    private fun initWakeUp() {
        WakeUpHelper.get().init(this, webview)
    }

    private fun initTts() {
        TtsHelper.get().init(this)
    }

    private fun initRecog() {
        mRecogHelper = RecogHelper.getInstance(webview)
        mRecogHelper?.init(this)

        mLongRecogHelper = LongRecogHelper.getInstance()
        mLongRecogHelper?.init(this, webview)
    }

    private fun initTitle() {
        url_text.setText(url)
        start_url.setOnClickListener {
            webview.loadUrl(url_text.text.toString())
        }
    }

    private fun initWebView() {
        webview.settings.javaScriptEnabled = true
        webview.addJavascriptInterface(this, "obj")
        webview.loadUrl(url)
        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted: ")
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ")
                val url: String
                //网页在webView中打开
                url = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {//安卓5.0的加载方法
                    request.toString()
                } else {//5.0以上的加载方法
                    request.url.toString()
                }

                try {
                    //处理intent协议
                    if (url.startsWith("intent://")) {
                        val intent: Intent
                        try {
                            intent = parseUri(url, URI_INTENT_SCHEME)
                            intent.addCategory("android.intent.category.BROWSABLE")
                            intent.component = null
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                intent.setSelector(null)
                            }
                            val resolves = packageManager.queryIntentActivities(intent, 0)
                            if (resolves.size > 0) {
                                startActivityIfNeeded(intent, -1)
                            }
                            return true
                        } catch (e: URISyntaxException) {
                            e.printStackTrace()
                        }

                    }
                    // 处理自定义scheme协议
                    if (!url.startsWith("http")) {
                        Log.d(TAG, "处理自定义scheme-->$url")
                        try {
                            // 以下固定写法
                            val intent = Intent(ACTION_VIEW,
                                    Uri.parse(url))
                            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                        } catch (e: Exception) {
                            // 防止没有安装的情况
                            e.printStackTrace()
                        }
                        return true
                    }else{
                        view.loadUrl(url)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished: url = $url")
            }
        }
        webview.webChromeClient = WebChromeClient()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0x0010)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @JavascriptInterface
    fun shortRecogStart() {
        runOnUiThread {
            mRecogHelper?.startRecognize()
        }
//        setLongRecog(false)
//        start()
    }

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    @JavascriptInterface
    fun startLongRecog() {
        Log.d(TAG, "start: ")
        mLongRecogHelper?.startLongRecog(this)
    }

    /**
     * 开始录音后，手动点击“停止”按钮。
     * SDK会识别不会再识别停止后的录音。
     */
    @JavascriptInterface
    fun stopRecog() {
        Log.d(TAG, "stop: ")
        // DEMO集成步骤4 (可选） 停止录音
        mLongRecogHelper?.stopLongRecog()
    }

    /**
     * 开始录音后，手动点击“取消”按钮。
     * SDK会取消本次识别，回到原始状态。
     */
    @JavascriptInterface
    fun cancel() {
        Log.d(TAG, "cancel: ")
        // DEMO集成步骤5 (可选） 取消本次识别
        mLongRecogHelper?.cancelLongRecog()
    }

    @JavascriptInterface
    fun startTtsPlay(text: String) {
        Log.d(TAG, "startTtsPlay: $text")
        TtsHelper.get().startTtsPlay(text)
    }

    @JavascriptInterface
    fun cancelTtsPlay() {
        TtsHelper.get().cancelTtsPlay()
    }

    @JavascriptInterface
    fun pauseTtsPlay() {
        TtsHelper.get().pauseTtsPlay()
    }

    @JavascriptInterface
    fun resumeTtsPlay() {
        TtsHelper.get().resumeTtsPlay()
    }

    @JavascriptInterface
    fun startWakeUp() {
        WakeUpHelper.get().start()
    }

    @JavascriptInterface
    fun stopWakeUp() {
        WakeUpHelper.get().stop()
    }

    @JavascriptInterface
    fun uploadUserWords() {
        mRecogHelper?.uploadUserWords(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        TtsHelper.get().destroy()
        mLongRecogHelper?.release()
        WakeUpHelper.get().release()
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
