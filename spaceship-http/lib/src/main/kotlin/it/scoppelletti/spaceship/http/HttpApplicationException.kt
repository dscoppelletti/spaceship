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
import mu.KotlinLogging
import okhttp3.ResponseBody
import okio.Source
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

private val logger = KotlinLogging.logger {}

/**
 * HTTP application exception.
 *
 * @since               1.0.0
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
 * @receiver The source exception.
 * @return   The converted exception.
 * @since    1.0.0
 */
public fun HttpException.toHttpApplicationException(
): HttpApplicationException {
    val body: ResponseBody?
    val resp: Response<*>?
    val moshi: Moshi
    val adapter: JsonAdapter<HttpApplicationException.Builder>
    var builder: HttpApplicationException.Builder?
    var source: Source? = null

    resp = this.response()
    if (resp == null) {
        // The original exception comes from a deserialization
        builder = HttpApplicationException.Builder()
    } else {
        body = resp.errorBody()

        if (body == null) {
            builder = makeBuilder(this, resp, null)
        } else {
            moshi = Moshi.Builder().build()
            adapter = moshi.adapter(
                    HttpApplicationException.Builder::class.java)

            try {
                source = body.source()
                builder = adapter.fromJson(body.source())
                if (builder == null) {
                    // It could happen only if I use the nullSafe version of the
                    // adapter
                    throw IOException("Error response body is empty.")
                }
            } catch (ex: Exception) { // IOException|JsonDataException
                logger.error("Failed to convert response body.", ex)
                builder = makeBuilder(this, resp, body)
            } finally {
                source?.closeQuietly()
                body.closeQuietly()
            }
        }
    }

    builder?.applyDefaults(this)
    return builder!!.build().apply {
        initCause(this)
    }
}

/**
 * Creates a new `HttpApplicationException.Builder` instance.
 *
 * @param  source The original exception.
 * @param  resp   The response.
 * @param  body   The response body.
 * @return        The new object.
 */
private fun makeBuilder(
        source: HttpException,
        resp: Response<*>,
        body: ResponseBody?
) : HttpApplicationException.Builder =
        HttpApplicationException.Builder().apply {
            if (body != null) {
                try {
                    message = body.string()
                } catch (ex: IOException) {
                    logger.error("Failed to read response body.", ex)
                    message = body.toString() // raw response
                }
            }

            statusCode = resp.code()
            error = resp.message()
            exception = source.javaClass.name
            timestamp = System.currentTimeMillis()
        }