package me.amanjeet.daggertrack.logger

internal interface DaggertrackLogger {
    fun onInjectionStart()
    fun onInjectionEnd(injectParam: String)
}