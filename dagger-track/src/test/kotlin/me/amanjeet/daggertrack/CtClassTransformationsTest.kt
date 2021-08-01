package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import javassist.ClassPool
import me.amanjeet.daggertrack.utils.GradleTestRunner
import me.amanjeet.daggertrack.utils.createMinimalDaggerAndroidProject
import me.amanjeet.daggertrack.utils.createMinimalDaggerHiltProject
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

    private lateinit var gradleTestRunner: GradleTestRunner

    @Before
    fun setUp() {
        gradleTestRunner = GradleTestRunner(testProjectDir)
    }

    @Test
    fun `it filters the dagger components`() {
        // given
        createMinimalDaggerAndroidProject(gradleTestRunner)
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
        createMinimalDaggerAndroidProject(gradleTestRunner)
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
    fun `it filters out all dagger hilt components`() {
        // given
        createMinimalDaggerHiltProject(gradleTestRunner)
        classPool.makeClass("android.app.Application")
        val result = gradleTestRunner.build(shouldIntegrateDaggerTrack = false)
        val daggerSingletonComponentClassFile = result.getClassFile("minimal/DaggerMyApp_HiltComponents_SingletonC.class")
        val applicationClassFile = result.getClassFile("minimal/Hilt_MyApp.class")
        val classFilesDirectory = daggerSingletonComponentClassFile.parentFile.parentFile
        val daggerSingletonComponent = classPool.makeClass(daggerSingletonComponentClassFile.inputStream())
        val appClass = classPool.makeClass(applicationClassFile.inputStream())
        classPool.insertClassPath(classFilesDirectory.absolutePath)
        val ctClassList = listOf(daggerSingletonComponent, appClass)

        // when
        val daggerHiltComponents = ctClassList.filterDaggerHiltComponents()

        // then
        val daggerHiltComponentNames = daggerHiltComponents.map { it.name }
        assertThat(daggerHiltComponentNames).containsExactly(
            "minimal.DaggerMyApp_HiltComponents_SingletonC",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ServiceCImpl",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl\$ActivityCImpl",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl\$ViewModelCImpl",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl\$ActivityCImpl\$FragmentCI",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl\$ActivityCImpl\$ViewCI",
            "minimal.DaggerMyApp_HiltComponents_SingletonC\$ActivityRetainedCImpl\$ActivityCImpl\$FragmentCI\$ViewWithFragmentCI"
        )
    }
}