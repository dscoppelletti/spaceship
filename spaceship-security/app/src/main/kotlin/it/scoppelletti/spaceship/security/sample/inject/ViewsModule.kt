package it.scoppelletti.spaceship.security.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.security.sample.CipherFragment
import it.scoppelletti.spaceship.security.sample.KeyFragment
import it.scoppelletti.spaceship.security.sample.MainActivity
import it.scoppelletti.spaceship.security.sample.ProviderFragment

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeKeyFragment(): KeyFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeCipherFragment(): CipherFragment

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeProvideFragment(): ProviderFragment
}