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
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Functions)
        }
    }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<AlatRepository> { AlatRepositoryImpl(get()) }
    single<TugasRepository> { TugasRepositoryImpl(get()) }
    single<DashboardRepository> { DashboardRepositoryImpl(get()) }
    single<ReportRepository> { ReportRepositoryImpl(get(), androidContext()) }
}
