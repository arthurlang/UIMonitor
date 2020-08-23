package com.lj.framemonitor.util

import android.os.Looper

object ViewUtils {
    fun isUiThread(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper()
    }
}