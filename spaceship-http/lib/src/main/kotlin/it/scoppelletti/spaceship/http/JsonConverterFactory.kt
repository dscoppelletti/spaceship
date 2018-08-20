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

package it.scoppelletti.spaceship.http

import java.io.IOException
import java.lang.reflect.Type
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * Creates the converters for exchanging JSON objects via HTTP.
 *
 * @since 1.0.0
 */
public class JsonConverterFactory private constructor() : Converter.Factory() {

    override fun requestBodyConverter(
            type: Type?,
            parameterAnnotations: Array<Annotation>?,
            methodAnnotations: Array<Annotation>?,
            client: Retrofit?
    ): Converter<*, RequestBody> {
        return JsonConverterFactory.RequestConverter()
    }

    override fun responseBodyConverter(
            type: Type?,
            annotations: Array<Annotation>?,
            client: Retrofit?
    ): Converter<ResponseBody, *> {
        return JsonConverterFactory.ResponseConverter()
    }

    public companion object {
        private val myMediaType = MediaType.parse(
                "application/json; charset=UTF-8")

        /**
         * Creates a new `JsonConvertFactory` instance.
         *
         * @return The new object.
         */
        public fun create(): JsonConverterFactory {
            return JsonConverterFactory()
        }
    }

    /**
     * Converts JSON objects to their representation in HTTP.
     */
    private class RequestConverter : Converter<JSONObject, RequestBody> {

        override fun convert(value: JSONObject): RequestBody {
            return RequestBody.create(myMediaType, value.toString())
        }
    }

    /**
     * Converts JSON objects from their representation in HTTP.
     */
    private class ResponseConverter : Converter<ResponseBody, JSONObject> {

        override fun convert(value: ResponseBody): JSONObject {
            try {
                return JSONObject(value.string())
            } catch (ex: JSONException) {
                throw IOException(ex.message, ex)
            } finally {
                value.close()
            }
        }
    }
}
