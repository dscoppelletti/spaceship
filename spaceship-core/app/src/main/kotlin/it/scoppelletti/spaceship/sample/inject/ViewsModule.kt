
@file:Suppress("unused")

package it.scoppelletti.spaceship.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.sample.DrawerActivity
import it.scoppelletti.spaceship.sample.GoogleApiActivity
import it.scoppelletti.spaceship.sample.ItemTabFragment
import it.scoppelletti.spaceship.sample.ListFragment
import it.scoppelletti.spaceship.sample.StubFragment
import it.scoppelletti.spaceship.sample.TabbedActivity

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector
    abstract fun contributeDrawerActivity(): DrawerActivity

    @ContributesAndroidInjector
    abstract fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector
    abstract fun contributeTabbedActivity(): TabbedActivity

    @ContributesAndroidInjector
    abstract fun contributeItemTabFragment(): ItemTabFragment

    @ContributesAndroidInjector
    abstract fun contributeStubFragment(): StubFragment

    @ContributesAndroidInjector
    abstract fun contributeGoogleApiActivity(): GoogleApiActivity
}