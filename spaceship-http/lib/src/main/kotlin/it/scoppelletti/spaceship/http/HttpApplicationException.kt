/*
 * Copyright (C) 2017-2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.types.StringExt
import it.scoppelletti.spaceship.types.trimRaw
import mu.KLogger
import mu.KotlinLogging
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

private val logger: KLogger = KotlinLogging.logger {}

/**
 * HTTP application exception.
 *
 * @since 1.0.0
 *
 * @property statusCode HTTP status code.
 * @property error      Description of the HTTP status code.
 * @property exception  Class of the original exception from the server.
 * @property path       Path of the resource.
 * @property timestamp  Timestamp of the original exception from the server
 *                      (milliseconds from the *Epoch*).
 */
public class HttpApplicationException private constructor(
        builder: HttpApplicationException.Builder
): RuntimeException() {

    public val statusCode: Int
    public val error: String?
    public val exception: String
    public val path: String?
    public val timestamp: Long
    private val _message: String

    init {
        _message = builder.message
        statusCode = builder.statusCode
        error = builder.error
        exception = builder.exception
        path = builder.path
        timestamp = builder.timestamp
    }

    override val message: String?
        get() = toString()

    override fun getLocalizedMessage(): String = _message

    override fun toString(): String = """
        |HttpApplicationException(message=$_message,statusCode=$statusCode,
        |error=$error,exception=$exception,path=$path,
        |timestamp=$timestamp)""".trimRaw()

    /**
     * Builds an `HttpApplicationException` instance.
     *
     * @property message    Localized message.
     * @property statusCode HTTP status code.
     * @property error      Description of the HTTP status code.
     * @property exception  Class of the original exception from the server.
     * @property path       Path of the resource.
     * @property timestamp  Timestamp of the original exception from the server
     *                      (milliseconds from the *Epoch*).
     */
    @JsonClass(generateAdapter = true)
    internal class Builder {

        var message: String = StringExt.EMPTY
        var statusCode: Int = 0
        var error: String? = null
        var exception: String = StringExt.EMPTY
        var path: String? = null
        var timestamp: Long = 0L

        /**
         * Applies default values from the properties of a Retrofit HTTP
         * exception.
         *
         * @param source The source exception.
         */
        fun applyDefaults(source: HttpException) {
            if (message.isBlank()) {
                message = source.message.orEmpty()
            }

            if (statusCode == 0) {
                statusCode = source.code()
            }

            if (error.isNullOrBlank()) {
                error = source.message()
            }

            if (exception.isBlank()) {
                exception = source.javaClass.name
            }

            if (timestamp == 0L) {
                timestamp = System.currentTimeMillis()
            }
        }

        /**
         * Builds a new `HttpApplicationException` instance.
         *
         * @return The new object.
         */
        fun build(): HttpApplicationException {
            return HttpApplicationException(this)
        }
    }
}

/**
 * Converts a Retrofit HTTP exception to an `HttpApplicationException`
 * exception.
 *
 * * [Server error response](http://github.com/dscoppelletti/spaceship/wiki/Server-error-response)
 *
 * @receiver Source exception.
 * @return   Converted exception.
 * @since    1.0.0
 */
public fun HttpException.toHttpApplicationException(
): HttpApplicationException =
        makeBuilder(this)
                .apply {
                    applyDefaults(this@toHttpApplicationException)
                }
                .build()
                .apply {
                    initCause(this@toHttpApplicationException)
                }

/**
 * Creates a new `HttpApplicationException.Builder` instance.
 *
 * @param  source Original exception.
 * @return        The new object.
 */
private fun makeBuilder(
        source: HttpException
) : HttpApplicationException.Builder {
    val message: String
    val resp: Response<*>?
    val body: ResponseBody?
    val moshi: Moshi
    val stream: BufferedSource
    val adapter: JsonAdapter<HttpApplicationException.Builder>
    val builder: HttpApplicationException.Builder?

    resp = source.response()
    if (resp == null) {
        // The original exception comes from a deserialization
        return HttpApplicationException.Builder()
    }

    body = resp.errorBody()
    if (body == null) {
        return makeBuilder(source, resp, null)
    }

    // I don't want to consume the original Okio source of the body
    stream = body.source().peek()
    try {
        message = stream.readUtf8()
    } catch (ex: IOException) {
        logger.error("Failed to read response body.", ex)
        // Use raw response
        return makeBuilder(source, resp, null)
    } finally {
        stream.closeQuietly()
    }

    moshi = Moshi.Builder().build()
    adapter = moshi.adapter(HttpApplicationException.Builder::class.java)
            .failOnUnknown().nullSafe()

    builder = try {
        adapter.fromJson(message)
    } catch (ex: Exception) { // IOException|JsonDataException
        logger.error("Failed to convert response body.", ex)
        null
    }

    return builder ?: makeBuilder(source, resp, message)
}

/**
 * Creates a new `HttpApplicationException.Builder` instance.
 *
 * @param  source Original exception.
 * @param  resp   Response.
 * @param  body   Response body.
 * @return        The new object.
 */
private fun makeBuilder(
        source: HttpException,
        resp: Response<*>,
        body: String?
) : HttpApplicationException.Builder =
        HttpApplicationException.Builder().apply {
            if (body != null) {
               message = body
            }

            statusCode = resp.code()
            error = resp.message()
            exception = source.javaClass.name
            timestamp = System.currentTimeMillis()
        }