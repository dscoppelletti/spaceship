package it.scoppelletti.spaceship.preference.sample

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import it.scoppelletti.spaceship.inject.enableInject
import it.scoppelletti.spaceship.preference.sample.inject.DaggerAppComponent
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

    companion object {
        const val PROP_CREDITS = "it.scoppelletti.spaceship.preference.sample.credits"
        const val PROP_FEEDBACK = "it.scoppelletti.spaceship.preference.sample.feedback"
        const val PROP_HELP = "it.scoppelletti.spaceship.preference.sample.help"
    }
}
