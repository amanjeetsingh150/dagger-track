package me.amanjeet.daggertrack

import android.os.SystemClock

interface DaggerTrackClocks {

    fun getUptimeMillis(): Long
    fun getCpuTimeMillis(): Long
}

object DaggerTrackClocksImpl: DaggerTrackClocks {

    init {
        System.loadLibrary("cpu_time")
    }

    override fun getUptimeMillis(): Long {
        return SystemClock.uptimeMillis()
    }

    override fun getCpuTimeMillis(): Long {
        return getCpuTime()
    }

    private external fun getCpuTime(): Long
}
