package me.amanjeet.daggertrack

import com.nhaarman.mockitokotlin2.*
import me.amanjeet.daggertrack.logger.ConsoleLogger
import org.junit.Test

class ConsoleLoggerTest {

    private val logger = mock<Logger>()
    private val daggerTrackClocks = mock<DaggerTrackClocks>()
    private val consoleLogger = ConsoleLogger(logger, daggerTrackClocks)

    @Test
    fun `it should log total time, cpu time and off cpu time on log console`() {
        // given
        whenever(daggerTrackClocks.getUptimeMillis()).thenReturn(100)
        whenever(daggerTrackClocks.getCpuTimeMillis()).thenReturn(94)

        consoleLogger.onInjectionStart()
        whenever(daggerTrackClocks.getUptimeMillis()).thenReturn(105)
        whenever(daggerTrackClocks.getCpuTimeMillis()).thenReturn(96)
        consoleLogger.onInjectionEnd("sleep")

        // then
        verify(logger).d("Total time for sleep injection: 5 ms")
        verify(logger).d("Total on cpu time for sleep injection: 2 ms")
        verify(logger).d("Total off cpu time for sleep injection: 3 ms")
    }
}