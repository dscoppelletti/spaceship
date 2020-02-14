/*
 * Copyright (C) 2017 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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
        "RemoveRedundantQualifierName", "unused")

package it.scoppelletti.spaceship.http

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.i18n.I18NProvider
import it.scoppelletti.spaceship.i18n.UIMessages
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

private const val LANG_UND = "und"
private const val LANG_SEP = '-'
private const val LOCALE_SEP = '_'
private const val OS_NAME = "android"
private const val VALUE_SEP = ';'

/**
 * Decorates an HTTP request with infos describing the client.
 *
 * @since 1.0.0
 */
public class ClientInterceptor @Inject constructor(
        private val context: Context,
        private val packageMgr: PackageManager,
        private val i18nProvider: I18NProvider,
        private val uiMessages: UIMessages
) : Interceptor {

    private val osName: String
    private val applName: String

    init {
        osName = buildString {
            append(OS_NAME)
            append(VALUE_SEP)
            append(Build.VERSION.SDK_INT)
        }

        applName = initApplName()
    }

    /**
     * Init the application name and version.
     *
     * @return            The value.
     */
    @Suppress("Deprecation")
    private fun initApplName(): String {
        val name: String
        val packageInfo: PackageInfo

        name = context.packageName
        try {
            packageInfo = packageMgr.getPackageInfo(name, 0)
        } catch (ex: PackageManager.NameNotFoundException) {
            throw ApplicationException(uiMessages.errorPackageNotFound(name),
                    ex)
        }

        return buildString {
            append(name)
            append(VALUE_SEP)
            append(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                packageInfo.longVersionCode else packageInfo.versionCode)
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response =
            chain.proceed(chain.request().newBuilder()
                    .header(HttpExt.HEADER_OS, osName)
                    .header(HttpExt.HEADER_APPL, applName)
                    .header(HttpExt.HEADER_LOCALE, toLanguageTag(
                            i18nProvider.currentLocale()))
                    .build())
}

/**
 * Returns the language tag corresponding to a locale object.
 *
 * @param  locale The locale object.
 * @return        The language tag.
 */
private fun toLanguageTag(locale: Locale): String {
    var s: String?
    val buf: StringBuilder

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return locale.toLanguageTag()
    }

    // Simple alternative implementation that does not strictly comply
    // IEFT BCP 47
    buf = StringBuilder()

    s = locale.language
    buf.append(if (s.isNullOrBlank()) LANG_UND else s.toLowerCase(Locale.UK))

    s = locale.country
    if (!s.isNullOrBlank()) {
        buf.append(LANG_SEP)
                .append(s.toUpperCase(Locale.UK))
    }

    s = locale.variant
    if (!s.isNullOrBlank()) {
        s.split(LOCALE_SEP).forEach { variant ->
            buf.append(LANG_SEP)
                    .append(variant.toLowerCase(Locale.UK))
        }
    }

    return buf.toString()
}
