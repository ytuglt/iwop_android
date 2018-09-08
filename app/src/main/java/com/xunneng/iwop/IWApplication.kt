package com.xunneng.iwop

import android.app.Application
import android.util.Log
import com.iflytek.cloud.SpeechUtility

class IWApplication : Application() {
    
    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        SpeechUtility.createUtility(this@IWApplication, "appid=" + getString(R.string.app_id))
        super.onCreate()
    }
    
    companion object {
        private const val TAG = "IWApplication"
    }
}