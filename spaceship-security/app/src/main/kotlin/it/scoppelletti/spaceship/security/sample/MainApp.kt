package it.scoppelletti.spaceship.security.sample

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import it.scoppelletti.spaceship.inject.enableInject
import it.scoppelletti.spaceship.security.sample.inject.DaggerAppComponent
import javax.inject.Inject

class MainApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
        enableInject()
    }

    override fun activityInjector(): AndroidInjector<Activity> =
            activityInjector
}


