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
import it.scoppelletti.spaceship.applicationException
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

/**
 * Decorates an HTTP request with infos describing the client.
 *
 * @since 1.0.0
 *
 * @constructor            Constructor
 * @param       context    Context.
 * @param       packageMgr Package manager.
 */
public class ClientInterceptor @Inject constructor(
        context: Context,
        packageMgr: PackageManager
) : Interceptor {

    private val osName: String
    private val applName: String
    private val languageTag: String

    init {
        osName = buildString {
            append(ClientInterceptor.OS_NAME)
            append(ClientInterceptor.VALUE_SEP)
            append(Build.VERSION.SDK_INT)
        }

        applName = ClientInterceptor.initApplName(context, packageMgr)
        languageTag = ClientInterceptor.toLanguageTag(Locale.getDefault())
    }

    override fun intercept(chain: Interceptor.Chain?): Response =
            chain!!.proceed(chain.request().newBuilder()
                .header(HttpExt.HEADER_OS, osName)
                .header(HttpExt.HEADER_APPL, applName)
                .header(HttpExt.HEADER_LOCALE, languageTag)
                .build())

    private companion object {
        private const val LANG_UND = "und"
        private const val LANG_SEP = '-'
        private const val LOCALE_SEP = '_'
        private const val OS_NAME = "android"
        private const val VALUE_SEP = ';'

        /**
         * Initializes the application name and version.
         *
         * @return The value.
         */
        @Suppress("Deprecation")
        private fun initApplName(
                context: Context,
                packageMgr: PackageManager
        ): String {
            val name: String
            val packageInfo: PackageInfo

            name = context.packageName
            try {
                packageInfo = packageMgr.getPackageInfo(name, 0)
            } catch (ex: PackageManager.NameNotFoundException) {
                throw applicationException {
                    message(R.string.it_scoppelletti_err_packageNotFound) {
                        arguments {
                            add(name)
                        }
                    }
                    cause = ex
                }
            }

            return buildString {
                append(name)
                append(ClientInterceptor.VALUE_SEP)
                append(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    packageInfo.longVersionCode else packageInfo.versionCode)
            }
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
            buf.append(if (s.isNullOrBlank()) ClientInterceptor.LANG_UND else
                s.toLowerCase())

            s = locale.country
            if (!s.isNullOrBlank()) {
                buf.append(ClientInterceptor.LANG_SEP)
                        .append(s.toUpperCase())
            }

            s = locale.variant
            if (!s.isNullOrBlank()) {
                s.split(ClientInterceptor.LOCALE_SEP).forEach { variant ->
                    buf.append(ClientInterceptor.LANG_SEP)
                            .append(variant.toLowerCase())
                }
            }

            return buf.toString()
        }
    }
}