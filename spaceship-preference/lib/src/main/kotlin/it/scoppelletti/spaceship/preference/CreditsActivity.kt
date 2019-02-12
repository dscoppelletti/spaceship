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

package it.scoppelletti.spaceship.preference

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.preference.lifecycle.CreditsState
import it.scoppelletti.spaceship.preference.lifecycle.CreditsViewModel
import it.scoppelletti.spaceship.preference.widget.CreditListAdapter
import kotlinx.android.synthetic.main.it_scoppelletti_pref_credits_activity.*
import mu.KLogger
import mu.KotlinLogging
import javax.inject.Inject

/**
 * Shows the credits of this application.
 *
 * @see   it.scoppelletti.spaceship.preference.credit.CreditsLoader
 * @since 1.0.0
 */
@UiThread
public class CreditsActivity : AppCompatActivity(),
        Injectable,
        HasSupportFragmentInjector,
        OnDialogResultListener {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector:
            DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var exDialog: ExceptionDialogFragment.Builder

    private lateinit var viewModel: CreditsViewModel
    private lateinit var adapter: CreditListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val creditId: Int
        val actionBar: ActionBar
        val listLayout : LinearLayoutManager

        super.onCreate(savedInstanceState)
        setContentView(R.layout.it_scoppelletti_pref_credits_activity)

        setSupportActionBar(toolbar)
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        refreshLayout.isEnabled = false
        refreshLayout.setColorSchemeResources(R.color.secondaryColor)
        listLayout = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        listView.layoutManager = listLayout

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CreditsViewModel::class.java)

        creditId = intent.getIntExtra(CreditsActivity.PROP_CREDITS, 0)
        if (creditId > 0) {
            viewModel.load(creditId)
        } else {
            logger.error { "Property ${CreditsActivity.PROP_CREDITS} not set." }
        }

        viewModel.state.observe(this, Observer<CreditsState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
            fragmentDispatchingAndroidInjector

    private fun stateObserver(state: CreditsState) {
        if (state.waiting) {
            refreshLayout.isRefreshing = true
        }

        adapter = CreditListAdapter(state.items)
        listView.adapter = adapter

        if (!state.waiting) {
            refreshLayout.isRefreshing = false
        }

        state.error?.poll()?.let { err ->
            exDialog.show(this, err)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                tryFinish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            ExceptionDialogFragment.TAG -> {
                tryFinish()
            }
        }
    }

    public companion object {

        /**
         * Property containing the credits for this application as a XML resource
         * ID.
         */
        public const val PROP_CREDITS: String = PreferenceExt.PROP_CREDITS

        private val logger: KLogger = KotlinLogging.logger {}
    }
}