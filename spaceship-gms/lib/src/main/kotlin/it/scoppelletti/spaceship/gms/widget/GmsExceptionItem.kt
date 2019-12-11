/*
 * Copyright (C) 2019 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gms.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.scoppelletti.spaceship.gms.GmsException
import it.scoppelletti.spaceship.gms.R
import it.scoppelletti.spaceship.widget.ExceptionAdapter
import it.scoppelletti.spaceship.widget.ExceptionItem
import it.scoppelletti.spaceship.widget.ExceptionMapperHandler
import javax.inject.Inject

/**
 * Implementation of the `ExceptionItem` interface.
 *
 * @since 1.0.0
 *
 * @property statusCode    Status of the operation.
 * @property statusMessage Descriptive status of the operation.
 * @property isCanceled    Indicates whether the operation is canceled
 * @property isInterrupted Indicates whether the operation is interrupted.
 */
public data class GmsExceptionItem(
        public val statusCode: Int,
        public val statusMessage: String,
        public val isCanceled: Boolean,
        public val isInterrupted: Boolean,
        public val className: String,
        override val adapter: ExceptionAdapter<*>
) : ExceptionItem

/**
 * Implementation of the `ExceptionAdapter` interface.
 *
 * @since 1.0.0
 */
public class GmsExceptionAdapter : ExceptionAdapter<GmsExceptionItem> {

    override fun getView(
            ex: GmsExceptionItem,
            parent: ViewGroup
    ): View {
        val itemView: View
        val inflater: LayoutInflater
        val ctx: Context
        var textView: TextView

        ctx = parent.context
        inflater = LayoutInflater.from(ctx)
        itemView = inflater.inflate(
                R.layout.it_scoppelletti_gmsexception, parent, false)

        textView = itemView.findViewById(R.id.txtStatusMessage)
        textView.text = ex.statusMessage

        textView = itemView.findViewById(R.id.txtStatusCode)
        textView.text = ex.statusCode.toString()

        textView = itemView.findViewById(R.id.txtCanceled)
        textView.text = ctx.getString(
                if (ex.isCanceled) android.R.string.yes else
                    android.R.string.no)

        textView = itemView.findViewById(R.id.txtInterrupted)
        textView.text = ctx.getString(
                if (ex.isInterrupted) android.R.string.yes else
                    android.R.string.no)

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
public class GmsExceptionMapperHandler @Inject constructor(
): ExceptionMapperHandler<GmsException> {

    override fun map(ex: GmsException) : ExceptionItem =
            GmsExceptionItem(
                    statusCode = ex.statusCode,
                    statusMessage = ex.statusMessage.orEmpty(),
                    isCanceled = ex.isCanceled,
                    isInterrupted = ex.isInterrupted,
                    className = ex.javaClass.name,
                    adapter = GmsExceptionAdapter())
}

