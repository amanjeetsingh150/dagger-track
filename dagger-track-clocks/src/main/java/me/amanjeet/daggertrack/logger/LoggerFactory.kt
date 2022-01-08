package me.amanjeet.daggertrack.logger

import me.amanjeet.daggertrack.DaggerTrack
import me.amanjeet.daggertrack.DaggerTrackClocks
import me.amanjeet.daggertrack.InjectionTimeLogger

internal class LoggerFactory private constructor() {

    companion object {

        @JvmStatic
        fun createLogger(
            loggerType: DaggerTrack.LoggerType,
            daggerTrackClocks: DaggerTrackClocks
        ): DaggertrackLogger {
            val injectionTimeLogger = InjectionTimeLogger()
            return when (loggerType) {
                DaggerTrack.LoggerType.TRACKER_ACTIVITY -> TrackerActivityLogger()
                DaggerTrack.LoggerType.CONSOLE -> ConsoleLogger(injectionTimeLogger, daggerTrackClocks)
            }
        }
    }
}