package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import javassist.ClassPool
import javassist.CtClass
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.ACTIVITY_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.ACTIVITY_RETAINED_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.FRAGMENT_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.SERVICE_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.SINGLETON_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.VIEWMODEL_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.VIEW_C_IMPL
import me.amanjeet.daggertrack.utils.DaggerComponentsFixtureCreator.Companion.VIEW_WITH_FRAGMENT_C_IMPL
import me.amanjeet.daggertrack.utils.GradleTestRunner
import me.amanjeet.daggertrack.utils.MinimalProjectCreator
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

internal class CtClassTransformationsTest {

    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder
        .builder().assureDeletion().build()

    private val classPool = ClassPool.getDefault()
    private val applicationClass = classPool.makeClass("android.app.Application")

    private lateinit var gradleTestRunner: GradleTestRunner

    @Before
    fun setUp() {
        gradleTestRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun `it filters the dagger components`() {
        // given
        MinimalProjectCreator.createDaggerAndroidProject(gradleTestRunner)
        classPool.makeClass("dagger.android.HasAndroidInjector")
        val result = gradleTestRunner.build(shouldIntegrateDaggerTrack = false)
        val appModuleClassFile = result.getClassFile("minimal/AppModule.class")
        val appComponentClassFile = result.getClassFile("minimal/ApplicationComponent.class")
        val daggerAppComponentClassFile = result.getClassFile("minimal/DaggerApplicationComponent.class")
        val appModule = classPool.makeClass(appModuleClassFile.inputStream())
        val appComponent = classPool.makeClass(appComponentClassFile.inputStream())
        val daggerAppComponent = classPool.makeClass(daggerAppComponentClassFile.inputStream())
        val ctClassList = listOf(appComponent, appModule, daggerAppComponent)

        // when
        val daggerComponents = ctClassList.filterDaggerComponents()

        // then
        val daggerComponentNames = daggerComponents.map { it.name }
        assertThat(daggerComponentNames).containsExactly("minimal.DaggerApplicationComponent")
    }

    @Test
    fun `it filters out the subcomponents from components`() {
        // given
        MinimalProjectCreator.createDaggerAndroidProject(gradleTestRunner)
        val result = gradleTestRunner.build(shouldIntegrateDaggerTrack = false)
        val daggerAppComponentClassFile = result.getClassFile("minimal/DaggerApplicationComponent.class")
        val classFilesDirectory = daggerAppComponentClassFile.parentFile.parentFile
        val daggerAppComponent = classPool.makeClass(daggerAppComponentClassFile.inputStream())
        classPool.insertClassPath(classFilesDirectory.absolutePath)

        // when
        val filteredSubComponents = daggerAppComponent.filterSubcomponents()

        // then
        val subComponentNames = filteredSubComponents.map { it.name }
        assertThat(subComponentNames).containsExactly("minimal.DaggerApplicationComponent\$HomeActivitySubcomponentImpl")
    }

    @Test
    fun `it filters out all the dagger hilt components`() {
        // given
        val hiltDaggerApp = classPool.makeClass("me.amanjeet.daggertrack.Hilt_DaggerTrackApp")
        hiltDaggerApp.superclass = applicationClass
        val singletonRootC = DaggerComponentsFixtureCreator().createHiltComponentTree()
        val homeActivity = mock<CtClass>()
        val homePresenter = mock<CtClass>()
        val ctClassList = listOf(
            hiltDaggerApp, singletonRootC,
            homeActivity, homePresenter
        )

        // when
        val hiltComponents = ctClassList.filterDaggerHiltComponents()

        // then
        val hiltComponentNames = hiltComponents.map { it.name }
        assertThat(hiltComponentNames).containsExactly(
            SINGLETON_C_IMPL,
            SERVICE_C_IMPL,
            ACTIVITY_RETAINED_C_IMPL,
            VIEWMODEL_C_IMPL,
            ACTIVITY_C_IMPL,
            VIEW_C_IMPL,
            VIEW_WITH_FRAGMENT_C_IMPL,
            FRAGMENT_C_IMPL
        )
    }
}