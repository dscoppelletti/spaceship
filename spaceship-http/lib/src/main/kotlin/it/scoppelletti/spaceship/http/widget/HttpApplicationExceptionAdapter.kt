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

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.scoppelletti.spaceship.http.HttpApplicationException
import it.scoppelletti.spaceship.http.R
import it.scoppelletti.spaceship.types.TimeExt
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

/**
 * Implementation of the `ExceptionAdapter` interface.
 *
 * @since 1.0.0
 */
public class HttpApplicationExceptionAdapter @Inject constructor(
): ExceptionAdapter<HttpApplicationException> {

    @Suppress("RemoveRedundantQualifierName")
    override fun getView(
            ex: HttpApplicationException,
            parent: ViewGroup
    ): View {
        val itemView: View
        val inflater: LayoutInflater
        val timestamp: Calendar
        var textView: TextView

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(
                R.layout.it_scoppelletti_httpapplicationexception, parent,
                false)

        textView = itemView.findViewById(R.id.txtMessage)
        textView.text = ex.localizedMessage

        textView = itemView.findViewById(R.id.txtStatusCode)
        textView.text = ex.statusCode.toString()

        textView = itemView.findViewById(R.id.txtError)
        textView.text = ex.error.orEmpty()

        textView = itemView.findViewById(R.id.txtClass)
        textView.text = ex.exception

        textView = itemView.findViewById(R.id.txtPath)
        textView.text = ex.path.orEmpty()

        timestamp = Calendar.getInstance(TimeZone.getTimeZone(TimeExt.TZ_UTC))
        timestamp.timeInMillis = ex.timestamp

        textView = itemView.findViewById(R.id.txtTimestamp)
        textView.text = DateFormat.format(
                HttpApplicationExceptionAdapter.TIMESTAMP_FORMAT, timestamp)

        return itemView
    }

    private companion object {
        const val TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }
}