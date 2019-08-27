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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.i18n

import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.types.joinLines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.lang.IllegalArgumentException
import java.text.MessageFormat
import java.util.Locale
import java.util.MissingResourceException
import java.util.PropertyResourceBundle
import java.util.ResourceBundle
import javax.inject.Inject

private const val EXT_PROPERTIES = "properties"

/**
 * Implementation of `MessageSourceHandler` interface based on `ResourceBundle`
 * objects.
 *
 * @since 1.0.0
 */
public class ResourceBundleMessageSource @Inject constructor(
        private val i18nProvider: I18NProvider
) : MessageSourceHandler<ResourceBundleMessageSpec> {

    override suspend fun getMessage(obj: ResourceBundleMessageSpec): String =
            withContext(Dispatchers.IO) {
                doGetMessage(obj)
            }

    private fun doGetMessage(obj: ResourceBundleMessageSpec): String {
        val msg: String
        val pattern: String
        val bundle: ResourceBundle
        val fmt: MessageFormat
        val locale: Locale

        locale = i18nProvider.currentLocale()

        try {
            bundle = ResourceBundle.getBundle(obj.clazz.name, locale,
                    obj.clazz.classLoader, Utf8PropertiesResourceBundleControl)

            pattern = bundle.getString(obj.key)
        } catch (ex: MissingResourceException) {
            logger.error(ex) { "Resource $obj not found." }
            return obj.toString()
        }

        if (obj.args.isEmpty()) {
            return pattern
        }

        try {
            fmt = MessageFormat(pattern, locale)
        } catch (ex: IllegalArgumentException) {
            logger.error(ex) { """Invalid pattern \"$pattern\" for
                |resource $obj""".trimMargin().joinLines() }
            return obj.toString()
        }

        try {
            msg = fmt.format(obj.args)
        } catch (ex: IllegalArgumentException) {
            logger.error(ex) { """Resource $obj cannot be formatted by
                |pattern \"$pattern\".""".trimMargin().joinLines() }
            return obj.toString()
        }

        return msg
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}

private object Utf8PropertiesResourceBundleControl : ResourceBundle.Control() {

    override fun getFormats(baseName: String?): MutableList<String> =
            ResourceBundle.Control.FORMAT_PROPERTIES

    @Throws(IllegalAccessException::class, InstantiationException::class,
            IOException::class)
    override fun newBundle(
            baseName: String?,
            locale: Locale?,
            format: String?,
            loader: ClassLoader?,
            reload: Boolean
    ): ResourceBundle? {
        val bundleName: String
        val resourceName: String
        val stream: InputStream?
        val reader: Reader

        if (baseName == null) {
            throw NullPointerException("Argument baseName is null.")
        }
        if (locale == null) {
            throw NullPointerException("Argument locale is null.")
        }
        if (format == null) {
            throw NullPointerException("Argument format is null.")
        }
        if (loader == null) {
            throw NullPointerException("Argument loader is null")
        }

        bundleName = toBundleName(baseName, locale)
        resourceName = toResourceName(bundleName, EXT_PROPERTIES)

        stream = if (reload) {
            loader.getResource(resourceName)?.openConnection()?.let {
                it.useCaches = false
                it.getInputStream()
            }
        } else {
            loader.getResourceAsStream(resourceName)
        }

        if (stream == null) {
            return null
        }

        reader = stream.reader()
        return try {
            PropertyResourceBundle(reader)
        } finally {
            reader.closeQuietly()
        }
    }
}
