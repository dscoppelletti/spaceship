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

package it.scoppelletti.spaceship.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * Adapter to keep nulls in serialization.
 *
 * * [moshi custom qualifier annotation to serialise null on one property only](http://stackoverflow.com/a/52265735/3311816)
 *
 * @since 1.0.0
 */
@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SerializeNulls {

    object AdapterFactory : JsonAdapter.Factory {

        override fun create(
                type: Type,
                annotations: MutableSet<out Annotation>,
                moshi: Moshi
        ): JsonAdapter<*>? {
            val nextAnnotations: MutableSet<out Annotation>?

            nextAnnotations = Types.nextAnnotations(annotations,
                    SerializeNulls::class.java)
            if (nextAnnotations == null) {
                return null
            }

            return moshi.nextAdapter<Any>(this, type,
                    nextAnnotations).serializeNulls()
        }
    }
}
