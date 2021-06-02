package com.droidsingh.daggertrack.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.droidsingh.daggertrack.R
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class HomeActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    internal lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment, HomeFragment())
            .commit()
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}