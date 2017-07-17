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

package it.scoppelletti.spaceship.app;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;

/**
 * Data retained by splash activity.
 *
 * @see   it.scoppelletti.spaceship.app.SplashActivityBase
 * @since 1.0.0
 */
public final class SplashActivityData extends Fragment {

    /**
     * The fragment tag.
     */
    public static final String TAG = AppExt.TAG_SPLASHACTIVITYDATA;

    private CompletableCoordinator mySplashCoordinator;

    /**
     * Sole constructor.
     */
    public SplashActivityData() {
        setRetainInstance(true);
    }

    /**
     * Gets the coordinator for the splash activity.
     *
     * @return The object.
     */
    @NonNull
    CompletableCoordinator getSplashCoordinator() {
        if (mySplashCoordinator == null) {
            mySplashCoordinator = new CompletableCoordinator();
        }

        return mySplashCoordinator;
    }

    @Override
    public void onDestroy() {
        if (mySplashCoordinator != null) {
            mySplashCoordinator.onDestroy();
            mySplashCoordinator = null;
        }

        super.onDestroy();
    }
}
