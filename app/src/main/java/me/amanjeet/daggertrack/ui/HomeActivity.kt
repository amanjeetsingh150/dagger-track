package me.amanjeet.daggertrack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.hilt.android.AndroidEntryPoint
import me.amanjeet.daggertrack.R
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportFragmentManager.beginTransaction()
            .replace(R.id.home_fragment, HomeFragment())
            .commit()
    }
}