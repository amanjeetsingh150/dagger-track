package com.droidsingh.daggertrack

import android.os.SystemClock

object DaggerTrackClocks {

    fun getUptimeMillis(): Long {
        return SystemClock.uptimeMillis()
    }
}