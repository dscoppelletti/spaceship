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

package it.scoppelletti.spaceship.preference.widget

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.scoppelletti.spaceship.preference.R
import it.scoppelletti.spaceship.preference.model.Credit
import kotlinx.android.synthetic.main.it_scoppelletti_pref_credit_item.view.*

/**
 * Adapts a collection of credits to provide views for a `RecyclerView` widget.
 *
 * @see   it.scoppelletti.spaceship.preference.CreditsActivity
 * @since 1.0.0
 *
 * @constructor      Constructor.
 * @param       list Collection.
 */
public class CreditListAdapter(
        private val list: List<Credit>
) : RecyclerView.Adapter<CreditViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): CreditViewHolder {
        val itemView: View
        val inflater: LayoutInflater

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.it_scoppelletti_pref_credit_item,
                parent, false)

        with (itemView) {
            txtComponent.movementMethod = LinkMovementMethod.getInstance()
            txtOwner.movementMethod = LinkMovementMethod.getInstance()
            txtLicense.movementMethod = LinkMovementMethod.getInstance()
        }

        return CreditViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CreditViewHolder, position: Int) {
        val credit: Credit

        credit = list[position]
        with (holder.itemView) {
            txtComponent.text = credit.component
            txtOwner.text = credit.owner
            txtLicense.text = credit.license
        }
    }
}

/**
 * Renders a credit as an item in a `RecyclerView` widget.
 *
 * @see   it.scoppelletti.spaceship.preference.CreditsActivity
 * @since 1.0.0.
 *
 * @constructor          Constructor.
 * @param       itemView View.
 */
public class CreditViewHolder(
        itemView: View
) : RecyclerView.ViewHolder(itemView)
