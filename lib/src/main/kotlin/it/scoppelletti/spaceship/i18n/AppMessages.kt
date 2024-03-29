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

package it.scoppelletti.spaceship.i18n

/**
 * String resources.
 *
 * @since 1.0.0
 */
public interface AppMessages {

    public fun errorClipboardNotSupported(): MessageSpec

    public fun errorDateFormat(pattern: String): MessageSpec

    public fun errorPackageNotFound(name: String): MessageSpec

    public fun errorStartActivity(): MessageSpec

    public fun errorTimeFormat(pattern: String): MessageSpec

    public fun promptSaveChanges(): MessageSpec
}
