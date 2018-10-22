package com.xunneng.iwop

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.baidu.aip.asrwakeup3.core.mini.AutoCheck
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener
import com.baidu.aip.asrwakeup3.uiasr.params.CommonRecogParams
import com.baidu.aip.asrwakeup3.uiasr.params.OnlineRecogParams
import com.xunneng.iwop.recognize.LongRecogHelper
import com.xunneng.iwop.recognize.RecogHelper
import com.xunneng.iwop.recognize.TtsHelper
import com.xunneng.iwop.recognize.WakeUpHelper
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

    private var mRecogHelper: RecogHelper? = null
    private var mLongRecogHelper: LongRecogHelper? = null
    private var mWakeUpHelper: WakeUpHelper? = null

    private var url = "file:///android_asset/web.html"

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
        webview.addJavascriptInterface(this, "obj")
        webview.loadUrl(url)
        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted: ")
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                Log.d(TAG, "shouldOverrideUrlLoading: ")
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished: ")
            }
        }
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
