package me.amanjeet.daggertrack.logger

import me.amanjeet.daggertrack.DaggerTrackClocks
import me.amanjeet.daggertrack.Logger

internal class ConsoleLogger(
    private val injectionTimeLogger: Logger,
    private val daggerTrackClocks: DaggerTrackClocks
) : DaggertrackLogger {

    private var injectionStartUptimeMillis: Long = 0
    private var injectionStartCpuTimeMillis: Long = 0

    override fun onInjectionStart() {
        injectionStartUptimeMillis = daggerTrackClocks.getUptimeMillis()
        injectionStartCpuTimeMillis = daggerTrackClocks.getCpuTimeMillis()
    }

    override fun onInjectionEnd(injectParam: String) {
        if (injectionStartUptimeMillis == 0L) return
        val injectionTime = daggerTrackClocks.getUptimeMillis() - injectionStartUptimeMillis
        val injectionCpuTime = daggerTrackClocks.getCpuTimeMillis() - injectionStartCpuTimeMillis
        val injectionOffCpuTime = injectionTime - injectionCpuTime
        injectionTimeLogger.d("Total time for $injectParam injection: $injectionTime ms")
        injectionTimeLogger.d("Total on cpu time for $injectParam injection: $injectionCpuTime ms")
        injectionTimeLogger.d("Total off cpu time for $injectParam injection: $injectionOffCpuTime ms")
    }
}