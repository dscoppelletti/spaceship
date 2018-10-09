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

package it.scoppelletti.spaceship.ads.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.model.AdProvider
import kotlinx.android.synthetic.main.it_scoppelletti_ads_provider_item.view.*

/**
 * Adapter for `AdProvider` objects.
 *
 * @since 1.0.0
 *
 * @constructor             Constructor.
 * @param       adProviders Collection of Ad providers.
 * @param       onItemClick Handles the click event on an item.
 */
public class AdProviderListAdapter(
        private val adProviders: List<AdProvider>,
        private val onItemClick: (url: String) -> Unit
) : RecyclerView.Adapter<AdProviderViewHolder>() {

    override fun getItemCount(): Int = adProviders.size

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): AdProviderViewHolder {
        val itemView: View
        val inflater: LayoutInflater

        inflater = LayoutInflater.from(parent.context)
        itemView = inflater.inflate(R.layout.it_scoppelletti_ads_provider_item,
                parent, false)
        return AdProviderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdProviderViewHolder, position: Int) {
        val item: AdProvider

        item = adProviders[position]
        with (holder.itemView) {
            txtProvider.text = item.name
            setOnClickListener {
                onItemClick(item.policyUrl)
            }
        }
    }

    override fun onViewRecycled(holder: AdProviderViewHolder) {
        holder.itemView.setOnClickListener(null)
    }
}

/**
 * Supports `ViewHolder` pattern for `AdProvider` objects.
 *
 * @see   it.scoppelletti.spaceship.ads.model.AdProvider
 * @since 1.0.0
 *
 * @constructor          Constructor.
 * @param       itemView The view.
 */
public class AdProviderViewHolder(
        itemView: View
) : RecyclerView.ViewHolder(itemView)
