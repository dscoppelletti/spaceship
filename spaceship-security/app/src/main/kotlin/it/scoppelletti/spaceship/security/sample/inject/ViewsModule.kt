package it.scoppelletti.spaceship.security.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.security.sample.MainActivity

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivity(): MainActivity
}