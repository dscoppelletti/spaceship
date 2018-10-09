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
 * Original repository: http://github.com/googlesamples/android-architecture-components
 * Original file: GithubBrowserSample/app/src/main/java/com/android/example/github/di/AppInjector.kt
 * Commit: 724cc1bd6ed11171a0bbf4a3a29977fac053777e
 * Remove the injection of the sample application.
 * Refactor the AppInjector class as the enabledInject extension function.
 * Porting to androix namespace.
 */

package it.scoppelletti.spaceship.inject

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

/**
 * Enables the injection process for this application.
 *
 * The injection process is enabled for activities and fragments if they
 * implement the `Injectable` interface.
 *
 * @receiver Application.
 * @see      it.scoppelletti.spaceship.inject.Injectable
 * @since    1.0.0
 */
public fun Application.enableInject() {
    this.registerActivityLifecycleCallbacks(ActivityInjector)
}

private object ActivityInjector : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle?) {
        if (activity is Injectable) {
            AndroidInjection.inject(activity)
        }
        if (activity is HasSupportFragmentInjector &&
                activity is FragmentActivity) {
            activity.supportFragmentManager
                    .registerFragmentLifecycleCallbacks(FragmentInjector, true)
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}

private object FragmentInjector : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?) {
        if (f is Injectable) {
            AndroidSupportInjection.inject(f)
        }
    }
}
