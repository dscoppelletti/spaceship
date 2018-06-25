/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * - Dario Scoppelletti, 2018
 * Original repository: http://github.com/googlesamples/android-architecture-components/commit/724cc1bd6ed11171a0bbf4a3a29977fac053777e
 * Original source: blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/viewmodel/GithubViewModelFactory.kt
 * Renamed to InjectViewModelFactory.
 * Added public qualifier.
 * Added KDoc.
 */

package it.scoppelletti.spaceship.inject

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Implementation of the `ViewModelProvider.Factory` interface for dependency
 * injection in `ViewModel` objects.
 *
 * @since 1.0.0
 *
 * @constructor          Constructor.
 * @param       creators Map to associate a `ViewModel` class with a
 *                       `Provider<ViewModel>` dependency.
 */
@Singleton
public class InjectViewModelFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>,
                @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException(
                "unknown model class $modelClass")
        try {
            @Suppress("UNCHECKED_CAST")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
