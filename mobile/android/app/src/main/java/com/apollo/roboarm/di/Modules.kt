package com.apollo.roboarm.di

import com.apollo.roboarm.data.network.createKtorClient
import com.apollo.roboarm.data.repository.RoboArmRepository
import com.apollo.roboarm.ui.screens.home.HomeViewModel
import com.apollo.roboarm.ui.screens.details.RobotDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { createKtorClient() }
    single { RoboArmRepository(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { RobotDetailsViewModel(get()) }
}
