package com.pln.monitoringpln.di

import com.pln.monitoringpln.BuildConfig
import com.pln.monitoringpln.data.repository.AlatRepositoryImpl
import com.pln.monitoringpln.data.repository.AuthRepositoryImpl
import com.pln.monitoringpln.data.repository.DashboardRepositoryImpl
import com.pln.monitoringpln.data.repository.ReportRepositoryImpl
import com.pln.monitoringpln.data.repository.TugasRepositoryImpl
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.AuthRepository
import com.pln.monitoringpln.domain.repository.DashboardRepository
import com.pln.monitoringpln.domain.repository.ReportRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.functions.Functions
import com.pln.monitoringpln.data.local.datasource.AlatLocalDataSource
import com.pln.monitoringpln.data.remote.AlatRemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth) {
                sessionManager = com.pln.monitoringpln.data.local.AndroidSessionManager(androidContext())
            }
            install(Postgrest)
            install(Storage)
            install(Functions)
        }
    }

    // Database
    single {
        androidx.room.Room.databaseBuilder(
            androidContext(),
            com.pln.monitoringpln.data.local.AppDatabase::class.java,
            "monitoring_pln.db"
        ).build()
    }

    // DAOs
    single { get<com.pln.monitoringpln.data.local.AppDatabase>().alatDao() }
    single { get<com.pln.monitoringpln.data.local.AppDatabase>().tugasDao() }

    // Data Sources
    single { AlatLocalDataSource(get()) }
    single { AlatRemoteDataSource(get()) }
    single { com.pln.monitoringpln.data.local.datasource.TugasLocalDataSource(get()) }
    single { com.pln.monitoringpln.data.remote.TugasRemoteDataSource(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<AlatRepository> { AlatRepositoryImpl(get(), get()) }
    single<TugasRepository> { TugasRepositoryImpl(get(), get()) }
    single<DashboardRepository> { DashboardRepositoryImpl(get(), get()) }
    single<ReportRepository> { ReportRepositoryImpl(get(), androidContext()) }
}
