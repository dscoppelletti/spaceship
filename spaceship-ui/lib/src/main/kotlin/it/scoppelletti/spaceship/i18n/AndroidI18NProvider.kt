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

package it.scoppelletti.spaceship.i18n

import android.content.Context
import android.content.res.Resources
import it.scoppelletti.spaceship.types.AndroidDateConverter
import it.scoppelletti.spaceship.types.AndroidTimeConverter
import it.scoppelletti.spaceship.types.DateConverter
import it.scoppelletti.spaceship.types.TimeConverter
import mu.KotlinLogging
import org.threeten.bp.ZoneId
import java.util.IllegalFormatException
import java.util.Locale
import javax.inject.Inject

/**
 * Implementation of `I18NProvider` interface` for Android.
 *
 * @since 1.0.0
 */
public class AndroidI18NProvider @Inject constructor(
        private val context: Context,
        private val resources: Resources
): I18NProvider {

    override fun currentLocale(): Locale = Locale.getDefault()

    override fun currentZoneId(): ZoneId = ZoneId.systemDefault()

    override fun dateConverter(): DateConverter =
            AndroidDateConverter(context, resources, this)

    override fun timeConverter(secs: Boolean): TimeConverter =
            AndroidTimeConverter(secs, context, resources, this)

    override fun resolveMessage(obj: MessageSpec): String {
        if (obj !is AndroidResourceMessageSpec) {
            return obj.toString()
        }

        if (obj.args.isEmpty()) {
            return try {
                resources.getString(obj.stringId)
            } catch (ex: Resources.NotFoundException) {
                logger.error(ex) { "Resource $obj not found." }
                obj.toString()
            }
        }

        return try {
                try {
                    resources.getString(obj.stringId, *obj.args)
                } catch (ex: Resources.NotFoundException) {
                    logger.error(ex) { "Resource $obj not found." }
                    obj.toString()
                }
        } catch (ex: IllegalFormatException) {
            logger.error(ex) { "Resource $obj cannot be formatted." }
            obj.toString()
        }
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}


