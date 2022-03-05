package me.amanjeet.daggertrack

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import me.amanjeet.daggertrack.transform.DaggerTrackTransform
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class DaggerTrackPlugin : Plugin<Project>{
    override fun apply(project: Project) {
        val isAndroid = project.plugins.hasPlugin(AppPlugin::class.java)
                || project.plugins.hasPlugin(LibraryPlugin::class.java)
        if (!isAndroid) {
            throw error("'com.android.application' or 'com.android.library' plugin required.")
        }

        val daggerTrackExtension = project.extensions.create("daggerTrack", DaggerTrackExtension::class.java)
        val android = project.extensions.findByName("android") as BaseExtension
        android.registerTransform(DaggerTrackTransform(project, android, daggerTrackExtension))
    }

    open class DaggerTrackExtension {
        var applyFor: Array<String> = emptyArray()
    }
}