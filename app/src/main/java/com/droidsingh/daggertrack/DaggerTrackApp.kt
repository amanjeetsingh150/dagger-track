package com.droidsingh.daggertrack

import android.app.Application
import com.droidsingh.daggertrack.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DaggerTrackApp : Application(), HasAndroidInjector {

    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent
            .builder()
            .bindApplication(this)
            .build()
            .inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}