package it.scoppelletti.spaceship.html.sample.inject

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    @JvmStatic
    fun provideContext(application: Application): Context =
            application.applicationContext

    @Provides
    @Singleton
    @JvmStatic
    fun provideResources(application: Application): Resources =
            application.resources

    @Provides
    @Singleton
    @JvmStatic
    fun providePackageManager(application: Application): PackageManager =
            application.packageManager
}
