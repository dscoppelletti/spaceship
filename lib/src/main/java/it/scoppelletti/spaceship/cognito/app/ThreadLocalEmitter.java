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
package it.scoppelletti.spaceship.cognito.app;

import android.support.annotation.NonNull;
import io.reactivex.Emitter;

/**
 * {@code Emitter} interface used by the process running in the current thread.
 */
final class ThreadLocalEmitter extends ThreadLocal<Emitter<Object>> {
    private static final ThreadLocalEmitter myInstance =
            new ThreadLocalEmitter();

    /**
     * Private constructor for singleton class.
     */
    private ThreadLocalEmitter() {
    }

    /**
     * Gets the instance.
     *
     * @return The object.
     */
    @NonNull
    static ThreadLocalEmitter getInstance() {
        return myInstance;
    }

    @Override
    public Emitter<Object> get() {
        try {
            return super.get();
        } finally {
            remove();
        }
    }

    @Override
    protected Emitter<Object> initialValue() {
        throw new NullPointerException("Emitter interface not set.");
    }
}
