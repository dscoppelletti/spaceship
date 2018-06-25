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

package it.scoppelletti.spaceship.html.app

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.html.R
import it.scoppelletti.spaceship.html.lifecycle.HtmlViewerState
import it.scoppelletti.spaceship.html.lifecycle.HtmlViewerViewModel
import it.scoppelletti.spaceship.inject.Injectable
import kotlinx.android.synthetic.main.it_scoppelletti_htmlviewer_activity.*
import mu.KotlinLogging
import javax.inject.Inject

/**
 * Activity for displaying an HTML text.
 *
 * You have to set the following `<meta-data>` elements in the activity
 * definition in your `AndroidManifest.xml`:
 *
 * 1. `it.scoppelletti.spaceship.html.text`
 *
 *    The HTML text as a string resource ID.
 *
 * @since 1.0.0
 *
 * @constructor Sole constructor.
 */
public class HtmlViewerActivity : AppCompatActivity(),
        Injectable,
        OnDialogResultListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HtmlViewerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val textId: Int
        val actionBar: ActionBar
        val activityInfo: ActivityInfo

        super.onCreate(savedInstanceState)
        setContentView(R.layout.it_scoppelletti_htmlviewer_activity)

        setSupportActionBar(toolbar)
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HtmlViewerViewModel::class.java)

        viewModel.state.observe(this, Observer<HtmlViewerState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })

        try {
            activityInfo = packageManager.getActivityInfo(componentName,
                    PackageManager.GET_META_DATA)
        } catch (ex: PackageManager.NameNotFoundException) {
            logger.error(ex) {
                "Failed to get ActivityInfo for activity $componentName."
            }

            return
        }

        textId = activityInfo.metaData?.getInt(
                HtmlViewerActivity.PROP_TEXT, 0) ?: 0
        if (textId > 0) {
            viewModel.buildText(getString(textId))
        } else {
            logger.error { "Metadata ${HtmlViewerActivity.PROP_TEXT} not set." }
        }
    }

    private fun stateObserver(state: HtmlViewerState) {
        txtContent.text = state.text

        state.error?.poll()?.let { ex ->
            ExceptionDialogFragment.show(this, ex, HtmlViewerActivity.DLG_ERROR)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (!isFinishing) {
                    finish()
                }

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDialogResult(dialogId: Int, which: Int) {
        when (dialogId) {
            HtmlViewerActivity.DLG_ERROR -> {
                if (!isFinishing) {
                    finish()
                }

                return
            }
        }
    }

    companion object {

        /**
         * Property containing an HTML text as a string resource ID.
         */
        public const val PROP_TEXT: String =
                "it.scoppelletti.spaceship.html.text"

        private const val DLG_ERROR: Int = 1
        private val logger = KotlinLogging.logger {}
    }
}