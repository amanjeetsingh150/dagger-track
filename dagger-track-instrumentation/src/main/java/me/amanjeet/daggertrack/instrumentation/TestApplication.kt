package me.amanjeet.daggertrack.instrumentation

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import me.amanjeet.daggertrack.instrumentation.di.DaggerAppComponent
import javax.inject.Inject

class TestApplication: Application(), HasAndroidInjector {

    @Inject
    lateinit var appDependency: AppDependency

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}