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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.lifecycle

/**
 * Wrapper for data that represents an event that should be handled only once.
 *
 * > Be aware that using `SingleEvent` with `ViewModel` and `LiveData` does not
 * > comply with *state immutability* and *unidirectional data flow* principles
 * > of some design patterns.
 *
 * * [View actions (snackbar, activity navigation, ...) in ViewModel](http://github.com/googlesamples/android-architecture-components/issues/63)
 * * [LiveData with SnackBar, Navigation and other events (the SingleLiveEvent case)](http://medium.com/google-developers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150)
 * * [Reactive Apps with Model-View-Intent - Part 7 - Timing (SingleLiveEvent problem)](http://hannesdorfmann.com/android/mosby3-mvi-7)
 *
 * @since 1.0.0
 *
 * @property hasBeenHandled Indicates whether this event has been handled or
 *                          not.
 *
 * @constructor         Constructor.
 * @param       content Data.
 */
public class SingleEvent<out T>(private val content: T) {

    @Suppress("WeakerAccess")
    public var hasBeenHandled: Boolean = false
        private set

    /**
     * Returns the data and marks this event as handled.
     *
     * @return The data. If this event has already been handled, returns `null`.
     */
    public fun poll(): T? =
            if (hasBeenHandled) {
                null
            } else {
                hasBeenHandled = true
                content
            }

    /**
     * Returns the data even if this event has already been handled.
     *
     * @return The data.
     */
    public fun peek(): T = content
}