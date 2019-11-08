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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.app

import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.scoppelletti.spaceship.i18n.MessageSpec
import mu.KotlinLogging

/**
 * Flow for Request App Permissions.
 *
 * * [Request App Permissions](http://developer.android.com/training/permissions/requesting)
 *
 * @see   it.scoppelletti.spaceship.app.OnDialogResultListener
 * @see   it.scoppelletti.spaceship.app.OnRequestPermissionFlowResultListener
 * @since 1.0.0
 *
 * @constructor             Constructor.
 * @param       activity    The activity hosting the flow.
 * @param       permission  The permission requested (one of the costants
 *                          defined by the class `Manifest.permission`).
 * @param       requestCode Request code.
 * @param       tag         Fragment tag of the Alert dialog showing the
 *                          rationale.
 * @param       message     Rationale for requesting the permission.
 * @param       titleId     Title of the Alert dialog showing the rationale.
 */
@UiThread
public class RequestPermissionFlow(
        private val activity: AppCompatActivity,
        private val permission: String,
        private val requestCode: Int,
        private val tag: String,
        private val message: MessageSpec,
        @StringRes private val titleId: Int) {

    /**
     * Starts the flow.
     */
    public fun start() {
        if (ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED) {
            logger.debug("Permission $permission already granted.")
            onResult(true)
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permission)) {
            activity.showAlertDialog {
                tag {
                    tag
                }
                message {
                    message
                }
                titleId {
                    titleId
                }
                iconId {
                    android.R.drawable.ic_dialog_info
                }
            }
            return
        }

        prompt()
    }

    /**
     * Should be called by the like-named method of the activity.
     *
     * If it returns `true`, the calling method should skip further processing.
     *
     * @return Returns `true` if the event has been handled, `false` otherwise.
     */
    public fun onRequestPermissionsResult(
            requestCode: Int,
            @Suppress("UNUSED_PARAMETER") permissions: Array<out String>,
            grantResults: IntArray): Boolean {
        when (requestCode) {
            this.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    logger.debug("Permission $permission granted.")
                    onResult(true)
                } else {
                    logger.debug("Permission $permission denied.")
                    onResult(false)
                }

                return true
            }
        }

        return false
    }

    /**
     * Should be called by the like-named method of the interface
     * `OnDialogResultListener` implemented by the activity.
     *
     * If it returns `true`, the calling method should skip further processing.
     *
     * @return Returns `true` if the event has been handled, `false` otherwise.
     */
    public fun onDialogResult(tag: String, which: Int): Boolean {
        when (tag) {
            this.tag -> {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        prompt()
                    }
                    else -> {
                        logger.debug("Permission $permission denied.")
                        onResult(false)
                    }
                }

                return true
            }
        }

        return false
    }

    /**
     * Callback for the result.
     *
     * @param result Whether the permission has been granted or not
     */
    private fun onResult(result: Boolean) {
        if (activity is OnRequestPermissionFlowResultListener) {
            activity.onRequestPermissionFlowResult(requestCode, result)
        }
    }

    /**
     * Requests the permission.
     */
    private fun prompt() {
        logger.debug("Prompting for permission $permission.")
        ActivityCompat.requestPermissions(activity, arrayOf(permission),
                requestCode)
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
