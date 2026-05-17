package com.apollo.roboarm.di

import com.apollo.roboarm.data.network.createKtorClient
import org.koin.dsl.module

val appModule = module {
    single { createKtorClient() }
}
