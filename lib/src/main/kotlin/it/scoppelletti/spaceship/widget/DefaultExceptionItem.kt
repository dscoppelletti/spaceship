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

package it.scoppelletti.spaceship.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import it.scoppelletti.spaceship.R
import it.scoppelletti.spaceship.toMessage
import kotlinx.parcelize.Parcelize

/**
 * Default implementation of the `ExceptionItem` interface.
 *
 * @since 1.0.0
 *
 * @property className Name of the exception class.
 * @property message   Message.
 */
@Parcelize
public data class DefaultExceptionItem(
        public val className: String,
        public val message: String,
        override val adapter: ExceptionAdapter<*>
) : ExceptionItem

/**
 * Default implementation of the `ExceptionAdapter` interface.
 *
 * @since 1.0.0
 */
@Parcelize
public class DefaultExceptionAdapter : ExceptionAdapter<DefaultExceptionItem> {

    override fun getView(ex: DefaultExceptionItem, parent: ViewGroup): View {
        val itemView: View
        val inflater: LayoutInflater
        var textView: TextView

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.it_scoppelletti_exception, parent,
                false)

        textView = itemView.findViewById(R.id.txtClass)
        textView.text = ex.className

        textView = itemView.findViewById(R.id.txtMessage)
        textView.text = ex.message

        return itemView
    }
}

/**
 * Default implementation of the `ExceptionMapper` interface.
 *
 * @since 1.0.0
 */
public class DefaultExceptionMapperHandler : ExceptionMapperHandler<Throwable> {

    override fun map(ex: Throwable) : ExceptionItem =
            DefaultExceptionItem(ex.javaClass.name,
                    ex.toMessage(), DefaultExceptionAdapter())
}
