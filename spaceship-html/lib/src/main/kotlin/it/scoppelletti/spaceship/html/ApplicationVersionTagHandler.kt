/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
 * limit
 */

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.html

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.Editable
import mu.KotlinLogging
import org.xml.sax.XMLReader
import javax.inject.Inject

/**
 * HTML custom tag for inserting the application version.
 *
 * @since 1.0.0
 */
public class ApplicationVersionTagHandler @Inject constructor(
        private val context: Context,
        private val packageManager: PackageManager
) : HtmlTagHandler(ApplicationVersionTagHandler.TAG) {

    override fun handleTag(
            output: Editable,
            start: Int,
            end: Int,
            xmlReader: XMLReader
    ) {
        val version: CharSequence
        val pkgName: String
        val pkgInfo: PackageInfo

        pkgName = context.packageName

        try {
            pkgInfo = packageManager.getPackageInfo(pkgName, 0)
        } catch (ex: PackageManager.NameNotFoundException) {
            logger.error(ex) {
                "Failed to get PackageInfo for package $pkgName."
            }

            return
        }

        version = pkgInfo.versionName
        output.replace(start, end, version)
    }

    public companion object {

        /**
         * Tag.
         */
        public const val TAG = "it-scoppelletti-appVersion"

        private val logger = KotlinLogging.logger {}
    }
}
