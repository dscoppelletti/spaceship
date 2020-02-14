@file:Suppress("unused")

package it.scoppelletti.spaceship.security.sample.inject

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.html.inject.HtmlModule
import it.scoppelletti.spaceship.inject.StdlibModule
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.security.inject.SecurityModule
import it.scoppelletti.spaceship.security.sample.lifecycle.CipherViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.KeyViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.ProviderViewModel

@Module(includes = [ HtmlModule::class, SecurityModule::class,
    StdlibModule::class, UIModule::class ])
abstract class AppModule {

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
