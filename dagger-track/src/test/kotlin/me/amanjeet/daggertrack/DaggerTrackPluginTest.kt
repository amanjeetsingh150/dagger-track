package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import me.amanjeet.daggertrack.utils.GradleTestRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DaggerTrackPluginTest {

    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var gradleTestRunner: GradleTestRunner

    @Before
    fun setUp() {
        gradleTestRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun `dagger track fails if it is not applied to android app or library`() {
        // when
        val result = gradleTestRunner
            .buildAndFailJavaLibrary()

        // then
        assertThat(
            result.getOutput().contains(
                "'com.android.application' or 'com.android.library' plugin required"
            )
        ).isTrue()
    }

    @Test
    fun `dagger track fails when dagger-track-clock artifacts are not added`() {
        // given
        gradleTestRunner.addSrc(
            srcPath = "minimal/MainActivity.java",
            srcContent =
            """
            package minimal;
            import android.os.Bundle;
            import androidx.appcompat.app.AppCompatActivity;

            public class MainActivity extends AppCompatActivity {
                @Override
                public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                }
            }
        """.trimIndent()
        )
        gradleTestRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'"
        )
        gradleTestRunner.addActivities(
            "<activity android:name=\".MainActivity\"/>"
        )

        // when
        val result = gradleTestRunner.buildAndFail()

        // then
        assertThat(
            result.getOutput().contains(
                "\"dagger-track-clocks\" dependency needed for dagger-track plugin"
            )
        ).isTrue()
    }

}