package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import me.amanjeet.daggertrack.logger.ConsoleLogger
import me.amanjeet.daggertrack.logger.LoggerFactory
import me.amanjeet.daggertrack.logger.TrackerActivityLogger
import org.junit.Test

internal class LoggerFactoryTest {

    @Test
    fun `it should return console logger when type is console`() {
        // given
        val loggerType = DaggerTrack.LoggerType.CONSOLE

        // when
        val logger = LoggerFactory.createLogger(loggerType)

        // then
        assertThat(logger).isInstanceOf(ConsoleLogger::class.java)
    }
    
    @Test
    fun `it should return tracker activity logger when type is tracker activity`() {
        // given
        val loggerType = DaggerTrack.LoggerType.TRACKER_ACTIVITY

        // when
        val logger = LoggerFactory.createLogger(loggerType)

        // then
        assertThat(logger).isInstanceOf(TrackerActivityLogger::class.java)
    }
}