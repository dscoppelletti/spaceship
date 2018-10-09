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

package it.scoppelletti.spaceship.html

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.text.Editable
import it.scoppelletti.spaceship.types.trimRaw
import mu.KLogger
import mu.KotlinLogging
import org.xml.sax.XMLReader
import javax.inject.Inject

/**
 * HTML custom tag for inserting the application name.
 *
 * @since 1.0.0
 *
 * @constructor                Constructor.
 * @param       context        Context.
 * @param       packageManager Package Manager.
 */
public class ApplicationLabelTagHandler @Inject constructor(
        private val context: Context,
        private val packageManager: PackageManager
) : HtmlTagHandler(ApplicationLabelTagHandler.TAG) {

    override fun handleTag(
            output: Editable,
            start: Int,
            end: Int,
            xmlReader: XMLReader
    ) {
        val applLabel: CharSequence
        val pkgName: String
        var applInfo: ApplicationInfo

        pkgName = context.packageName

        try {
            applInfo = packageManager.getApplicationInfo(pkgName, 0)
        } catch (ex: PackageManager.NameNotFoundException) {
            logger.error(ex) { """Failed to get ApplicationInfo for package
                |$pkgName.""".trimRaw() }

            applInfo = context.applicationInfo
        }

        applLabel = packageManager.getApplicationLabel(applInfo)
        output.replace(start, end, applLabel)
    }

    public companion object {

        /**
         * Tag.
         */
        public const val TAG: String = "it-scoppelletti-appLabel"

        private val logger: KLogger = KotlinLogging.logger {}
    }
}
