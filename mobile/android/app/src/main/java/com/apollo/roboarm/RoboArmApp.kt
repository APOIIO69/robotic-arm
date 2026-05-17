package com.apollo.roboarm

import android.app.Application
import com.apollo.roboarm.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RoboArmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RoboArmApp)
            modules(appModule)
        }
    }
}
