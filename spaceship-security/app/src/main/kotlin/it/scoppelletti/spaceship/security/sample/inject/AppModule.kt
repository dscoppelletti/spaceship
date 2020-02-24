@file:Suppress("unused")

package it.scoppelletti.spaceship.security.sample.inject

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.scoppelletti.spaceship.html.inject.HtmlModule
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import it.scoppelletti.spaceship.security.inject.SecurityModule
import it.scoppelletti.spaceship.security.sample.lifecycle.CipherViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.CipherViewModelFactory
import it.scoppelletti.spaceship.security.sample.lifecycle.KeyViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.KeyViewModelFactory

@Module(includes = [ HtmlModule::class, SecurityModule::class,
    UIModule::class ])
abstract class AppModule {

    @Binds
    @IntoMap
    @ViewModelKey(CipherViewModel::class)
    abstract fun bindCipherViewModelFactory(
            obj: CipherViewModelFactory
    ): ViewModelProviderEx.Factory

    @Binds
    @IntoMap
    @ViewModelKey(KeyViewModel::class)
    abstract fun bindKeyViewModelFactory(
            obj: KeyViewModelFactory
    ): ViewModelProviderEx.Factory
}
