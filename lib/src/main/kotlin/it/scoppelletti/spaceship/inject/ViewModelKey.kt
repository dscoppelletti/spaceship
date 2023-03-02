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

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass
import it.scoppelletti.spaceship.lifecycle.DefaultViewModelProviderEx
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx

/**
 * Used to associate a `ViewModel` class as a key with a
 * [ViewModelProviderEx.Factory] dependency as a value in order to
 * compose the map used by the [DefaultViewModelProviderEx] dependency.
 *
 * @since 1.0.0
 *
 * @property value The `ViewModel` class.
 */
@MapKey
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER)
public annotation class ViewModelKey(val value: KClass<out ViewModel>)
