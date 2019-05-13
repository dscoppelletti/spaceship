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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.UiThread
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.chip.Chip
import it.scoppelletti.spaceship.ads.R
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPrivacyState
import it.scoppelletti.spaceship.ads.lifecycle.ConsentPrivacyViewModel
import it.scoppelletti.spaceship.ads.lifecycle.ConsentViewModel
import it.scoppelletti.spaceship.ads.model.AdProvider
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.inject.Injectable
import kotlinx.android.synthetic.main.it_scoppelletti_ads_consentprivacy_fragment.*
import java.lang.RuntimeException
import javax.inject.Inject

/**
 * Gives access to the privacy policies.
 *
 * @see   it.scoppelletti.spaceship.ads.app.AbstractConsentActivity
 * @since 1.0.0
 */
@UiThread
public class ConsentPrivacyFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var exDialog: ExceptionDialogFragment.Builder

    private lateinit var activityViewModel: ConsentViewModel
    private lateinit var viewModel: ConsentPrivacyViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(
            R.layout.it_scoppelletti_ads_consentprivacy_fragment, container,
            false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtHeader.movementMethod = LinkMovementMethod.getInstance()
        txtFooter.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val url: String

        super.onActivityCreated(savedInstanceState)

        activityViewModel = ViewModelProviders.of(requireActivity(),
                viewModelFactory).get(ConsentViewModel::class.java)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ConsentPrivacyViewModel::class.java)

        viewModel.state.observe(this, Observer<ConsentPrivacyState> { state ->
            txtHeader.text = state?.header
            txtFooter.text = state?.footer
        })

        url = getString(R.string.it_scoppelletti_url_privacy)
        viewModel.buildText(getString(R.string.it_scoppelletti_ads_html_header),
                getString(R.string.it_scoppelletti_ads_html_footer, url))

        showProviders(activityViewModel.state.value?.data?.adProviders
                ?: emptyList())

        view?.findViewById<Button>(R.id.cmdBack)?.setOnClickListener {
            activityViewModel.backStep()
        }
    }

    /**
     * Shows Ad providers.
     *
     * @param adProviders Collection.
     */
    private fun showProviders(adProviders: List<AdProvider>) {
        var chip: Chip

        for (provider: AdProvider in adProviders) {
            chip = Chip(requireContext()).apply {
                text = provider.name
                isCheckable = false
                setOnClickListener {
                    openUrl(provider.policyUrl)
                }
            }

            ViewCompat.setLayoutDirection(chip,
                    ViewCompat.LAYOUT_DIRECTION_LOCALE)

            grdProviders.addView(chip, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    /**
     * Visits an URL.
     *
     * @param url URL.
     */
    private fun openUrl(url: String) {
        val intent: Intent

        try {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (ex: RuntimeException) {
            exDialog.show(requireActivity(), applicationException {
                message(R.string.it_scoppelletti_err_startActivity)
                cause = ex
            })
        }
    }

    public companion object {

        /**
         * Fragment position.
         */
        public const val POS = 4

        /**
         * Creates a new fragment.
         *
         * @return The new object.
         */
        public fun newInstance() = ConsentPrivacyFragment()
    }
}
