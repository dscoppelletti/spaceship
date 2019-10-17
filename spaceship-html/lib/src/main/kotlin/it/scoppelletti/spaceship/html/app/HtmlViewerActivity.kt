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

package it.scoppelletti.spaceship.html.app

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.annotation.UiThread
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.app.uiComponent
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.R
import it.scoppelletti.spaceship.html.lifecycle.HtmlViewerState
import it.scoppelletti.spaceship.html.lifecycle.HtmlViewerViewModel
import kotlinx.android.synthetic.main.it_scoppelletti_htmlviewer_activity.*
import mu.KotlinLogging

/**
 * Activity for displaying an HTML text.
 *
 * @since 1.0.0
 */
@UiThread
public class HtmlViewerActivity : AppCompatActivity(), OnDialogResultListener {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HtmlViewerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val textId: Int
        val titleId: Int
        val actionBar: ActionBar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.it_scoppelletti_htmlviewer_activity)
        setSupportActionBar(toolbar)

        if (intent.getBooleanExtra(HtmlViewerActivity.PROP_HOMEASUP, false)) {
            actionBar = supportActionBar!!
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        titleId = intent.getIntExtra(HtmlViewerActivity.PROP_TITLE, 0)
        if (titleId > 0) {
            supportActionBar?.setTitle(titleId)
            setTitle(titleId)
        }

        txtContent.movementMethod = LinkMovementMethod.getInstance()

        viewModelFactory = uiComponent().viewModelFactory()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HtmlViewerViewModel::class.java)

        viewModel.state.observe(this, Observer<HtmlViewerState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })

        textId = intent.getIntExtra(HtmlViewerActivity.PROP_TEXT, 0)
        if (textId > 0) {
            viewModel.buildText(getString(textId))
        } else {
            logger.error { "Metadata ${HtmlViewerActivity.PROP_TEXT} not set." }
        }
    }

    private fun stateObserver(state: HtmlViewerState) {
        txtContent.text = state.text

        state.error?.poll()?.let { ex ->
            showExceptionDialog(ex)
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
         * Property indicating whether `home` should be displayed as an `up`
         * affordance.
         */
        public const val PROP_HOMEASUP: String = HtmlExt.PROP_HOMEASUP

        /**
         * Property containing the HTML text as a string resource ID.
         */
        public const val PROP_TEXT: String = HtmlExt.PROP_TEXT

        /**
         * Property containing the title as a string resource ID.
         */
        public const val PROP_TITLE: String = HtmlExt.PROP_TITLE

        private val logger = KotlinLogging.logger {}
    }
}