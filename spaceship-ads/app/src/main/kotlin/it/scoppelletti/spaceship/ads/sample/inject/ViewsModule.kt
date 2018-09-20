package it.scoppelletti.spaceship.ads.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.ads.inject.AdsViewsModule
import it.scoppelletti.spaceship.ads.sample.AdConsentActivity

@Module(includes = [ AdsViewsModule::class ])
public abstract class ViewsModule {

    @ContributesAndroidInjector(modules = [])
    public abstract fun contributeAdConsentActivity(): AdConsentActivity
}