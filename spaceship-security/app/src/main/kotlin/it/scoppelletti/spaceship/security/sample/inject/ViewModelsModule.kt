package it.scoppelletti.spaceship.security.sample.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.inject.CoreViewModelsModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.security.sample.lifecycle.MainViewModel

@Module(includes = [ CoreViewModelsModule::class ])
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}