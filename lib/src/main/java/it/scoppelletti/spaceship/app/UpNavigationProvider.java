/*
 * Copyright (C) 2015 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import lombok.extern.slf4j.Slf4j;

/**
 * Up Navigation provider.
 *
 * @see   android.support.v7.app.AppCompatActivity#onSupportNavigateUp()
 * @see   <a href="http://developer.android.com/design/patterns/navigation.html"
 *        target="_top">Navigation with Back and Up</a>
 * @see   <a href="http://developer.android.com/training/implementing-navigation/ancestral.html"
 *        target="_top">Providing Up Navigation</a>
 * @since 1.0.0
 */
@Slf4j
@MainThread
public final class UpNavigationProvider {
    // The AppCompatActivity class already implements most of what the training
    // requires.

    private final AppCompatActivity myActivity;
    private final UpNavigationCallbacks myCallbacks;

    /**
     * Constructor.
     *
     * @param builder The instance builder.
     */
    private UpNavigationProvider(UpNavigationProvider.Builder builder) {
        myActivity = (AppCompatActivity) builder.myActivity;
        myCallbacks = builder.myCallbacks;
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * @param savedInstanceState State of the activity. May be {@code null}.
     * @see   android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ActionBar actionBar;

        actionBar = myActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * <p>The {@code onPrepareSupportNavigateUpTaskStack} method of the activity
     * is called when the activity was started in a task that does not belong to
     * the App that implements the activity. In such case, the App should start
     * a new task with the appropriate back stack before navigating up the
     * parent activity.<br />
     * The {@code onPrepareSupportNavigateUpTaskStack} method of the
     * {@code UpMavigationProvider} class copies all the extra parameters from
     * the {@code Intent} that started the activity to each {@code Intent} in
     * the new task stack.<br />
     * You can further customize each {@code Intent} in the new task stack
     * providing a {@code UpNavigationCallbacks} implementation.</p>
     *
     * @param builder A {@code TaskStackBuilder} object for the task stack that
     *                will be generated during Up Navigation from different
     *                task.
     * @see it.scoppelletti.spaceship.app.UpNavigationCallbacks#prepareIntent(android.content.Intent)
     * @see android.support.v7.app.AppCompatActivity#onPrepareNavigateUpTaskStack(android.app.TaskStackBuilder)
     */
    public void onPrepareSupportNavigateUpTaskStack(
            @NonNull TaskStackBuilder builder) {
        int i, n;
        Intent intent;

        if (builder == null) {
            throw new NullPointerException("Argument builder is null.");
        }

        n = builder.getIntentCount();
        for (i = 0; i < n; i++) {
            intent = builder.editIntentAt(i);
            intent.putExtras(myActivity.getIntent());

            if (myCallbacks != null) {
                myCallbacks.prepareIntent(intent);
            }
        }
    }

    /**
     * Should be called by the namesake method of the activity just before the
     * default implementation.
     *
     * <p>The {@code supportNavigateUpTo} method of the activity is called when
     * the App that implements the activity is the owner of the current
     * task.<br />
     * The {@code supportNavigateUpTo} method of the
     * {@code UpMavigationProvider} class copies all the extra parameters from
     * the {@code Intent} that started the activity to the {@code upIntent} for
     * resuming the parent activity.<br />
     * You can further customize the {@code upIntent} providing a
     * {@code UpNavigationCallbacks} implementation.</p>
     *
     * <ul>
     * <li>If the parent activity has launch mode {@code singleTop}, or the
     * {@code upIntent} contains {@code FLAG_ACTIVITY_CLEAR_TOP}, the parent
     * activity is brought to the top of the stack, and receives
     * {@code upIntent} through its {@code onNewIntent} method.</li>
     * <li>If the parent activity has launch mode {@code standard}, and the
     * {@code upIntent} does not contain {@code FLAG_ACTIVITY_CLEAR_TOP}, the
     * parent activity is popped off the stack, and a new instance of that
     * activity is created on top of the stack to receive the
     * {@code upIntent}.</li>
     * </ul>
     *
     * <p>In order to uniform how the parent activity is resumed, you should
     * simply override its {@code onNewIntent} method as follows:</p>
     *
     * <blockquote><pre>
     * &#64;Override
     * protected void onNewIntent(Intent intent) &#123;
     *     super.onNewIntent(intent);
     *     setIntent(intent);
     * &#125;
     * </pre></blockquote>
     *
     * @param upIntent An intent representing the target destination for up
     *                 navigation.
     * @see it.scoppelletti.spaceship.app.UpNavigationCallbacks#prepareIntent(android.content.Intent)
     * @see android.support.v7.app.AppCompatActivity#onNewIntent(android.content.Intent)
     * @see android.support.v7.app.AppCompatActivity#supportNavigateUpTo(android.content.Intent)
     */
    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        if (upIntent ==  null) {
            throw new NullPointerException("Argument upIntent is null.");
        }

        upIntent.putExtras(myActivity.getIntent());
        if (myCallbacks != null) {
            myCallbacks.prepareIntent(upIntent);
        }
    }

    /**
     * Finishes the activity and navigates up to the parent activity.
     */
    public void navigateUp() {
        if (myActivity.isFinishing()) {
            return;
        }

        if (!myActivity.onSupportNavigateUp()) {
            myLogger.error("Method onSupportNavigateUp failed.");
            myActivity.finish();
        }
    }

    /**
     * Builds a {@code UpNavigationProvider} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final Activity myActivity;
        private UpNavigationCallbacks myCallbacks;

        /**
         * Constructor.
         *
         * @param activity The activity.
         */
        public Builder(@NonNull Activity activity) {
            if (activity == null) {
                throw new NullPointerException("Argument activity is null.");
            }

            myActivity = activity;
        }

        /**
         * Sets the callbacks implementation.
         *
         * @param  obj The object. May be {@code null}.
         * @return     This object.
         */
        @NonNull
        public UpNavigationProvider.Builder callbacks(
                @Nullable UpNavigationCallbacks obj) {
            myCallbacks = obj;
            return this;
        }

        /**
         * Builds a new {@code UpNavigationProvider} instance.
         *
         * @return The new object.
         */
        @NonNull
        public UpNavigationProvider build() {
            return new UpNavigationProvider(this);
        }
    }
}
