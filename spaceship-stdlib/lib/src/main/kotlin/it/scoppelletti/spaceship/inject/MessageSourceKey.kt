/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.inject

import dagger.MapKey
import it.scoppelletti.spaceship.i18n.DefaultMessageSource
import it.scoppelletti.spaceship.i18n.MessageSourceHandler
import it.scoppelletti.spaceship.i18n.MessageSpec
import kotlin.reflect.KClass

/**
 * Used to associate a [MessageSpec] class as a key with a
 * [MessageSourceHandler] object as a value in order to compose the map used by
 * [DefaultMessageSource].
 *
 * @since 1.0.0
 *
 * @property value The `MessageSpec` class.
 */
@MapKey
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
public annotation class MessageSourceKey(val value: KClass<out MessageSpec>)