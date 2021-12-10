package me.amanjeet.daggertrack.logger

import me.amanjeet.daggertrack.DaggerTrack

internal class LoggerFactory private constructor() {

    companion object {

        @JvmStatic
        fun createLogger(loggerType: DaggerTrack.LoggerType): DaggertrackLogger {
            return when (loggerType) {
                DaggerTrack.LoggerType.TRACKER_ACTIVITY -> TrackerActivityLogger()
                DaggerTrack.LoggerType.CONSOLE -> ConsoleLogger()
            }
        }
    }
}