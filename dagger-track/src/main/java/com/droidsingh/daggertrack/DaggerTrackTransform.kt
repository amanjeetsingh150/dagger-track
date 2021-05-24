package com.droidsingh.daggertrack

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform

internal class DaggerTrackTransform: Transform() {
    override fun getName(): String {
        TODO()
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        TODO()
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        TODO()
    }

    override fun isIncremental(): Boolean {
        TODO()
    }
}