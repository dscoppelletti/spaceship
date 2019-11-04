/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("RedundantVisibilityModifier")

package it.scoppelletti.spaceship.app

/**
 * Handles the result of a Request App Permissions.
 *
 * * [Request App Permissions](http://developer.android.com/training/permissions/requesting)
 *
 * @see   it.scoppelletti.spaceship.app.RequestPermissionFlow
 * @since 1.0.0
 */
public interface OnRequestPermissionFlowResultListener {

    /**
     * This method will be invoked when a request for permissions is granted or
     * denied.
     *
     * @param requestCode Request code.
     * @param result      Whether the permission has been granted or not.
     */
    fun onRequestPermissionFlowResult(requestCode: Int, result: Boolean)
}
