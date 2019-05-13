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
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.R
import javax.inject.Inject

/**
 * Implementation of the `ExceptionAdapter` interface.
 *
 * @since 1.0.0
 */
public class ApplicationExceptionAdapter @Inject constructor(
): ExceptionAdapter<ApplicationException> {

    override fun getView(ex: ApplicationException, parent: ViewGroup): View {
        val itemView: View
        val textView: TextView
        val inflater: LayoutInflater

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(
                R.layout.it_scoppelletti_applicationexception, parent, false)

        textView = itemView.findViewById(R.id.txtMessage)
        textView.text = ex.messageBuilder.build(parent.resources)

        return itemView
    }
}
