package me.amanjeet.daggertrack.logger

import android.os.SystemClock
import android.util.Log
import me.amanjeet.daggertrack.DaggerTrackClocks

internal class ConsoleLogger : Logger {

    private var injectionStartUptimeMillis: Long = 0
    private var injectionStartCpuTimeMillis: Long = 0

    override fun onInjectionStart() {
        injectionStartUptimeMillis = DaggerTrackClocks.getUptimeMillis()
        injectionStartCpuTimeMillis = DaggerTrackClocks.getCpuTimeMillis()
    }

    override fun onInjectionEnd(injectParam: String) {
        if (injectionStartUptimeMillis == 0L) return
        val injectionTime = SystemClock.uptimeMillis() - injectionStartUptimeMillis
        val injectionCpuTime = DaggerTrackClocks.getCpuTimeMillis() - injectionStartCpuTimeMillis
        val injectionOffCpuTime = injectionTime - injectionCpuTime
        Log.d("DaggerTrack", "Total time for $injectParam injection: $injectionTime ms")
        Log.d("DaggerTrack", "Total on cpu time for $injectParam injection: $injectionCpuTime ms")
        Log.d(
            "DaggerTrack",
            "Total off cpu time for $injectParam injection: $injectionOffCpuTime ms"
        )
    }
}