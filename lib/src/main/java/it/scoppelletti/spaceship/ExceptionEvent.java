/*
 * Copyright (C) 2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Notifies an exception.
 *
 * <p>An activity can handle exception events if it is registered to the
 * EventBus library and implements a method like the following:</p>
 *
 * <blockquote><pre>
 * &#64;Subscribe
 * public void onExceptionEvent(&#64;NonNull ExceptionEvent event) &#123;
 *     new ExceptionDialogFragment.Builder(this)
 *         .exceptionEvent(event).show();
 * &#125;
 * </pre></blockquote>
 *
 * @see  <a href="http://greenrobot.org/eventbus" target="_top">EventBus: Events
 *       for Android</a>
 * @since 1.0.0
 */
public final class ExceptionEvent {
    private final Throwable myEx;
    private int myTitleId;
    private int myReqCode;

    /**
     * Constructor.
     *
     * @param ex The exception.
     */
    public ExceptionEvent(@NonNull Throwable ex) {
        if (ex == null) {
            throw new NullPointerException("Argument ex is null.");
        }

        myEx = ex;
        myTitleId = -1;
    }

    /**
     * Gets the exception.
     *
     * @return The object.
     */
    @NonNull
    public Throwable getThrowable() {
        return myEx;
    }

    /**
     * Gets the title.
     *
     * @return The value as a string resource ID. If undefined, returns
     *         {@code -1}.
     */
    public int getTitleId() {
        return myTitleId;
    }

    /**
     * Sets the title.
     *
     * @param  value The value as a string resource ID.
     * @return       This object.
     */
    @NonNull
    public ExceptionEvent title(@StringRes int value) {
        myTitleId = value;
        return this;
    }

    /**
     * Gets the request code.
     *
     * @return The value. May be {@code 0}.
     */
    public int getRequestCode() {
        return myReqCode;
    }

    /**
     * Sets the request code.
     *
     * @param  value The value.
     * @return       This object.
     */
    @NonNull
    public ExceptionEvent requestCode(int value) {
        myReqCode = value;
        return this;
    }
}
