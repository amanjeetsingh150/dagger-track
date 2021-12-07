package me.amanjeet.daggertrack.instrumentation

import me.amanjeet.daggertrack.instrumentation.di.AppComponent
import me.amanjeet.daggertrack.instrumentation.di.DaggerTestAppComponent

class DaggerTrackTestApp: DaggerTrackApp() {

    override fun getAppComponent(): AppComponent {
        return DaggerTestAppComponent.builder()
            .build()
    }
}