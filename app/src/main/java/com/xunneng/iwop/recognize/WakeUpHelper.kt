package com.xunneng.iwop.recognize

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import android.webkit.WebView
import com.baidu.aip.asrwakeup3.core.recog.IStatus
import com.baidu.aip.asrwakeup3.core.wakeup.MyWakeup
import com.baidu.aip.asrwakeup3.core.wakeup.RecogWakeupListener
import com.baidu.speech.asr.SpeechConstant
import java.util.HashMap

class WakeUpHelper {
    private var myWakeup: MyWakeup? = null
    private var mWebView: WebView? = null
    private var status = IStatus.STATUS_NONE

    private var handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.d(TAG,"has wake up $msg")
            mWebView?.loadUrl("javascript:hasWakeUp()")
        }
    }

    fun init(context: Context, webView: WebView) {
        mWebView = webView
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        val listener = RecogWakeupListener(handler)
        myWakeup = MyWakeup(context, listener)
    }

    // 点击“开始识别”按钮
    fun start() {
        val params = HashMap<String, Any>()
        params[SpeechConstant.WP_WORDS_FILE] = "assets:///XunNengWakeUp.bin"
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        myWakeup?.start(params)
    }

    fun stop() {
        myWakeup?.stop()
    }

    fun release() {
        myWakeup?.release()
    }

    companion object {
        private const val TAG = "WakeUpHelper"
        @SuppressLint("StaticFieldLeak")
        private var mInstance: WakeUpHelper? = null
            get() {
                if (field == null) {
                    field = WakeUpHelper()
                }
                return field
            }

        fun get(): WakeUpHelper {
            return mInstance!!
        }
    }
}