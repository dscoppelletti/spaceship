/*
 * Copyright (C) 2018 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier")

package it.scoppelletti.spaceship.http.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.scoppelletti.spaceship.http.R
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import it.scoppelletti.spaceship.widget.ExceptionItem
import it.scoppelletti.spaceship.widget.ExceptionMapperHandler
import mu.KotlinLogging
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of the `ExceptionItem` interface.
 *
 * @since 1.0.0
 *
 * @property message    Message.
 * @property statusCode HTTP status code.
 * @property error      Description of the status code.
 * @property className  Exception class
 */
public data class HttpExceptionItem(
        public val message: String,
        public val statusCode: Int,
        public val error: String,
        public val className: String,
        override val adapter: ExceptionAdapter<*>
) : ExceptionItem

/**
 * Implementation of the `ExceptionAdapter` interface.
 *
 * @since 1.0.0
 */
public class HttpExceptionAdapter : ExceptionAdapter<HttpExceptionItem> {

    override fun getView(
            ex: HttpExceptionItem,
            parent: ViewGroup
    ): View {
        val itemView: View
        val inflater: LayoutInflater
        var textView: TextView

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(
                R.layout.it_scoppelletti_httpexception, parent, false)

        textView = itemView.findViewById(R.id.txtMessage)
        textView.text = ex.message

        textView = itemView.findViewById(R.id.txtStatusCode)
        textView.text = ex.statusCode.toString()

        textView = itemView.findViewById(R.id.txtError)
        textView.text = ex.error

        textView = itemView.findViewById(R.id.txtClass)
        textView.text = ex.className

        return itemView
    }
}

/**
 * Implementation of the `ExceptionMapper` interface.
 *
 * @since 1.0.0
 */
public class HttpExceptionMapperHandler @Inject constructor(
): ExceptionMapperHandler<HttpException> {

    override fun map(ex: HttpException) : ExceptionItem {
        val message: String
        val resp: Response<*>?
        val body: ResponseBody?
        val stream: BufferedSource

        resp = ex.response()
        if (resp == null) {
            // The original exception comes from a deserialization
            return map(ex, null, null)
        }

        body = resp.errorBody()
        if (body == null) {
            return map(ex, resp, null)
        }

        // I don't want to consume the original Okio source of the body
        stream = body.source().peek()
        try {
            message = stream.readUtf8()
        } catch (err: IOException) {
            logger.error("Failed to read response body.", err)
            // Use raw response
            return map(ex, resp, null)
        } finally {
            stream.closeQuietly()
        }

        return map(ex, resp, message)
    }

    /**
     * Maps an exception to a `HttpExceptionItem` object.
     *
     * @param  ex   Exception.
     * @param  resp Response.
     * @param  body Response body.
     * @return      The new object.
     */
    private fun map(
            ex: HttpException,
            resp: Response<*>?,
            body: String?
    ): HttpExceptionItem {
        val msg: String?
        var statusCode: Int
        var error: String?

        msg = if (body.isNullOrBlank()) ex.message else body

        statusCode = if (resp?.code() == null) 0 else resp.code()
        if (statusCode == 0) {
            statusCode = ex.code()
        }

        error = resp?.message()
        if (error.isNullOrBlank()) {
            error = ex.message()
        }

        return HttpExceptionItem(
                message = msg.orEmpty(),
                statusCode = statusCode,
                error = error.orEmpty(),
                className = ex.javaClass.name,
                adapter = HttpExceptionAdapter())
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
