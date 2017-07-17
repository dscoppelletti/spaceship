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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * HTTP application exception.
 *
 * @since 1.0.0
 */
public class HttpApplicationException extends RuntimeException {
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

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String getLocalizedMessage() {
        return myMessage;
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
     * Builds an {@code HttpApplicationException} instance.
     */
    static final class Builder {

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
