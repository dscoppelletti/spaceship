package it.scoppelletti.spaceship.ads.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.ads.inject.AdsViewsModule
import it.scoppelletti.spaceship.ads.sample.AdConsentActivity
import it.scoppelletti.spaceship.ads.sample.MainActivity
import it.scoppelletti.spaceship.ads.sample.SettingsActivity
import it.scoppelletti.spaceship.ads.sample.SettingsFragment

@Module(includes = [ AdsViewsModule::class ])
public abstract class ViewsModule {

    @ContributesAndroidInjector(modules = [])
    public abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [])
    public abstract fun contributeAdConsentActivity(): AdConsentActivity

    @ContributesAndroidInjector(modules = [])
    public abstract fun contributeSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector(modules = [])
    public abstract fun contributeSettingsFragment(): SettingsFragment
}
