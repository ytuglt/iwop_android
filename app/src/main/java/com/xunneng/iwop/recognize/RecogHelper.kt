package com.xunneng.iwop.recognize

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.WebView
import com.iflytek.cloud.*
import com.xunneng.iwop.utils.FucUtil
import com.xunneng.iwop.utils.JsonParser
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class RecogHelper(var webview: WebView) {

    // 语音听写对象
    private lateinit var mIat: SpeechRecognizer
    // 引擎类型
    private val mEngineType = SpeechConstant.TYPE_CLOUD
    private val mIatResults = LinkedHashMap<String, String>()

    companion object {
        private const val TAG = "RecogHelper"
        var mInstance: RecogHelper? = null

        fun getInstance(webView: WebView): RecogHelper? {
            if (mInstance == null) {
                mInstance = RecogHelper(webView)
            }

            return mInstance
        }
    }

    fun init(context: Context) {
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(context) { code ->
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG, "初始化失败，错误码：$code")
            }
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    fun setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null)

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType)
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json")


        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin")

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000")

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000")

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0")

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory().toString() + "/msc/iat.wav")
    }

    fun startRecognize() {
        Log.d(TAG, "localMethods:")
        setParam()
        // 不显示听写对话框
        var ret = mIat.startListening(mRecognizerListener)
        if (ret != ErrorCode.SUCCESS) {
            Log.d(TAG, "startRecognize: 听写失败,错误码：$ret")
        }
    }


    /**
     * 听写监听器。
     */
    private val mRecognizerListener = object : RecognizerListener {

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            Log.d(TAG, "onBeginOfSpeech: 开始说话")
        }

        override fun onError(error: SpeechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (error.errorCode == 14002) {
                Log.d(TAG, "onError: ${error.getPlainDescription(true)}\n请确认是否已开通翻译功能")
            } else {
                Log.d(TAG, "onError: ${error.getPlainDescription(true)}")
            }
        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            Log.d(TAG, "onEndOfSpeech: 结束说话")
        }

        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            printResult(results)

            if (isLast) {
                // TODO 最后的结果
            }
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            Log.d(TAG, "onVolumeChanged: 当前正在说话，音量大小：$volume")
        }

        override fun onEvent(p0: Int, p1: Int, p2: Int, obj: Bundle?) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    }

    private fun printResult(results: RecognizerResult) {
        val text = JsonParser.parseIatResult(results.resultString)

        var sn: String? = null
        // 读取json结果中的sn字段
        try {
            val resultJson = JSONObject(results.resultString)
            sn = resultJson.optString("sn")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        if (sn != null) {
            mIatResults[sn] = text
        }

        val resultBuffer = StringBuffer()
        for (key in mIatResults.keys) {
            resultBuffer.append(mIatResults.get(key))
        }

        var textString = resultBuffer.toString()
        Log.d(TAG, "printResult: resultBuffer.toString() =  " + resultBuffer.toString())
        webview.loadUrl("javascript:recognizeResult('$textString')")

    }

    fun uploadUserWords(context: Context) {
        val contents = FucUtil.readFile(context, "userwords", "utf-8")
// 指定引擎类型
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")
        var ret = mIat.updateLexicon("userword", contents, mLexiconListener)
        if (ret != ErrorCode.SUCCESS)
            Log.e(TAG,"上传热词失败,错误码：$ret")
    }

    /**
     * 上传联系人/词表监听器。
     */
    private val mLexiconListener = LexiconListener { lexiconId, error ->
        Log.d(TAG,"upload")
        if (error != null) {
            webview.loadUrl("javascript:uploadUserWordResult('${error.toString()}')")
        } else {
            webview.loadUrl("javascript:uploadUserWordResult('upload success')")
        }
    }

}