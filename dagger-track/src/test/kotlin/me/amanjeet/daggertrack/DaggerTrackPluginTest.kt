package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import javassist.ClassPool
import javassist.bytecode.ClassFile
import me.amanjeet.daggertrack.utils.*
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.DataInputStream
import java.io.FileInputStream

internal class DaggerTrackPluginTest {

    private val classPool = ClassPool.getDefault()

    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder
        .builder().assureDeletion().build()

    private lateinit var gradleTestRunner: GradleTestRunner

    @Before
    fun setUp() {
        gradleTestRunner = GradleTestRunner(testProjectDir)
        classPool.makeClass("me.amanjeet.daggertrack.DaggerTrackClocks")
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
            "implementation 'me.amanjeet.daggertrack:dagger-track-clocks:LOCAL_SNAPSHOT'"
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

    @Test
    fun `it adds tracking to dagger android components and subcomponents inject method`() {
        // given
        createMinimalDaggerAndroidProject(gradleTestRunner)

        // when
        val result = gradleTestRunner.build()
        val assembleTask = result.getTask(":app:assembleDebug")

        // then
        assertThat(assembleTask.outcome).isEqualTo(TaskOutcome.SUCCESS)
        verifyDaggerAppComponentTracking(classPool, result)
        verifyDaggerSubcomponentTracking(classPool, result)
    }

    @Test
    fun `it adds clock tracking to dagger hilt components`() {
        // given
        createMinimalDaggerHiltProject(gradleTestRunner)

        // when
        val result = gradleTestRunner.build()
        val assembleTask = result.getTask(":app:assembleDebug")

        // then
        assertThat(assembleTask.outcome).isEqualTo(TaskOutcome.SUCCESS)

        val homeActivityDaggerHiltComponent = classPool.getCtClassFromName(result,
            "minimal/DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl\$ActivityCImpl.class"
        )
        val expectedInjectHomeActivityMethodCalls = arrayOf(
            "getUptimeMillis",
            "getCpuTimeMillis",
            "injectHomeActivity2",
            "getUptimeMillis",
            "getCpuTimeMillis"
        )
        val injectHomeActivityMethodCalls = homeActivityDaggerHiltComponent.getMethodCalls("injectHomeActivity")
            .filter { expectedInjectHomeActivityMethodCalls.contains(it) }
        assertThat(injectHomeActivityMethodCalls).isEqualTo(expectedInjectHomeActivityMethodCalls.toList())
    }

    private fun verifyDaggerSubcomponentTracking(
        classPool: ClassPool,
        result: GradleTestRunner.Result
    ) {
        val daggerHomeSubComponent = classPool.getCtClassFromName(
            result,
            "minimal/DaggerApplicationComponent\$HomeActivitySubcomponentImpl.class"
        )
        val expectedInjectHomeActivityMethodCalls = arrayOf(
            "getUptimeMillis",
            "getCpuTimeMillis",
            "injectHomeActivity",
            "getUptimeMillis",
            "getCpuTimeMillis"
        )
        val injectHomeActivityMethodCalls = daggerHomeSubComponent.getMethodCalls("inject")
            .filter { expectedInjectHomeActivityMethodCalls.contains(it) }
        assertThat(injectHomeActivityMethodCalls).isEqualTo(expectedInjectHomeActivityMethodCalls.toList())
    }

    private fun verifyDaggerAppComponentTracking(
        classPool: ClassPool,
        result: GradleTestRunner.Result
    ) {
        val daggerApplicationComponent = classPool.getCtClassFromName(
            result,
            "minimal/DaggerApplicationComponent.class"
        )
        val expectedInjectMyAppMethodCalls = arrayOf(
            "getUptimeMillis",
            "getCpuTimeMillis",
            "injectMyApp",
            "getUptimeMillis",
            "getCpuTimeMillis"
        )
        val injectMyAppMethodCalls = daggerApplicationComponent.getMethodCalls("inject")
            .filter { expectedInjectMyAppMethodCalls.contains(it) }
        assertThat(injectMyAppMethodCalls).isEqualTo(expectedInjectMyAppMethodCalls.toList())
    }
}