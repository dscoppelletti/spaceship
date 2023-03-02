/*
 * Copyright (C) 2013-2023 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Application model extensions.
 *
 * @since 1.0.0
 */
public object AppExt {

    /**
     * Tag of `AlertDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.AlertDialogFragment
     */
    public const val TAG_ALERTDIALOG: String = "it.scoppelletti.spaceship.1"

    /**
     * Tag of `ExceptionDialogFragment` fragment.
     *
     * @see it.scoppelletti.spaceship.app.ExceptionDialogFragment
     */
    public const val TAG_EXCEPTIONDIALOG: String = "it.scoppelletti.spaceship.2"

    /**
     * Tag of `BottomSheetDialogFragmentEx` fragment.
     *
     * @see it.scoppelletti.spaceship.app.BottomSheetDialogFragmentEx
     */
    public const val TAG_BOTTOMSHEETDIALOG: String =
        "it.scoppelletti.spaceship.3"

    /**
     * Property containing an item.
     */
    public const val PROP_ITEM: String = "it.scoppelletti.spaceship.1"

    /**
     * Property containing a message.
     */
    public const val PROP_MESSAGE: String = "it.scoppelletti.spaceship.2"

    /**
     * Property containing a result.
     */
    public const val PROP_RESULT: String = "it.scoppelletti.spaceship.3"

    /**
     * Returns information about an application.
     *
     * @param  packageManager PackageManager instance.
     * @param  packageName    Package name.
     * @param  flags          Info combination to retrieve.
     * @return                Application info.
     * @since                 1.1.0
     */
    @Throws(PackageManager.NameNotFoundException::class)
    public fun getApplicationInfo(
        packageManager: PackageManager,
        packageName: String,
        flags: Int
    ): ApplicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getApplicationInfoTiramisu(packageManager, packageName, flags)
    else
        getApplicationInfoDeprecated(packageManager, packageName, flags)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getApplicationInfoTiramisu(
        packageManager: PackageManager,
        packageName: String,
        flags: Int
    ): ApplicationInfo = packageManager.getApplicationInfo(packageName,
        PackageManager.ApplicationInfoFlags.of(flags.toLong()))

    @Suppress("deprecation")
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getApplicationInfoDeprecated(
        packageManager: PackageManager,
        packageName: String,
        flags: Int
    ): ApplicationInfo = packageManager.getApplicationInfo(packageName, flags)

    /**
     * Returns information about a package.
     *
     * @param  packageManager PackageManager instance.
     * @param  packageName    Package name.
     * @param  flags          Info combination to retrieve.
     * @return                Package info.
     * @since                 1.1.0
     */
    @Throws(PackageManager.NameNotFoundException::class)
    public fun getPackageInfo(
        packageManager: PackageManager,
        packageName: String,
        flags: Int
    ): PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getPackageInfoTiramisu(packageManager, packageName, flags)
    else
        getPackageInfoDeprecated(packageManager, packageName, flags)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getPackageInfoTiramisu(
        packageManager: PackageManager,
        packageName: String,
        flags: Int
    ): PackageInfo = packageManager.getPackageInfo(packageName,
        PackageManager.PackageInfoFlags.of(flags.toLong()))

    @Suppress("deprecation")
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getPackageInfoDeprecated(
        packageManager: PackageManager,
        packageName: String,
        flags: Int
    ): PackageInfo = packageManager.getPackageInfo(packageName, flags)

    /**
     * Returns the version of a package.
     *
     * @param  packageInfo Package info.
     * @return             Version.
     * @since              1.1.0
     */
    public fun getVersion(packageInfo: PackageInfo): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            packageInfo.longVersionCode else getVersionDeprecated(packageInfo)

    @Suppress("deprecation")
    private fun getVersionDeprecated(packageInfo: PackageInfo): Long =
        packageInfo.versionCode.toLong()
}
