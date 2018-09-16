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
import com.xunneng.iwop.recognize.RecogHelper
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity() {

    private var mRecogHelper: RecogHelper? = null
    private lateinit var apiParams: CommonRecogParams

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected lateinit var myRecognizer: MyRecognizer

    private var url = "file:///android_asset/web.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissions()
        initTitle()
        initWebView()
        initRecog()
    }

    private fun initRecog() {
        mRecogHelper = RecogHelper.getInstance(webview)
        mRecogHelper?.init(this)

        // DEMO集成步骤 1.1 新建一个回调类，识别引擎会回调这个类告知重要状态和识别结果
        val listener = MessageStatusRecogListener(@SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                handleMsg(msg)
            }
        })
        // DEMO集成步骤 1.2 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例
        myRecognizer = MyRecognizer(this, listener)
        apiParams = OnlineRecogParams()

    }

    protected fun handleMsg(msg: Message?) {
        Log.d(TAG, "handleMsg: ${msg?.obj?.toString()}")
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

    @JavascriptInterface
    fun startRecognize(arg: String) {
        runOnUiThread {
            mRecogHelper?.startRecognize(arg)
        }
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

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    @JavascriptInterface
    fun start() {
        Log.d(TAG, "start: ")
        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        val params = fetchParams()
        // params 也可以根据文档此处手动修改，参数会以json的格式在界面和logcat日志中打印

        // 复制此段可以自动检测常规错误
        AutoCheck(applicationContext, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == 100) {
                    val autoCheck = msg.obj as AutoCheck
                    synchronized(autoCheck) {
                        val message = autoCheck.obtainErrorMessage() // autoCheck.obtainAllMessage();
                        Log.d(TAG, "handleMessage: $message")
                        // Log.w("AutoCheckMessage", message);
                    }// 可以用下面一行替代，在logcat中查看代码
                }
            }
        }, false).checkAsr(params)

        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        // DEMO集成步骤2.2 开始识别
        myRecognizer.start(params)
    }

    private fun fetchParams(): Map<String, Any> {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        //  集成时不需要上面的代码，只需要params参数。
        return apiParams.fetch(sp)
    }
    /**
     * 开始录音后，手动点击“停止”按钮。
     * SDK会识别不会再识别停止后的录音。
     */
    @JavascriptInterface
    fun stop() {
        Log.d(TAG, "stop: ")
        // DEMO集成步骤4 (可选） 停止录音
        myRecognizer.stop()
    }

    /**
     * 开始录音后，手动点击“取消”按钮。
     * SDK会取消本次识别，回到原始状态。
     */
    @JavascriptInterface
    fun cancel() {
        Log.d(TAG, "cancel: ")
        // DEMO集成步骤5 (可选） 取消本次识别
        myRecognizer.cancel()
    }


    companion object {
        private const val TAG = "MainActivity"
    }
}
