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

package it.scoppelletti.spaceship.http;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Creates the converters for exchanging JSON objects via HTTP.
 *
 * @since 1.0.0
 */
public final class JsonConverterFactory extends Converter.Factory {
    private static final MediaType myMediaType =
            MediaType.parse("application/json; charset=UTF-8");

    /**
     * Sole constructor.
     */
    private JsonConverterFactory() {
    }

    /**
     * Creates a new {@code JsonConvertFactory} instance.
     *
     * @return The new object.
     */
    public static JsonConverterFactory create() {
        return new JsonConverterFactory();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
            Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
            Retrofit client) {
        return new JsonConverterFactory.RequestConverter();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
            Annotation[] annotations, Retrofit client) {
        return new JsonConverterFactory.ResponseConverter();
    }

    /**
     * Converts JSON objects to their representation in HTTP.
     */
    static final class RequestConverter implements
            Converter<JSONObject, RequestBody> {

        /**
         * Sole constructor.
         */
        RequestConverter() {
        }

        @Override
        @SuppressWarnings("RedundantThrows")
        public RequestBody convert(JSONObject value) throws IOException {
            return RequestBody.create(myMediaType, value.toString());
        }
    }

    /**
     * Converts JSON objects from their representation in HTTP.
     */
    static final class ResponseConverter implements
            Converter<ResponseBody, JSONObject> {

        /**
         * Sole constructor.
         */
        ResponseConverter() {
        }

        @Override
        public JSONObject convert(ResponseBody value) throws IOException {
            try {
                return new JSONObject(value.string());
            } catch (JSONException ex) {
                throw new IOException(ex.getMessage(), ex);
            } finally {
                value.close();
            }
        }
    }
}
