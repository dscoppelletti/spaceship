
@file:Suppress("unused")

package it.scoppelletti.spaceship.security.sample.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.security.sample.CipherFragment
import it.scoppelletti.spaceship.security.sample.KeyFragment
import it.scoppelletti.spaceship.security.sample.MainActivity
import it.scoppelletti.spaceship.security.sample.ProviderFragment

@Module
abstract class ViewsModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeKeyFragment(): KeyFragment

    @ContributesAndroidInjector
    abstract fun contributeCipherFragment(): CipherFragment

    @ContributesAndroidInjector
    abstract fun contributeProvideFragment(): ProviderFragment
}