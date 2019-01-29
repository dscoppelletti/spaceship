package it.scoppelletti.spaceship.preference.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.preference.sample.SettingsActivity

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeSettingsActivity(): SettingsActivity
}

