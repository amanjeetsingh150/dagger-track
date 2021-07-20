package me.amanjeet.daggertrack

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import javassist.ClassPool
import me.amanjeet.daggertrack.utils.GradleTestRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

internal class ClassPoolTransformationsTest {

    @Rule
    @JvmField
    val testProjectDir: TemporaryFolder = TemporaryFolder
        .builder().assureDeletion().build()

    private val classPool = ClassPool.getDefault()

    private lateinit var gradleTestRunner: GradleTestRunner

    @Before
    fun setup() {
        gradleTestRunner = GradleTestRunner(testProjectDir)
        gradleTestRunner.addDependencies(
            "implementation 'androidx.appcompat:appcompat:1.1.0'"
        )
        setupApplicationClass()
        setupMainActivity()
    }

    @Test
    fun `it transforms class pool to the list of CtClass`() {
        // given
        val inputs = mock<TransformInput>()
        val classFilesDirectoryInput = mock<DirectoryInput>()
        val transformInvocation = mock<TransformInvocation>()
        val result = gradleTestRunner.build(shouldIntegrateDaggerTrack = false)
        val appClassFile = result.getClassFile("minimal/MyApp.class")
        val classFilesDirectory = appClassFile.parentFile.parentFile
        classPool.insertClassPath(classFilesDirectory.absolutePath)
        whenever(transformInvocation.inputs).thenReturn(arrayListOf(inputs))
        whenever(inputs.directoryInputs).thenReturn(arrayListOf(classFilesDirectoryInput))
        whenever(classFilesDirectoryInput.file).thenReturn(classFilesDirectory)

        // when
        val ctClassList = classPool.mapToCtClassList(transformInvocation)

        // then
        val classNameList = ctClassList.map { it.name }
        assertThat(classNameList).containsExactly(
            "minimal.MyApp",
            "minimal.MainActivity",
            "minimal.BuildConfig"
        )
    }

    private fun setupApplicationClass() {
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
    }

    private fun setupMainActivity() {
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
        gradleTestRunner.addActivities(
            "<activity android:name=\".MainActivity\"/>"
        )
    }
}