package com.xunneng.iwop.recognize

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.iflytek.cloud.*
import com.xunneng.iwop.utils.Constant.PREFER_NAME

class TtsHelper {
    private var mEngineType = SpeechConstant.TYPE_CLOUD
    // 默认发音人
    private var voicer = "xiaoyan"

    private lateinit var mTts: SpeechSynthesizer
    private lateinit var mSharedPreferences: SharedPreferences

    fun init(context: Context) {
        mSharedPreferences = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE)

        mTts = SpeechSynthesizer.createSynthesizer(context) {
            Log.d(TAG, "init: InitListener init() code = $it")
            if (it != ErrorCode.SUCCESS) {
                Toast.makeText(context, "初始化失败：错误码$it", Toast.LENGTH_SHORT).show()
            } else {

            }
        }
    }

    fun setParam() {
        // 清空参数
        mTts?.setParameter(SpeechConstant.PARAMS, null)
        // 根据合成引擎设置相应参数
        if (mEngineType == SpeechConstant.TYPE_CLOUD) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
            //onevent回调接口实时返回音频流数据
            //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer)
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"))
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"))
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"))
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL)
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "")
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"))
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true")

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm")
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory().toString() + "/msc/tts.pcm")
    }

    fun startTtsPlay(text: String) {
        Log.d(TAG, "startTtsPlay: $text")
        setParam()
        val code = mTts.startSpeaking(text, object : SynthesizerListener {

            override fun onSpeakBegin() {
            }

            override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {
            }

            override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {
            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
            }

            override fun onSpeakPaused() {
            }

            override fun onSpeakResumed() {
            }

            override fun onCompleted(p0: SpeechError?) {
            }

        })

    }

    fun cancelTtsPlay() {
        mTts.stopSpeaking()
    }

    fun pauseTtsPlay() {
        mTts.pauseSpeaking()
    }

    fun resumeTtsPlay() {
        mTts.resumeSpeaking()
    }

    fun destroy() {
        mTts.stopSpeaking()
        mTts.destroy()
    }
    companion object {
        private const val TAG = "TtsHelper"
        private var mInstance: TtsHelper? = null
            get() {
                if (field == null) {
                    field = TtsHelper()
                }
                return field
            }

        fun get(): TtsHelper {
            return mInstance!!
        }
    }
}