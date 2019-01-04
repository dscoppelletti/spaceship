package it.scoppelletti.spaceship.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.inject.CoreViewsModule
import it.scoppelletti.spaceship.sample.DrawerActivity
import it.scoppelletti.spaceship.sample.ItemTabFragment
import it.scoppelletti.spaceship.sample.ListFragment
import it.scoppelletti.spaceship.sample.StubFragment
import it.scoppelletti.spaceship.sample.TabbedActivity

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeDrawerActivity(): DrawerActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeListFragment(): ListFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeTabbedActivity(): TabbedActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeItemTabFragment(): ItemTabFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeStubFragment(): StubFragment
}