package it.scoppelletti.spaceship.sample.inject

import dagger.Binds
import dagger.Module
import it.scoppelletti.spaceship.inject.CoreModule
import it.scoppelletti.spaceship.sample.model.DefaultItemRepo
import it.scoppelletti.spaceship.sample.model.ItemRepo
import javax.inject.Singleton

@Module(includes = [ CoreModule::class ])
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindItemRepo(repo: DefaultItemRepo): ItemRepo
}