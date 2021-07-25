package me.amanjeet.daggertrack.utils

object MinimalProjectCreator {

    fun createDaggerAndroidProject(gradleTestRunner: GradleTestRunner) {
        gradleTestRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'",
            "implementation 'com.google.dagger:dagger-android-support:2.35.1'",
            "annotationProcessor 'com.google.dagger:dagger-android-processor:2.35.1'",
            "annotationProcessor 'com.google.dagger:dagger-compiler:2.35.1'"
        )
        createApplicationClass(gradleTestRunner)
        createApplicationComponent(gradleTestRunner)
        createDaggerModules(gradleTestRunner)
        createHomeScreenDeps(gradleTestRunner)
        createHomeActivity(gradleTestRunner)
    }

    private fun createApplicationClass(gradleTestRunner: GradleTestRunner) {
        gradleTestRunner.addSrc(
            srcPath = "minimal/MyApp.java",
            srcContent =
            """
               package minimal;
               
               import android.app.Application;
               import javax.inject.Inject;
               import dagger.android.AndroidInjector;
               import dagger.android.DispatchingAndroidInjector;
               import dagger.android.HasAndroidInjector;
               
               public class MyApp extends Application implements HasAndroidInjector {
                   
                   @Inject
                   DispatchingAndroidInjector<Object> androidInjector;
     
                   @Override
                   public void onCreate() {
                       super.onCreate();
                   }
                   
                   @Override
                    public AndroidInjector<Object> androidInjector() {
                        return androidInjector;
                    }
               }
            """.trimIndent()
        )
        gradleTestRunner.setAppClassName(".MyApp")
    }

    private fun createHomeScreenDeps(gradleTestRunner: GradleTestRunner) {
        gradleTestRunner.addSrc(
            srcPath = "minimal/HomeDependency.java",
            srcContent = """
                    package minimal;
                    
                        class HomeDependency {
                            
                            public HomeDependency() {
                               // some home dependency
                            }
                        }
                """.trimIndent()
        )
    }

    private fun createHomeActivity(gradleTestRunner: GradleTestRunner) {
        gradleTestRunner.addSrc(
                srcPath = "minimal/HomeActivity.java",
                srcContent =
            """
                package minimal;
                
                import android.os.Bundle;
                import androidx.appcompat.app.AppCompatActivity;
                import javax.inject.Inject;
                import dagger.android.AndroidInjector;
                import dagger.android.DispatchingAndroidInjector;
                import dagger.android.HasAndroidInjector;
                
                    public class HomeActivity extends AppCompatActivity implements HasAndroidInjector {
            
                        @Inject
                        DispatchingAndroidInjector<Object> androidInjector;
    
                        @Override
                        public void onCreate(Bundle savedInstanceState) {
                            super.onCreate(savedInstanceState);
                        }
                        
                        @Override
                        public AndroidInjector<Object> androidInjector() {
                            return androidInjector;
                        }
                }
            """.trimIndent()
        )
        gradleTestRunner.addActivities(
            "<activity android:name=\".HomeActivity\"/>"
        )
    }

    private fun createApplicationComponent(gradleTestRunner: GradleTestRunner) {
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
                                AndroidSupportInjectionModule.class,
                                HomeActivityModule.class
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

    private fun createDaggerModules(gradleTestRunner: GradleTestRunner) {
        gradleTestRunner.addSrc(
            srcPath = "minimal/HomeActivityModule.java",
            srcContent = """
                    package minimal;
                    
                    import dagger.Module;
                    import dagger.android.ContributesAndroidInjector;
                    import minimal.HomeActivity;
    
                    @Module
                    abstract class HomeActivityModule {
    
                        @ContributesAndroidInjector(modules = { HomeModule.class })
                        abstract HomeActivity homeActivity();
                    }
                """.trimIndent()
        )
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
        gradleTestRunner.addSrc(
            srcPath = "minimal/HomeModule.java",
            srcContent = """
                    package minimal;
    
                    import dagger.Module;
                    import dagger.Provides;
                    import minimal.HomeDependency;
    
                    @Module
                    class HomeModule {
    
                        @Provides
                        HomeDependency providesHeavyDependencyOne() {
                            return new HomeDependency();
                        }
                    }
                """.trimIndent()
        )
    }
}