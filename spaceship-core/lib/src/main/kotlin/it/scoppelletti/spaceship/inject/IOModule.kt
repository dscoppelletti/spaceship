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
import android.content.res.AssetManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import it.scoppelletti.spaceship.io.DefaultIOProvider
import it.scoppelletti.spaceship.io.IOProvider

/**
 * Defines the dependencies for I/O operations.
 *
 * @since 1.0.0
 */
@Module
public abstract class IOModule {

    @Binds
    public abstract fun bindIOProvider(obj: DefaultIOProvider): IOProvider

    @Module
    public companion object {

        @Provides
        @JvmStatic
        public fun provideAssetManager(application: Application): AssetManager =
                application.assets
    }
}