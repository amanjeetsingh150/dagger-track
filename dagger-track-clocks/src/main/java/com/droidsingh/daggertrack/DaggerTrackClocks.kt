package com.droidsingh.daggertrack

import android.os.SystemClock

object DaggerTrackClocks {

    init {
        System.loadLibrary("cpu_time")
    }

    @JvmStatic
    fun getUptimeMillis(): Long {
        return SystemClock.uptimeMillis()
    }

    @JvmStatic
    fun getCpuTimeMillis(): Long {
        return getCpuTime()
    }

    private external fun getCpuTime(): Long
}
