package com.xunneng.iwop.recognize

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.webkit.WebView
import com.baidu.aip.asrwakeup3.core.mini.AutoCheck
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener
import com.baidu.aip.asrwakeup3.uiasr.params.CommonRecogParams
import com.baidu.aip.asrwakeup3.uiasr.params.OnlineRecogParams

class LongRecogHelper {
    private lateinit var apiParams: CommonRecogParams

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected lateinit var myRecognizer: MyRecognizer

    fun init(context: Context, webView: WebView) {
        // DEMO集成步骤 1.1 新建一个回调类，识别引擎会回调这个类告知重要状态和识别结果
        val listener = MessageStatusRecogListener(@SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                Log.d(TAG, "handleMessage: ${msg?.obj?.toString()}, status=${msg?.arg1}")
            }
        }, @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                handleResultMsg(msg, webView)
            }
        })
        // DEMO集成步骤 1.2 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例
        myRecognizer = MyRecognizer(context, listener)
        apiParams = OnlineRecogParams()
    }

    private fun setLongRecog(isLong: Boolean, context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sp.edit()
        if (isLong) {
            editor.putString("vad.endpoint-timeout", "0, 开启长语音（离线不支持）。建议pid选择15362。")
        } else {
            editor.putString("vad.endpoint-timeout", "")
        }
        editor.commit()
    }

    private  fun handleResultMsg(msg: Message?, webView: WebView) {
        Log.d(TAG, "handleResultMsg: ${msg?.obj?.toString()}, status=${msg?.arg1}")
        webView.loadUrl("javascript:recognizeResult('${msg?.obj?.toString()}')")
    }

    private fun start(context: Context) {
        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        val params = fetchParams(context)
        // params 也可以根据文档此处手动修改，参数会以json的格式在界面和logcat日志中打印

        // 复制此段可以自动检测常规错误
        AutoCheck(context.applicationContext, @SuppressLint("HandlerLeak")
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

    private fun fetchParams(context: Context): Map<String, Any> {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        //  集成时不需要上面的代码，只需要params参数。
        return apiParams.fetch(sp)
    }


    fun startLongRecog(context: Context) {
        setLongRecog(true,context)
        start(context)
    }

    fun stopLongRecog() {
        myRecognizer.stop()
    }

    fun cancelLongRecog() {
        myRecognizer.cancel()
    }

    fun release() {
        myRecognizer.release()
    }

    companion object {
        private const val TAG = "LongRecogHelper"
        var mInstance: LongRecogHelper? = null

        fun getInstance(): LongRecogHelper? {
            if (mInstance == null) {
                mInstance = LongRecogHelper()
            }

            return mInstance
        }
    }
}