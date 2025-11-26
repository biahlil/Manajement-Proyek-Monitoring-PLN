package com.pln.monitoringpln.app

import android.app.Application
import com.pln.monitoringpln.di.appModule
import com.pln.monitoringpln.di.dataModule
import com.pln.monitoringpln.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(appModule, dataModule, domainModule)
        }
    }
}
