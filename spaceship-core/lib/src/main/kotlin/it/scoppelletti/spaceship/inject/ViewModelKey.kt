/*
 * Copyright (C) 2018 The Android Open Source Project
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
 * Original repository: http://github.com/googlesamples/android-architecture-components
 * Original file: GithubBrowserSample/app/src/main/java/com/android/example/github/di/ViewModelKey.kt
 * Commit: 724cc1bd6ed11171a0bbf4a3a29977fac053777e
 * Add public qualifier.
 * Add KDoc.
 * Porting to androix namespace.
 */

package it.scoppelletti.spaceship.inject

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Used to associate a `ViewModel` class as a key with a `Provider<ViewModel>`
 * as a value in order to compose the map used by [InjectViewModelFactory].
 *
 * @since          1.0.0
 *
 * @property value The `ViewModel` class.
 *
 * @constructor Constructor.
 */
@MustBeDocumented
@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
public annotation class ViewModelKey(val value: KClass<out ViewModel>)

