/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.rx;

import android.support.annotation.NonNull;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Creates a new observer instance.
 *
 * <p>To coordinate an observable source with the lifecycle of an activity, your
 * observer should be unsubscribed when the activity is paused and then
 * subscribed again when the activity is resumed; but when an observer is
 * disposed it cannot be used anymore, thus the {@code SingleCoordinator} class
 * makes use of an {@code SingleObserverFactory} interface in order to create a
 * new observer instance whenever it needs.</p>
 *
 * @param <T> Class of the items to observe.
 * @see       it.scoppelletti.spaceship.rx.ObservableCoordinator#subscribe(ObserverFactory)
 * @since     1.0.0
 */
public interface SingleObserverFactory<T> {

    /**
     * Creates a new observer instance.
     *
     * @return The new object.
     */
    @NonNull
    DisposableSingleObserver<T> create();
}
