
@file:Suppress("unused")

package it.scoppelletti.spaceship.security.sample.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.inject.CoreViewModelsModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.security.sample.lifecycle.CipherViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.KeyViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.MainViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.ProviderViewModel

@Module(includes = [ CoreViewModelsModule::class ])
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(KeyViewModel::class)
    abstract fun bindKeyViewModel(viewModel: KeyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CipherViewModel::class)
    abstract fun bindCipherViewModel(viewModel: CipherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProviderViewModel::class)
    abstract fun bindProviderViewModel(viewModel: ProviderViewModel): ViewModel
}