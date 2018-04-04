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
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Converter;
import it.scoppelletti.spaceship.io.IOExt;

/**
 * Converts JSON objects from their representation in HTTP.
 */
final class JsonResponseBodyConverter implements
        Converter<ResponseBody, JSONObject> {

    /**
     * Sole constructor.
     */
    JsonResponseBodyConverter() {
    }

    @Override
    public JSONObject convert(ResponseBody value) throws IOException {
        try {
            return new JSONObject(value.string());
        } catch (JSONException ex) {
            throw new IOException(ex.getMessage(), ex);
        } finally {
            value = IOExt.close(value);
        }
    }
}
