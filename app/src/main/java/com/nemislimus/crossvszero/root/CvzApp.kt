package com.nemislimus.crossvszero.root

import android.app.Application
import com.nemislimus.crossvszero.di.domainModule
import com.nemislimus.crossvszero.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CvzApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CvzApp)
            modules(domainModule, presentationModule)
        }
    }
}