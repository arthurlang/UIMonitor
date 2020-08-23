package com.lj.framemonitor.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import org.json.JSONObject

//1：捕获1条卡顿message，如何获取调用这条message的调用栈
//2：DroidAssist代理用到的所有Handler :还有其他方法也能实现全局替换handler的gradle插件，除了使用AspectJ实现之外
open class GlobalHandler : Handler {
    private val mStackTraceRunnable: Runnable = Runnable { mMainStackTrace = Utils.getStack(Looper.getMainLooper().thread.stackTrace) }
    private lateinit var mWatchDogHandler: Handler
    private var mMainStackTrace: String? = ""
    private var mStartTime = System.currentTimeMillis()
    private var block = false
    constructor() : super(Looper.myLooper(), null) {}
    constructor(callback: Callback?) : super(Looper.myLooper(), callback) {}
    constructor(looper: Looper?, callback: Callback?) : super(looper, callback) {}
    constructor(looper: Looper?) : super(looper) {}

    override fun sendMessageAtTime(msg: Message?, uptimeMillis: Long): Boolean {
        Log.e("GlobalHandler", "-----sendMessageAtTime")
        val send: Boolean = super.sendMessageAtTime(msg, uptimeMillis)
        if (send && ViewUtils.isUiThread()) {
            Utils.getMsgStackTrace()[msg] = Log.getStackTraceString(Throwable()).replace("java.lang.Throwable", "")
        }
        return send
    }

    override fun dispatchMessage(msg: Message) {
        Log.e("GlobalHandler", "-----dispatchMessage")
        if (ViewUtils.isUiThread()) {
            var ht = HandlerThread("block-watcher")
            ht.start()
            Log.e("GlobalHandler", "-----ht.looper="+ht.looper)
            mWatchDogHandler = Handler(ht.looper)
            mWatchDogHandler.postDelayed(mStackTraceRunnable, (Config.IS_BLOCK_MSG_THRESHOLD*0.8f).toLong())
            mStartTime = System.currentTimeMillis()
        }

        super.dispatchMessage(msg)

        if (Utils.getMsgStackTrace().containsKey(msg) && ViewUtils.isUiThread()) {
            var cost = System.currentTimeMillis() - mStartTime
            Log.e("GlobalHandler","Msg_Cost :"+cost)
            if(cost > Config.IS_BLOCK_MSG_THRESHOLD) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("Msg_Cost", cost)
                    jsonObject.put("Msg_send_Trace", mMainStackTrace)
                    mMainStackTrace = null
                    jsonObject.put("Msg_end_Trace", msg.getTarget().toString() + " " + Utils.getMsgStackTrace()[msg])

                    //upload to apm
                    Log.e("---",">>>>>>>>>>>>>>>>>>>>>>> maybe happens block message! <<<<<<<<<<<<<<<<<<<<<<<MsgDetail $jsonObject")
                } catch (e: Exception) {

                }
            } else {
                if(mWatchDogHandler != null) {
                    mWatchDogHandler.removeCallbacks(mStackTraceRunnable)
                }
            }
            Utils.getMsgStackTrace().remove(msg)
        }
    }
}