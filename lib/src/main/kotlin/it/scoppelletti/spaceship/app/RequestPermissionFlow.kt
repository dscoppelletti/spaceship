/*
 * Copyright (C) 2019-2021 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.app

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
 * @since 1.0.0
 */
@UiThread
public class RequestPermissionFlow private constructor(
        private val host: RequestPermissionHost,
        private val permission: String,
        private val tag: String,
        private val message: MessageSpec,
        @StringRes private val titleId: Int,
        private val callback: ActivityResultCallback<Boolean>
) {

    @Suppress("JoinDeclarationAndAssignment")
    private val launcher: ActivityResultLauncher<String>

    init {
        launcher = host.createLauncher(callback)
        host.registerRequestPermissionRationaleResult(tag,
                ::onRequestPermissionRationaleResult)
    }

    /**
     * @constructor             Constructor.
     * @param       activity    The activity hosting the flow.
     * @param       permission  The permission requested (one of the costants
     *                          defined by the class `Manifest.permission`).
     * @param       tag         Fragment tag of the Alert dialog showing the
     *                          rationale.
     * @param       message     Rationale for requesting the permission.
     * @param       titleId     Title of the Alert dialog showing the rationale.
     */
    public constructor(
            activity: AppCompatActivity,
            permission: String,
            tag: String,
            message: MessageSpec,
            titleId: Int,
            callback: ActivityResultCallback<Boolean>
    ) : this(ActivityRequestPermissionHost(activity), permission, tag, message,
            titleId, callback)

    /**
     * @constructor             Constructor.
     * @param       fragment    The fragment hosting the flow.
     * @param       permission  The permission requested (one of the costants
     *                          defined by the class `Manifest.permission`).
     * @param       tag         Fragment tag of the Alert dialog showing the
     *                          rationale.
     * @param       message     Rationale for requesting the permission.
     * @param       titleId     Title of the Alert dialog showing the rationale.
     */
    public constructor(
            fragment: Fragment,
            permission: String,
            tag: String,
            message: MessageSpec,
            titleId: Int,
            callback: ActivityResultCallback<Boolean>
    ) : this(FragmentRequestPermissionHost(fragment), permission, tag, message,
            titleId, callback)

    /**
     * Starts the flow.
     */
    public fun start() {
        if (host.checkSelfPermission(permission)) {
            logger.debug("Permission $permission already granted.")
            callback.onActivityResult(true)
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

    private fun onRequestPermissionRationaleResult(result: Bundle) {
        val which = result.getInt(AlertDialogFragment.PROP_RESULT,
                DialogInterface.BUTTON_NEGATIVE)
        if (which == DialogInterface.BUTTON_POSITIVE) {
            requestPermission()
        } else {
            logger.debug("Permission $permission denied.")
            callback.onActivityResult(false)
        }
    }

    private fun requestPermission() {
        logger.debug("Prompting for permission $permission.")
        launcher.launch(permission)
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
     * Creates the launcher for the contract
     * `ActivityResultContract.RequestPermission`.
     */
    fun createLauncher(
            callback: ActivityResultCallback<Boolean>
    ): ActivityResultLauncher<String>

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
     * Sets the listener for the result of the UI with rationale for requesting a
     * permission.
     *
     * @param tag      Tag of the UI with rationale for requesting a permission.
     * @param listener Listener for the result of the UI.
     */
    fun registerRequestPermissionRationaleResult(tag: String,
        listener: (Bundle) -> Unit)

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

    override fun createLauncher(
            callback: ActivityResultCallback<Boolean>
    ): ActivityResultLauncher<String> = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(), callback)

    override fun checkSelfPermission(permission: String): Boolean =
            (ContextCompat.checkSelfPermission(activity, permission) ==
                    PackageManager.PERMISSION_GRANTED)

    override fun showRequestPermissionRationale(
            init: AlertDialogFragment.Builder.() -> Unit
    ) = AlertDialogFragment.Builder(activity, activity.supportFragmentManager)
            .apply(init)
            .show()

    override fun registerRequestPermissionRationaleResult(
            tag: String, listener: (Bundle) -> Unit) {
        activity.supportFragmentManager.setFragmentResultListener(tag,
                activity) { _, bundle ->
            listener(bundle)
        }
    }

    override fun shouldShowRequestPermissionRationale(
            permission: String
    ): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity,
            permission)
}

/**
 * Implementation of the `RequestPermissionHost` interface for
 * `Fragment` objects.
 */
private class FragmentRequestPermissionHost(
        private val fragment: Fragment
) : RequestPermissionHost {

    override fun createLauncher(
            callback: ActivityResultCallback<Boolean>
    ): ActivityResultLauncher<String> = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission(), callback)

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

    override fun registerRequestPermissionRationaleResult(
            tag: String, listener: (Bundle) -> Unit) {
        fragment.childFragmentManager.setFragmentResultListener(tag,
                fragment) { _, bundle ->
            listener(bundle)
        }
    }

    override fun shouldShowRequestPermissionRationale(
            permission: String
    ): Boolean = fragment.shouldShowRequestPermissionRationale(permission)
}

