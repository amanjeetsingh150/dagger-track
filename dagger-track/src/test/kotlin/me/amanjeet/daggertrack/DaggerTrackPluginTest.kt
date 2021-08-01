package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import javassist.bytecode.ClassFile
import me.amanjeet.daggertrack.utils.GradleTestRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.DataInputStream
import java.io.FileInputStream

internal class DaggerTrackPluginTest {

    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder
        .builder().assureDeletion().build()

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


    @Test
    fun `it includes project files in class pool's classpath and then finally in transformed outputs`() {
        // given
        gradleTestRunner.addSrc(
            srcPath = "minimal/MyApp.java",
            srcContent =
            """
                package minimal;
                import android.app.Application;

                public class MyApp extends Application {
                    @Override
                    public void onCreate() {
                        super.onCreate();
                    }
                }
        """.trimIndent()
        )
        gradleTestRunner.setAppClassName(".MyApp")
        gradleTestRunner.addDependencies(
            "implementation 'org.jetbrains.kotlin:kotlin-stdlib:1.5.10'",
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
            "implementation 'androidx.core:core-ktx:1.5.0'",
            "implementation 'androidx.constraintlayout:constraintlayout:2.0.4'",
            "implementation 'me.amanjeet.daggertrack:dagger-track-clocks:1.0.6-SNAPSHOT'",
        )

        // when
        val result = gradleTestRunner.build()
        val assembleTask = result.getTask(":app:assembleDebug")


        // then
        assertThat(assembleTask.outcome).isEqualTo(TaskOutcome.SUCCESS)
        val transformedFile = result.getTransformedFile("minimal/MyApp.class")
        FileInputStream(transformedFile).use {
            ClassFile(DataInputStream(it)).let { classFile ->
                assertThat(classFile.name).isEqualTo("minimal.MyApp")
            }
        }
    }
}