package com.pln.monitoringpln.app

import android.app.Application
import com.pln.monitoringpln.di.appModule
import com.pln.monitoringpln.di.dataModule
import com.pln.monitoringpln.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(appModule, dataModule, domainModule))
        }

        setupWorkManager()
    }

    private fun setupWorkManager() {
        val workManager = androidx.work.WorkManager.getInstance(this)

        val syncRequest = androidx.work.OneTimeWorkRequestBuilder<com.pln.monitoringpln.data.worker.SyncWorker>()
            .setInitialDelay(5, java.util.concurrent.TimeUnit.MINUTES) // Initial delay or immediate? User wants every 5 mins.
            // Let's make it immediate for the first run, then it schedules next in 5 mins.
            // Actually, Login triggers immediate. This is for app start.
            // If we want it to run periodically even if not logged in? No, only if logged in.
            // But WorkManager persists.
            // Let's schedule it with 5 min delay to avoid conflict with Login trigger if they happen together.
            .setConstraints(
                androidx.work.Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build(),
            )
            .addTag("SyncWorker")
            .build()

        workManager.enqueueUniqueWork(
            "SyncWorker",
            androidx.work.ExistingWorkPolicy.KEEP, // Keep existing if running
            syncRequest,
        )
    }
}
