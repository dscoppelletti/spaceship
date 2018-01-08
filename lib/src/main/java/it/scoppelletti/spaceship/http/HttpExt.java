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
import java.io.Reader;
import android.support.annotation.NonNull;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;
import it.scoppelletti.spaceship.io.IOExt;

/**
 * Operations for HTTP protocol.
 *
 * @since 1.0.0
 */
@Slf4j
public final class HttpExt {

    /**
     * Header reporting the client application name and version.
     */
    public static final String HEADER_APPL = "X-scoppelletti-appl";

    /**
     * Header {@code Authorization}.
     */
    public static final String HEADER_AUTH = "Authorization";

    /**
     * Header {@code Accept-Language}.
     */
    public static final String HEADER_LOCALE = "Accept-Language";

    /**
     * Header reporting the client <abbr title="Operative Syteme">OS</abbr> name
     * and version.
     */
    public static final String HEADER_OS = "X-scoppelletti-os";

    /**
     * Private constructor for static class.
     */
    private HttpExt() {
    }

    /**
     * Synchronously sends a request and returns its response.
     *
     * <p>The response body describing an HTTP error must mimics the JSON object
     * returned by the default {@code ErrorController} implementation in Spring
     * Boot 1.3.4:</p>
     *
     * <p><table width="100%" border="1" cellpadding="5">
     * <thead>
     * <tr>
     *     <th>Key</th>
     *     <th>Value</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     *     <td>{@code message}</td>
     *     <td>The localized message.</td>
     * </tr>
     * <tr>
     *     <td>{@code statusCode}</td>
     *     <td>The HTTP status code (optional).</td>
     * </tr>
     * <tr>
     *     <td>{@code error}</td>
     *     <td>Description of the HTTP status code (optional).</td>
     * </tr>
     * <tr>
     *     <td>{@code exception}</td>
     *     <td>Class og the original exception.</td>
     * </tr>
     * <tr>
     *     <td>{@code path}</td>
     *     <td>Path of the resource (optional).</td>
     * </tr>
     * <tr>
     *     <td>{@code timestamp}</td>
     *     <td>The timestamp of the exception. Represented by a millisecond value
     *     that is an offset from the <i>Epoch</i>.</td>
     * </tr>
     * </tbody>
     * </table></p>
     *
     * @param  call The request.
     * @param  <R>  Model of the response body.
     * @return      The response.
     * @throws      it.scoppelletti.spaceship.http.HttpApplicationException
     *              HTTP errors.
     * @throws      java.io.IOException
     *              Network errors.
     * @throws      java.lang.RuntimeException
     *              Unexpected errors.
     * @see         <a href="http://projects.spring.io/spring-boot"
     *              target="_blank">Spring Boot</a>
     */
    @NonNull
    public static <R> Response<R> execute(@NonNull Call<R> call) throws
            IOException {
        Gson converter;
        Response<R> resp;
        ResponseBody body;
        Reader reader = null;
        HttpApplicationException.Builder builder;

        if (call == null) {
            throw new NullPointerException("Argument call is null.");
        }

        resp = call.execute();
        if (resp.isSuccessful()) {
            return resp;
        }

        converter = new Gson();
        body = resp.errorBody();

        try {
            reader = body.charStream();
            builder = converter.fromJson(reader,
                    HttpApplicationException.Builder.class);
        } catch (RuntimeException err) {
            myLogger.error("Failed to convert response body.", err);
            builder = HttpExt.toException(call, resp, body);
        } finally {
            reader = IOExt.close(reader);
            body = IOExt.close(body);
        }

        throw builder.build();
    }

    /**
     * Builds an exception from an HTTP response.
     *
     * @param  call The request.
     * @param  resp The response.
     * @param  body The response body.
     * @return      The exception builder.
     */
    private static HttpApplicationException.Builder toException(Call<?> call,
            Response<?> resp, ResponseBody body) throws IOException {
        String msg;
        HttpApplicationException.Builder builder;

        try {
            msg = body.string();
        } catch (IOException ex) {
            myLogger.error("Failed to read response body.", ex);
            msg = resp.message();
        }

        builder = new HttpApplicationException.Builder();
        builder.message = msg;
        builder.statusCode = resp.code();
        builder.error = resp.message();
        builder.exception = HttpException.class.getName();
        builder.path = call.request().url().encodedPath();
        builder.timestamp = System.currentTimeMillis();
        return builder;
    }
}
