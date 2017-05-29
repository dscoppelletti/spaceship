/*
 * Copyright (C) 2013-2016 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import it.scoppelletti.spaceship.ApplicationException;

/**
 * Application model operations.
 *
 * @since 1.0.0
 */
public final class AppExt {

    /**
     * Property indicating whether the user has manually opened the drawer at
     * least once.
     */
    public static final String PROP_LEARNED = "it.scoppelletti.spaceship.1";

    /**
     * Property reporting the title of an activity as a string resource ID.
     */
    public static final String PROP_TITLE = "it.scoppelletti.spaceship.2";

    /**
     * Tag of the {@code ConfirmDialogFragment} fragment.
     *
     * @see it.scoppelletti.spaceship.app.ConfirmDialogFragment
     */
    public static final String TAG_CONFIRMDIALOG =
            "it.scoppelletti.spaceship.1";

    /**
     * Tag of the {@code ExceptionDialogFragment} fragment.
     *
     * @see it.scoppelletti.spaceship.app.ExceptionDialogFragment
     */
    public static final String TAG_EXCEPTIONDIALOG =
            "it.scoppelletti.spaceship.2";

    /**
     * Private constructor for static class.
     */
    private AppExt() {
    }

    /**
     * Gets or creates a fragment.
     *
     * @param  activity      The activity hosting the fragment.
     * @param  fragmentClass Class of the fragment.
     * @param  tag           Tag of the fragment.
     * @param  <T>           Class of the fragment.
     * @return               The fragment.
     */
    @NonNull
    public static <T extends Fragment> T getOrCreateFragment(
            @NonNull Activity activity, @NonNull Class<T> fragmentClass,
            @NonNull String tag) {
        T fragment;
        FragmentManager fragmentMgr;

        if (activity == null) {
            throw new NullPointerException("Argument activity is null.");
        }
        if (fragmentClass == null) {
            throw new NullPointerException("Argument fragmentClass is null.");
        }
        if (TextUtils.isEmpty(tag)) {
            throw new NullPointerException("Argument tag is null.");
        }

        fragmentMgr = ((FragmentActivity) activity).getSupportFragmentManager();
        fragment = fragmentClass.cast(fragmentMgr.findFragmentByTag(tag));
        if (fragment != null) {
            return fragment;
        }

        try {
            fragment = fragmentClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ApplicationException.toString(ex), ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException(ApplicationException.toString(ex), ex);
        }

        fragmentMgr.beginTransaction()
                .add(fragment, tag)
                .commit();

        return fragment;
    }

    /**
     * Hides the soft keyboard.
     *
     * @param activity The activity.
     */
    @UiThread
    public static void hideSoftKeyboard(@NonNull Activity activity) {
        View view;
        InputMethodManager inputMgr;

        if (activity == null) {
            throw new NullPointerException("Argument activity is null.");
        }

        view = activity.getCurrentFocus();
        if (view == null) {
            return;
        }

        inputMgr = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
