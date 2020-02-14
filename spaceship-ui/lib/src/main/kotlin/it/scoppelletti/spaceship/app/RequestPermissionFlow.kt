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
import androidx.fragment.app.Fragment
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
 */
@UiThread
public class RequestPermissionFlow private constructor(
        private val host: RequestPermissionHost,
        private val resultListener: OnRequestPermissionFlowResultListener?,
        private val permission: String,
        private val requestCode: Int,
        private val tag: String,
        private val message: MessageSpec,
        @StringRes private val titleId: Int) {

    /**
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
    public constructor(
            activity: AppCompatActivity,
            permission: String,
            requestCode: Int,
            tag: String,
            message: MessageSpec,
            titleId: Int
    ) : this(ActivityRequestPermissionHost(activity),
            activity as? OnRequestPermissionFlowResultListener,
            permission, requestCode, tag, message, titleId)

    /**
     * @constructor             Constructor.
     * @param       fragment    The fragment hosting the flow.
     * @param       permission  The permission requested (one of the costants
     *                          defined by the class `Manifest.permission`).
     * @param       requestCode Request code.
     * @param       tag         Fragment tag of the Alert dialog showing the
     *                          rationale.
     * @param       message     Rationale for requesting the permission.
     * @param       titleId     Title of the Alert dialog showing the rationale.
     */
    public constructor(
            fragment: Fragment,
            permission: String,
            requestCode: Int,
            tag: String,
            message: MessageSpec,
            titleId: Int
    ) : this(FragmentRequestPermissionHost(fragment),
            fragment as? OnRequestPermissionFlowResultListener,
            permission, requestCode, tag, message, titleId)

    /**
     * Starts the flow.
     */
    public fun start() {
        if (host.checkSelfPermission(permission)) {
            logger.debug("Permission $permission already granted.")
            onResult(true)
            return
        }

        if (host.shouldShowRequestPermissionRationale(permission)) {
            host.showRequestPermissionRationale {
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

        requestPermission()
    }

    /**
     * Should be called by the like-named method of the activity/fragment.
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
     * `OnDialogResultListener` implemented by the activity/fragment.
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
                        requestPermission()
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
        resultListener?.onRequestPermissionFlowResult(requestCode, result)
    }

    /**
     * Requests the permission.
     */
    private fun requestPermission() {
        logger.debug("Prompting for permission $permission.")
        host.requestPermission(permission, requestCode)
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}

/**
 * Interface for abstracting `Activity` and `Fragment`.
 */
private interface RequestPermissionHost {

    /**
     * Requests a permission to be granted to this application
     *
     * @param permission  The permission requested (one of the costants
     *                    defined by the class `Manifest.permission`).
     * @param requestCode Request code.
     */
    fun requestPermission(permission: String, requestCode: Int)

    /**
     * Determine whether you have been granted a permission.
     *
     * @return Returns `true` if you have the permission, `false` otherwise.
     */
    fun checkSelfPermission(permission: String): Boolean

    /**
     * Shows UI with rationale for requesting a permission.
     *
     * @param init Initialization block.
     */
    fun showRequestPermissionRationale(
            init: AlertDialogFragment.Builder.() -> Unit = { }
    )

    /**
     * Checks whether you should show UI with rationale for requesting a
     * permission.
     *
     * @param permission  The permission requested (one of the costants
     *                    defined by the class `Manifest.permission`).
     * @return            Return `true` if you can show UI with rationale for
     *                    requesting the permission, `false` otherwise.
     */
    fun shouldShowRequestPermissionRationale(permission: String): Boolean
}

/**
 * Implementation of the `RequestPermissionSupport` interface for
 * `Activity` objects.
 */
private class ActivityRequestPermissionHost(
        private val activity: AppCompatActivity
) : RequestPermissionHost {

    override fun requestPermission(permission: String, requestCode: Int) =
            ActivityCompat.requestPermissions(activity, arrayOf(permission),
                    requestCode)

    override fun checkSelfPermission(permission: String): Boolean =
            (ContextCompat.checkSelfPermission(activity, permission) ==
                    PackageManager.PERMISSION_GRANTED)

    override fun showRequestPermissionRationale(
            init: AlertDialogFragment.Builder.() -> Unit
    ) = AlertDialogFragment.Builder(activity, activity.supportFragmentManager)
            .apply(init)
            .show()

    override fun shouldShowRequestPermissionRationale(
            permission: String
    ): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity,
            permission)
}

/**
 * Implementation of the `RequestPermissionSupport` interface for
 * `Fragment` objects.
 */
private class FragmentRequestPermissionHost(
        private val fragment: Fragment
) : RequestPermissionHost {

    override fun requestPermission(permission: String, requestCode: Int) =
            fragment.requestPermissions(arrayOf(permission), requestCode)

    override fun checkSelfPermission(permission: String): Boolean =
            (ContextCompat.checkSelfPermission(fragment.requireContext(),
                    permission) == PackageManager.PERMISSION_GRANTED)

    override fun showRequestPermissionRationale(
            init: AlertDialogFragment.Builder.() -> Unit
    ) = AlertDialogFragment.Builder(
            fragment.requireActivity() as AppCompatActivity,
            fragment.childFragmentManager)
            .apply(init)
            .show()

    override fun shouldShowRequestPermissionRationale(
            permission: String
    ): Boolean = fragment.shouldShowRequestPermissionRationale(permission)
}
