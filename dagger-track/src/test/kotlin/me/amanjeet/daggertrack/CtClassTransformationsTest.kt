package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
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
import me.amanjeet.daggertrack.utils.addSubcomponentAnnotation
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
        gradleTestRunner.addSrc(
            srcPath = "minimal/ApplicationComponent.java",
            srcContent = """
                    package minimal;
    
                    import android.app.Application;
                    import dagger.BindsInstance;
                    import dagger.Component;
                    import dagger.android.AndroidInjectionModule;
                    import dagger.android.support.AndroidSupportInjectionModule;
                    import minimal.MyApp;
                    import javax.inject.Singleton;
    
                    @Singleton
                    @Component(
                        modules = {
                            AndroidInjectionModule.class,
                            AppModule.class,
                            AndroidSupportInjectionModule.class
                        }
                    )
                    interface ApplicationComponent {
    
                        void inject(MyApp myApplication);
    
                        @Component.Builder
                        interface Builder {
                            Builder bindApplication(@BindsInstance Application application);
                            ApplicationComponent build();
                        }
                    }
                """.trimIndent()
        )
    }

    @Test
    fun `it filters the dagger components`() {
        // given
        gradleTestRunner.addSrc(
            srcPath = "minimal/AppModule.java",
            srcContent = """
                package minimal;

                import android.app.Application;
                import android.content.Context;
                import dagger.Binds;
                import dagger.Module;
                import javax.inject.Singleton;

                @Module
                abstract class AppModule {

                    @Binds
                    @Singleton
                    abstract Context provideApplicationContext(Application application);
                }
            """.trimIndent()
        )
        gradleTestRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
            "implementation 'com.google.dagger:dagger-android-support:2.35.1'",
            "annotationProcessor 'com.google.dagger:dagger-android-processor:2.35.1'",
            "annotationProcessor 'com.google.dagger:dagger-compiler:2.35.1'"
        )
        val result = gradleTestRunner.build(shouldIntegrateDaggerTrack = false)
        val appClassFile = result.getClassFile("minimal/MyApp.class")
        val appModuleClassFile = result.getClassFile("minimal/AppModule.class")
        val appComponentClassFile = result.getClassFile("minimal/ApplicationComponent.class")
        val daggerAppComponentClassFile = result.getClassFile("minimal/DaggerApplicationComponent.class")
        val appClass = classPool.makeClass(appClassFile.inputStream())
        val appModule = classPool.makeClass(appModuleClassFile.inputStream())
        val appComponent = classPool.makeClass(appComponentClassFile.inputStream())
        val daggerAppComponent = classPool.makeClass(daggerAppComponentClassFile.inputStream())
        val ctClassList = listOf(appClass, appComponent, appModule, daggerAppComponent)

        // when
        val daggerComponents = ctClassList.filterDaggerComponents()

        // then
        val daggerComponentNames = daggerComponents.map { it.name }
        assertThat(daggerComponentNames).containsExactly("minimal.DaggerApplicationComponent")
    }

    @Test
    fun `it filters out all the subcomponents of component`() {
        // given
        val homeActivitySubcomponentImpl = classPool.makeClass(
            "me.amanjeet.daggertrack.di.components.HomeActivitySubcomponentImpl"
        )
        val homeFragmentSubcomponentImpl = classPool.makeClass(
            "me.amanjeet.daggertrack.di.components.HomeFragmentSubcomponentImpl"
        )
        // for convenience lets assume subcomponent class is static
        // dagger does not create static subcomponents
        // javassist does not supports to create non-static classes
        val homeFragmentNestedSubcomponentImpl = homeActivitySubcomponentImpl.makeNestedClass(
            homeFragmentSubcomponentImpl.name,
            true
        )
        prepareHomeActivity(homeActivitySubcomponentImpl)
        prepareHomeFragment(homeFragmentNestedSubcomponentImpl)
        val applicationComponent = mock<CtClass>()
        whenever(applicationComponent.nestedClasses).thenReturn(arrayOf(homeActivitySubcomponentImpl))

        // when
        val subComponentCtClassList = applicationComponent.filterSubcomponents()

        // then
        val subcomponentListNames = subComponentCtClassList.map { it.name }
        assertThat(subcomponentListNames).containsExactly(
            homeActivitySubcomponentImpl.name, homeFragmentNestedSubcomponentImpl.name
        )
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

    private fun prepareHomeActivity(homeActivitySubcomponentImpl: CtClass) {
        val homeActivitySubcomponent = classPool.makeInterface(
            "me.amanjeet.daggertrack.di.components.HomeActivitySubcomponent"
        )
        homeActivitySubcomponentImpl.addInterface(homeActivitySubcomponent)
        homeActivitySubcomponent.addSubcomponentAnnotation()
    }

    private fun prepareHomeFragment(homeFragmentNestedSubcomponentImpl: CtClass) {
        val homeFragmentSubcomponent = classPool.makeInterface(
            "me.amanjeet.daggertrack.di.components.HomeFragmentSubcomponent"
        )
        homeFragmentNestedSubcomponentImpl.addInterface(homeFragmentSubcomponent)
        homeFragmentSubcomponent.addSubcomponentAnnotation()
    }
}