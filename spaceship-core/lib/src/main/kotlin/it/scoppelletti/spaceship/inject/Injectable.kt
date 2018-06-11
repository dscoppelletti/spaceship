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
 * Original source: blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/di/Injectable.kt
 * Added public qualifier.
 * Modified KDoc.
 */

package it.scoppelletti.spaceship.inject

/**
 * Marks a component injectable.
 *
 * @see   it.scoppelletti.spaceship.inject.enableInject
 * @since 1.0.0
 */
public interface Injectable