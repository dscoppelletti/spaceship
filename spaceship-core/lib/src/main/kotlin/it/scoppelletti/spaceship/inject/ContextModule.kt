/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.scoppelletti.spaceship.inject

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import dagger.Module
import dagger.Provides

/**
 * Defines the dependency provided by the Android platform.
 *
 * @since 1.0.0
 */
@Module
public object ContextModule {

    @Provides
    @JvmStatic
    public fun provideContext(application: Application): Context =
            application.applicationContext

    @Provides
    @JvmStatic
    public fun provideResources(application: Application): Resources =
            application.resources

    @Provides
    @JvmStatic
    public fun providePackageManager(application: Application): PackageManager =
            application.packageManager

    @Provides
    @JvmStatic
    public fun provideContentResolver(
            application: Application
    ): ContentResolver = application.contentResolver
}
