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

package it.scoppelletti.spaceship.app;

import java.util.Locale;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Title adapter.
 *
 * <p>Using a {@code TitleAdapter} object you can set the title of an activity
 * by the only one method {@link #setTitle} even if you set a custom
 * {@code Toolbar} as the action bar or if you use a
 * {@code CollapsingToolbarLayout} widget.</p>
 *
 * @see   android.support.design.widget.CollapsingToolbarLayout
 * @see   android.support.v7.widget.Toolbar
 * @since 1.0.0
 */
public final class TitleAdapter {
    private final Activity myActivity;
    private final CollapsingToolbarLayout myToolbarLayout;
    private int myTitleId;

    /**
     * Constructor.
     *
     * @param builder The instance builder.
     */
    private TitleAdapter(TitleAdapter.Builder builder) {
        myActivity = builder.myActivity;

        if (builder.myToolbarLayoutId != View.NO_ID) {
            myToolbarLayout = (CollapsingToolbarLayout) myActivity.findViewById(
                    builder.myToolbarLayoutId);
            if (myToolbarLayout == null) {
                throw new NullPointerException(String.format(Locale.ENGLISH,
                        "No CollapsingToolbarLayout widget with identifier " +
                                "%1$d.", builder.myToolbarLayoutId));
            }
        } else {
            myToolbarLayout = null;
        }

        myTitleId = -1;
    }

    /**
     * Sets the title.
     *
     * @param value The value as a string resource ID.
     */
    public void setTitle(@StringRes int value) {
        ActionBar actionBar;
        AppCompatActivity activity;

        if (myToolbarLayout == null) {
            if (myActivity instanceof AppCompatActivity) {
                activity = (AppCompatActivity) myActivity;
                actionBar = activity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(value);
                }
            }
        } else {
            myToolbarLayout.setTitle(myActivity.getString(value));
        }

        myActivity.setTitle(value);
        myTitleId = value;
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * @param savedInstanceState State of the activity. May be {@code null}.
     * @see   android.app.Activity#onPostCreate(android.os.Bundle)
     */
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            myTitleId = -1;
        } else {
            myTitleId = savedInstanceState.getInt(AppExt.PROP_TITLE, -1);
        }

        if (myTitleId > 0) {
            setTitle(myTitleId);
        }
    }

    /**
     * Should be called by the namesake method of the activity.
     *
     * @param outState State of the activity.
     * @see   android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (outState == null) {
            throw new NullPointerException("Argument outState is null.");
        }

        if (myTitleId > 0) {
            outState.putInt(AppExt.PROP_TITLE, myTitleId);
        }
    }

    /**
     * Builds a {@code TitleAdapter} instance.
     *
     * @since 1.0.0
     */
    public static final class Builder {
        private final Activity myActivity;
        private int myToolbarLayoutId;

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
            myToolbarLayoutId = View.NO_ID;
        }

        /**
         * Sets the ID of the {@code CollapsingToolbarLayout} widget.
         *
         * @param  value The value.
         * @return       This object.
         * @see          android.support.design.widget.CollapsingToolbarLayout
         */
        @NonNull
        public TitleAdapter.Builder toolbarLayoutId(@IdRes int value) {
            myToolbarLayoutId = value;
            return this;
        }

        /**
         * Builds a new {@code TitleAdapter} instance.
         *
         * @return The new object.
         */
        @NonNull
        public TitleAdapter build() {
            return new TitleAdapter(this);
        }
    }
}
