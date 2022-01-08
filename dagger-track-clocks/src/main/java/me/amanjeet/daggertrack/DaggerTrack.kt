package me.amanjeet.daggertrack

import android.util.Log
import me.amanjeet.daggertrack.logger.DaggertrackLogger
import me.amanjeet.daggertrack.logger.LoggerFactory

object DaggerTrack {

    @JvmStatic
    var config = Config()
        set(newConfig) {
            val previousConfig = field
            field = newConfig
            logConfigChange(previousConfig, newConfig)
        }

    private lateinit var logger: DaggertrackLogger

    data class Config(
        /** Minimum tolerance for wall clock time, defaults to 0ms **/
        val minWallClockTimeMillis: Long = 0,

        /** Minimum tolerance for on cpu time, defaults to 0ms **/
        val minOnCpuTimeMillis: Long = 0,

        /** Minimum tolerance for off cpu time, default to 0 ms **/
        val minOffCpuTimeMillis: Long = 0,

        /** Type of logging configured, defaults to tracker activity type **/
        val loggerType: LoggerType = LoggerType.TRACKER_ACTIVITY
    ) {
        fun newBuilder() = Builder(this)

        class Builder internal constructor(config: Config) {
            private var minWallClockTimeMillis = config.minWallClockTimeMillis
            private var minOnCpuTimeMillis = config.minOnCpuTimeMillis
            private var minOffCpuTimeMillis = config.minOffCpuTimeMillis
            private var loggerType = config.loggerType

            fun minWallClockTimeMillis(minWallClockTimeMillis: Long) =
                apply { this.minWallClockTimeMillis = minWallClockTimeMillis }

            fun minOnCpuTimeMillis(minOnCPUTimeMillis: Long) =
                apply { this.minOnCpuTimeMillis = minOnCPUTimeMillis }

            fun minOffCpuTimeMillis(minOffCpuTimeMillis: Long) =
                apply { this.minOffCpuTimeMillis = minOffCpuTimeMillis }

            fun loggerType(loggerType: LoggerType) = apply { this.loggerType = loggerType }

            fun build() = config.copy(
                minWallClockTimeMillis = minWallClockTimeMillis,
                minOnCpuTimeMillis = minOnCpuTimeMillis,
                minOffCpuTimeMillis = minOffCpuTimeMillis,
                loggerType = loggerType
            )
        }
    }

    enum class LoggerType {
        TRACKER_ACTIVITY,
        CONSOLE
    }

    @JvmStatic
    fun manualInstall() {
        check(
            config.minOffCpuTimeMillis >= 0 &&
                    config.minOnCpuTimeMillis >= 0 &&
                    config.minWallClockTimeMillis >= 0
        ) {
            "Minimum tolerance for wall clock time, off cpu time or on cpu time should be at least 0 ms."
        }

        val loggerType = config.loggerType
        logger = LoggerFactory.createLogger(loggerType, DaggerTrackClocksImpl)
    }

    @JvmStatic
    fun onInjectionStart() {
        if (::logger.isInitialized) {
            logger.onInjectionStart()
        }
    }

    @JvmStatic
    fun onInjectionEnd(injectParam: String) {
        if (::logger.isInitialized) {
            logger.onInjectionEnd(injectParam)
        }
    }

    private fun logConfigChange(previousConfig: Config, newConfig: Config) {
        val changedFields = mutableListOf<String>()
        Config::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            val previousValue = field[previousConfig]
            val newValue = field[newConfig]
            if (previousValue != newValue) {
                changedFields += "${field.name}=$newValue"
            }
            if (changedFields.isNotEmpty()) {
                val changesInConfig = changedFields.joinToString(", ")
                Log.d("DaggerTrack", "Updated config: $changesInConfig")
            } else {
                Log.d("DaggerTrack", "No config changes")
            }
        }
    }
}