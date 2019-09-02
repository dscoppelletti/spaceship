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
        "unused")

package it.scoppelletti.spaceship.http

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Adds headers to a request.
 *
 * @since 1.0.0
 *
 * @constructor
 * @param       headers Headers to add.
 */
public class HeadersInterceptor(headers: Headers) : Interceptor {
    private val map: Map<String, List<String>>

    init {
        map = headers.toMultimap()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder

        builder = chain.request().newBuilder()

        map.forEach { (name, values) ->
            values.forEach { value ->
                builder.addHeader(name, value)
            }
        }

        return chain.proceed(builder.build())
    }
}