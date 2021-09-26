package me.amanjeet.daggertrack.instrumentation

import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import me.amanjeet.daggertrack.DaggerTrack
import me.amanjeet.daggertrack.instrumentation.di.DaggerTestAppComponent
import org.junit.Before
import org.junit.Test

class DaggerTrackConsoleActivityTest {

    private lateinit var testApplication: TestApplication

    @Before
    fun setup() {
        testApplication = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as TestApplication
        DaggerTestAppComponent.builder().build()
            .inject(testApplication)
    }

    @Test
    fun failsWhenDaggerTrackConfigsAreNegative() {
        // given
        DaggerTrack.config = DaggerTrack.config.copy(minWallClockTimeMillis = -100)

        // when
        try {
            DaggerTrack.manualInstall()
            ActivityScenario.launch(DaggerTrackConsoleActivity::class.java)
        } catch (exception: Exception) {
            // then
            assertThat(exception).isInstanceOf(IllegalStateException::class.java)
        }
    }
}