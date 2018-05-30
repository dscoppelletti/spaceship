package it.scoppelletti.spaceship.sample

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import it.scoppelletti.spaceship.inject.enableInject
import it.scoppelletti.spaceship.sample.inject.DaggerAppComponent
import javax.inject.Inject

class MainApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.create()
                .inject(this)
        enableInject()
    }

    override fun activityInjector(): AndroidInjector<Activity> =
            activityInjector

    companion object {
        const val PROP_ITEMID: String = "it.scoppelletti.spaceship.sample.1"
    }
}