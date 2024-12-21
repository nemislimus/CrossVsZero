package com.nemislimus.crossvszero.di

import com.nemislimus.crossvszero.ui.viewmodels.GameFragmentViewModel
import com.nemislimus.crossvszero.ui.viewmodels.MainFragmentViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    ////////// ViewModels Block
    viewModel {
        MainFragmentViewModel()
    }

    viewModel {
        GameFragmentViewModel(get())
    }
}