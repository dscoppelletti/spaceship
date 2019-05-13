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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.http.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.scoppelletti.spaceship.http.R
import it.scoppelletti.spaceship.io.closeQuietly
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import mu.KotlinLogging
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of the `ExceptionAdapter` interface.
 *
 * @since 1.0.0
 */
public class HttpExceptionAdapter @Inject constructor(
): ExceptionAdapter<HttpException> {

    override fun getView(ex: HttpException, parent: ViewGroup): View {
        val message: String
        val itemView: View
        val inflater: LayoutInflater
        val resp: Response<*>?
        val body: ResponseBody?
        val stream: BufferedSource

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(
                R.layout.it_scoppelletti_httpexception, parent,
                false)

        resp = ex.response()
        if (resp == null) {
            // The original exception comes from a deserialization
            return bind(itemView, ex, null, null)
        }

        body = resp.errorBody()
        if (body == null) {
            return bind(itemView, ex, resp, null)
        }

        // I don't want to consume the original Okio source of the body
        stream = body.source().peek()
        try {
            message = stream.readUtf8()
        } catch (err: IOException) {
            logger.error("Failed to read response body.", err)
            // Use raw response
            return bind(itemView, ex, resp, null)
        } finally {
            stream.closeQuietly()
        }

        return bind(itemView, ex, resp, message)
    }

    /**
     * Updates a view contents with an exception.
     *
     * @param  itemView View to update.
     * @param  ex       Exception.
     * @param  resp     Response.
     * @param  body     Response body.
     * @return          The updated view.
     */
    private fun bind(
            itemView: View,
            ex: HttpException,
            resp: Response<*>?,
            body: String?
    ): View {
        var code: Int
        var msg: String?
        var textView: TextView

        msg = if (body.isNullOrBlank()) ex.message else body

        textView = itemView.findViewById(R.id.txtMessage)
        textView.text = msg.orEmpty()

        code = if (resp?.code() == null) 0 else resp.code()
        if (code == 0) {
            code = ex.code()
        }

        textView = itemView.findViewById(R.id.txtStatusCode)
        textView.text = code.toString()

        msg = resp?.message()
        if (msg.isNullOrBlank()) {
            msg = ex.message()
        }

        textView = itemView.findViewById(R.id.txtError)
        textView.text = msg.orEmpty()

        textView = itemView.findViewById(R.id.txtClass)
        textView.text = ex.javaClass.name

        return itemView
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}