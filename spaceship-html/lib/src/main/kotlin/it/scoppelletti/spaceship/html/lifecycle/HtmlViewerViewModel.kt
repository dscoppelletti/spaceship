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

package it.scoppelletti.spaceship.html.lifecycle

import android.os.Bundle
import android.text.Html
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.app.HtmlViewerActivity
import it.scoppelletti.spaceship.html.fromHtml
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.lifecycle.ViewModelProviderEx
import it.scoppelletti.spaceship.types.StringExt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * `ViewModel` of a [HtmlViewerActivity] view.
 *
 * @since 1.0.0
 *
 * @property state State of the view.
 */
public class HtmlViewerViewModel(
        private val tagHandler: Html.TagHandler,
        private val handle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableLiveData<HtmlViewerState>()
    public val state: LiveData<HtmlViewerState> = _state

    init {
        val text: CharSequence?

        text = handle[HtmlViewerViewModel.PROP_TEXT]
        if (text != null) {
            _state.value = HtmlViewerState(text = text)
        }
    }

    /**
     * Builds the displayable styled text from the provided HTML string.
     *
     * @param source The source HTML string.
     */
    public fun buildText(source: String) = viewModelScope.launch {
        val text: CharSequence

        try {
            text = fromHtml(source, null, tagHandler)
            handle.set(HtmlViewerViewModel.PROP_TEXT, text)
            _state.value = HtmlViewerState(text = text)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _state.value = HtmlViewerState(error = SingleEvent(ex))
        }
    }

    private companion object {
        const val PROP_TEXT = HtmlExt.PROP_TEXT
    }
}

/**
 * State of a [HtmlViewerActivity] view.
 *
 * @since  1.0.0
 *
 * @property text  The styled text.
 * @property error The error.
 */
public data class HtmlViewerState (
        public val text: CharSequence = StringExt.EMPTY,
        public val error: SingleEvent<Throwable>? = null
)

/**
 * Implementation of `SavedStateViewModelProvider.Factory` interface for
 * [HtmlViewerViewModel] class.
 *
 * @since 1.0.0
 */
public class HtmlViewerViewModelFactory @Inject constructor(

        @Named(HtmlExt.DEP_TAGHANDLER)
        private val tagHandler: Html.TagHandler
) : ViewModelProviderEx.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle?
    ): T {
        val delegate: ViewModelProvider.Factory

        delegate = HtmlViewerViewModelFactory.Delegate(owner, defaultArgs,
                tagHandler)
        return delegate.create(HtmlViewerViewModel::class.java) as T
    }

    private class Delegate(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle?,
            private val tagHandler: Html.TagHandler
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
        ): T = HtmlViewerViewModel(tagHandler, handle) as T
    }
}
