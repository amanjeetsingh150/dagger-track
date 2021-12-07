package me.amanjeet.daggertrack.instrumentation

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import me.amanjeet.daggertrack.instrumentation.di.AppComponent
import me.amanjeet.daggertrack.instrumentation.di.DaggerAppComponent
import javax.inject.Inject

open class DaggerTrackApp: Application(), HasAndroidInjector {

    @Inject
    lateinit var appDependency: AppDependency

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        getAppComponent().inject(this)
    }

    open fun getAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .bindApplication(this)
            .build()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}