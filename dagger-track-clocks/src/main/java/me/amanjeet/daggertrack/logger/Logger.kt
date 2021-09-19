package me.amanjeet.daggertrack.logger

internal interface Logger {
    fun onInjectionStart()
    fun onInjectionEnd(injectParam: String)
}