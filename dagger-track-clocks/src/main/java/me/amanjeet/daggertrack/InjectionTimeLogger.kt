package me.amanjeet.daggertrack

import android.util.Log

internal class InjectionTimeLogger : Logger {

    override fun d(message: String) {
        Log.d("DaggerTrack", message)
    }
}