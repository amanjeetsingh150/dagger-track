package me.amanjeet.daggertrack.utils

fun createMinimalDaggerHiltProject(gradleTestRunner: GradleTestRunner) {
    gradleTestRunner.addDependencies(
        "implementation 'androidx.appcompat:appcompat:1.1.0'",
        "implementation 'com.google.dagger:dagger-android-support:2.35.1'",
        "implementation 'com.google.dagger:hilt-android:2.35'",
        "annotationProcessor 'com.google.dagger:hilt-android-compiler:2.35'",
        "annotationProcessor 'com.google.dagger:dagger-android-processor:2.35.1'",
        "annotationProcessor 'com.google.dagger:dagger-compiler:2.35.1'"
    )
    gradleTestRunner.addPlugins("id 'dagger.hilt.android.plugin'")
    createApplicationClass(gradleTestRunner)
    createHomeScreenDeps(gradleTestRunner)
    createDaggerModules(gradleTestRunner)
    createHomeActivity(gradleTestRunner)
}

private fun createApplicationClass(gradleTestRunner: GradleTestRunner) {
    gradleTestRunner.addSrc(
        srcPath = "minimal/MyApp.java",
        srcContent =
        """
               package minimal;
               
               import android.app.Application;
               import dagger.hilt.android.HiltAndroidApp;
               
               @HiltAndroidApp
               public class MyApp extends Application {
               
                   @Override
                   public void onCreate() {
                       super.onCreate();
                   }
               
               }
            """.trimIndent()
    )
    gradleTestRunner.setAppClassName(".MyApp")
}

private fun createDaggerModules(gradleTestRunner: GradleTestRunner) {
    gradleTestRunner.addSrc(
        srcPath = "minimal/AppModule.java",
        srcContent = """
            package minimal;
            
            import android.app.Application;
            import android.content.Context;
            import dagger.Binds;
            import dagger.Module;
            import dagger.hilt.InstallIn;
            import dagger.hilt.components.SingletonComponent;
            import javax.inject.Singleton;
            
            @InstallIn(SingletonComponent.class)
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
            import dagger.hilt.InstallIn;
            import dagger.hilt.android.components.ActivityComponent;
            import minimal.HomeDependency;

            @InstallIn(ActivityComponent.class)
            @Module
            class HomeModule {

                @Provides
                HomeDependency providesHomeDependency() {
                    return new HomeDependency();
                }

            }
        """.trimIndent()
    )
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
                import dagger.hilt.android.AndroidEntryPoint;

                   @AndroidEntryPoint
                   public class HomeActivity extends AppCompatActivity {
           
                       @Override
                       public void onCreate(Bundle savedInstanceState) {
                           super.onCreate(savedInstanceState);
                       }          
                }
            """.trimIndent()
    )
    gradleTestRunner.addActivities(
        "<activity android:name=\".HomeActivity\"/>"
    )
}