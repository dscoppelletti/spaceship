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
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import it.scoppelletti.spaceship.io.IOExt;

/**
 * HTTP application exception.
 *
 * @since 1.0.0
 */
@Slf4j
public final class HttpApplicationException extends RuntimeException {
    private static final long serialVersionUID = 1;

    /**
     * @serial The localized message.
     */
    private String myMessage;

    /**
     * @serial The HTTP status code. May be {@code 0}.
     */
    private int myStatusCode;

    /**
     * @serial Description of the HTTP status code. May be {@code null}.
     */
    private String myError;

    /**
     * @serial Class of the original exception.
     */
    private String myException;

    /**
     * @serial Path of the resource. May be {@code null}.
     */
    private String myPath;

    /**
     * @serial Timestamp of the exception. Represented by a millisecond value
     *         that is an offset from the <i>Epoch</i>.
     */
    private long myTimestamp;

    /**
     * Constructor.
     *
     * @param builder The instance builder.
     */
    private HttpApplicationException(Builder builder) {
        myMessage = builder.message;
        myStatusCode = builder.statusCode;
        myError = builder.error;
        myException = builder.exception;
        myPath = builder.path;
        myTimestamp = builder.timestamp;
    }

    /**
     * Constructor.
     *
     * @param ex The original exception.
     */
    private HttpApplicationException(HttpException ex) {
        myMessage = ex.getMessage();
        myStatusCode = ex.code();
        myError = ex.message();
        myException = HttpException.class.getName();
        myTimestamp = System.currentTimeMillis();
        initCause(ex);
    }

    /**
     * Converts a Retrofit HTTP exception in a Spaceship exception.
     *
     * <p>The response body describing the HTTP error must mimic the JSON object
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
     * @param  ex The original exception.
     * @return    The converted exception.
     * @see       <a href="http://projects.spring.io/spring-boot"
     *            target="_blank">Spring Boot</a>
     */
    @NonNull
    public static HttpApplicationException create(@NonNull HttpException ex) {
        Gson converter;
        Response<?> resp;
        ResponseBody body;
        Reader reader = null;
        HttpApplicationException applEx;
        HttpApplicationException.Builder builder;

        if (ex == null) {
            throw new NullPointerException("Argument ex is null.");
        }

        resp = ex.response();
        if (resp == null) {
            // The original exception comes from a deserialization
            return new HttpApplicationException(ex);
        }

        converter = new Gson();
        body = resp.errorBody();

        try {
            reader = body.charStream();
            builder = converter.fromJson(reader,
                    HttpApplicationException.Builder.class);
        } catch (RuntimeException err) {
            myLogger.error("Failed to convert response body.", err);
            builder = new HttpApplicationException.Builder(resp, body);
        } finally {
            reader = IOExt.close(reader);
            body = IOExt.close(body);
        }

        applEx = builder.build();
        applEx.copy(ex);
        applEx.initCause(ex);
        return applEx;
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String getLocalizedMessage() {
        return myMessage;
    }

    /**
     * Applies default values from the properties of a Retrofit HTTP exception.
     *
     * @param ex The original exception.
     */
    private void copy(HttpException ex) {
        if (TextUtils.isEmpty(myMessage)) {
            myMessage = ex.getMessage();
        }

        if (myStatusCode == 0) {
            myStatusCode = ex.code();
        }

        if (TextUtils.isEmpty(myError)) {
            myError = ex.message();
        }

        if (TextUtils.isEmpty(myException)) {
            myException = HttpException.class.getName();
        }

        if (myTimestamp == 0L) {
            myTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getName())
                .append("(message=")
                .append(myMessage)
                .append(";statusCode=")
                .append(myStatusCode)
                .append(";error=")
                .append(myError)
                .append(";exception=")
                .append(myException)
                .append(";path=")
                .append(myPath)
                .append(";timestamp=")
                .append(myTimestamp)
                .append(")").toString();
    }

    /**
     * Gets the HTTP status code.
     *
     * @return The value. May be {@code 0}.
     * @see    #getError()
     */
    public int getStatusCode() {
        return myStatusCode;
    }

    /**
     * Gets the description the HTTP status code.
     *
     * @return The value. May be {@code null}.
     * @see    #getStatusCode()
     */
    @Nullable
    public String getError() {
        return myError;
    }

    /**
     * Gets the class of the original exception.
     *
     * @return The value.
     */
    public String getException() {
       return myException;
    }

    /**
     * Gets the path of the resource.
     *
     * @return The value. May be {@code null}.
     */
    @Nullable
    public String getPath() {
        return myPath;
    }

    /**
     * Gets the timestamp of the exception.
     *
     * @return The value (milliseconds since the <i>Epoch</i>).
     */
    public long getTimestamp() {
        return myTimestamp;
    }

    /**
     * Builds an {@code HttpApplicationException} instance.
     */
    private static final class Builder {

        /**
         * The localized message.
         */
        String message;

        /**
         * The HTTP status code. May be {@code 0}.
         */
        int statusCode;

        /**
         * Description of the HTTP status code. May be {@code null}.
         */
        String error;

        /**
         * Class of the original exception.
         */
        String exception;

        /**
         * Path of the resource. May be {@code null}.
         */
        String path;

        /**
         * Timestamp of the exception. Represented by a millisecond value that
         * is an offset from the <i>Epoch</i>.
         */
        long timestamp;

        /**
         * Sole constructor.
         */
        Builder() {
        }

        /**
         * Constructor.
         *
         * @param  resp The response.
         * @param  body The response body.
         */
        Builder(Response<?> resp, ResponseBody body) {
            try {
                message = body.string();
            } catch (IOException ex) {
                myLogger.error("Failed to read response body.", ex);
                message = resp.toString(); // raw response
            }

            statusCode = resp.code();
            error = resp.message();
            exception = HttpException.class.getName();
            timestamp = System.currentTimeMillis();
        }

        /**
         * Builds a new {@code HttpApplicationException} instance.
         *
         * @return The new object.
         */
        @NonNull
        HttpApplicationException build() {
            return new HttpApplicationException(this);
        }
    }
}
