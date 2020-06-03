package com.android.client.ninjacat

import DaggerAppComponent
import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.android.client.ninjacat.core.SharedPrefs
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/*
 * AppComponent is used to inject ApplicationController class.
 * This way a DispatchingAndroidInjector is injected which is
 * then returned when an injector for an activity is requested.
 * */
class AppController : MultiDexApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}

