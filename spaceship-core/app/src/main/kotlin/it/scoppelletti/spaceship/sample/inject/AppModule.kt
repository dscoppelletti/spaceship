
@file:Suppress("unused")

package it.scoppelletti.spaceship.sample.inject

import dagger.Binds
import dagger.Module
import it.scoppelletti.spaceship.sample.model.DefaultItemRepo
import it.scoppelletti.spaceship.sample.model.ItemRepo
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindItemRepo(repo: DefaultItemRepo): ItemRepo
}