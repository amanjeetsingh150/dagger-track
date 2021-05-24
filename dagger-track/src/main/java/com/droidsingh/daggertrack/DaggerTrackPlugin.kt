package com.droidsingh.daggertrack

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.plugins.LibraryPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class DaggerTrackPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        val isAndroid = target.plugins.hasPlugin(AppPlugin::class.java)
                || target.plugins.hasPlugin(LibraryPlugin::class.java)
        if (!isAndroid) {
            throw GradleException(
                "'com.android.application' or 'com.android.library' plugin required."
            )
        }
        val android = target.extensions.findByName("android") as BaseExtension
        android.registerTransform(DaggerTrackTransform())
    }
}