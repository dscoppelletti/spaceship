
@file:Suppress("unused")

package it.scoppelletti.spaceship.preference.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.preference.sample.SettingsActivity
import it.scoppelletti.spaceship.preference.sample.SettingsFragment

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector
    abstract fun contributeSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment
}

