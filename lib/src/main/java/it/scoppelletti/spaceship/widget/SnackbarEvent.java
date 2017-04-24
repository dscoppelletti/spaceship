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

package it.scoppelletti.spaceship.widget;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Event to show as a {@code Snackbar} widget.
 *
 * @since 1.0.0
 */
public final class SnackbarEvent {
    private final int myMsgId;
    private final int myDuration;
    private List<Object> myArgs;

    /**
     * Constructor.
     *
     * @param msgId    The message as a string resource ID.
     * @param duration The duration.
     */
    public SnackbarEvent(@StringRes int msgId,
            @SnackbarEvent.Duration int duration) {
        myMsgId = msgId;
        myDuration = duration;
    }

    /**
     * Sets the message arguments.
     *
     * @param  v The array of arguments.
     * @return   This object.
     */
    @NonNull
    public SnackbarEvent messageArguments(Object... v) {
        myArgs = (v == null) ? null : Arrays.asList(v);
        return this;
    }

    /**
     * Adds a message argument.
     *
     * @param  obj The object. May be {@code null}.
     * @return     This object.
     */
    @NonNull
    public SnackbarEvent addMessageArgument(@Nullable Object obj) {
        if (myArgs == null) {
            myArgs = new ArrayList<>();
        }

        myArgs.add(obj);
        return this;
    }

    /**
     * Shows this event.
     *
     * <p>An activity can handle {@code SnackbarEvent} events if it is
     * registered to the EventBus library and implements a method like the
     * following:</p>
     *
     * <blockquote><pre>
     * &#64;Subscribe
     * public void onSnackbarEvent(&#64;NonNull SnackbarEvent event) &#123;
     *     event.show(view);
     * &#125;
     * </pre></blockquote>
     *
     * @param view The view to find a parent from.
     * @see        <a href="http://greenrobot.org/eventbus"
     *             target="_top">EventBus: Events for Android</a>
     */
    public void show(@NonNull View view) {
        String msg;
        Snackbar widget;

        if (view == null) {
            throw new NullPointerException("Argument view is null.");
        }

        if (myArgs == null) {
            widget = Snackbar.make(view, myMsgId, myDuration);
        } else {
            msg = view.getContext().getString(myMsgId, myArgs.toArray());
            widget = Snackbar.make(view, msg, myDuration);
        }

        widget.show();
    }

    /**
     * Supported durations for a {@code Snackbar} widget.
     *
     * @since 1.0.0
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ Snackbar.LENGTH_INDEFINITE, Snackbar.LENGTH_LONG,
            Snackbar.LENGTH_SHORT })
    public @interface Duration {
    }
}
