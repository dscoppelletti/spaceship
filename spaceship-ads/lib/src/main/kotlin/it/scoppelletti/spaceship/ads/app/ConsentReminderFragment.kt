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

package it.scoppelletti.spaceship.ads.app

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.consent.ConsentStatus
import it.scoppelletti.spaceship.ads.lifecycle.ConsentFragmentViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.app.uiComponent
import kotlinx.android.synthetic.main.it_scoppelletti_ads_consentreminder_fragment.*

/**
 * Reminds the user that she can change her choice anytime.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @since 1.0.0
 */
@UiThread
public class ConsentReminderFragment : Fragment() {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var activityViewModel: ConsentViewModel
    private lateinit var viewModel: ConsentFragmentViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(
            R.layout.it_scoppelletti_ads_consentreminder_fragment, container,
            false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtMessage.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val url: String
        val activity: FragmentActivity

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()
        viewModelFactory = activity.uiComponent().viewModelFactory()
        activityViewModel = ViewModelProviders.of(activity, viewModelFactory)
                .get(ConsentViewModel::class.java)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ConsentFragmentViewModel::class.java)

        viewModel.text.observe(viewLifecycleOwner,
                Observer<CharSequence> { text ->
                    txtMessage.text = text
                })

        url = getString(R.string.it_scoppelletti_url_privacy)
        viewModel.buildText(getString(
                R.string.it_scoppelletti_ads_html_reminder, url))

        cmdAgree.setOnClickListener {
            activityViewModel.save(ConsentStatus.NON_PERSONALIZED)
        }

        cmdBack.setOnClickListener {
            activityViewModel.backStep()
        }
    }

    public companion object {

        /**
         * Fragment position.
         */
        public const val POS = 5

        /**
         * Creates a new fragment.
         *
         * @return The new object.
         */
        public fun newInstance() = ConsentReminderFragment()
    }
}
