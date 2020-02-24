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

@file:Suppress("JoinDeclarationAndAssignment", "RedundantVisibilityModifier",
        "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.lifecycle

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import it.scoppelletti.spaceship.app.AlertDialogFragment
import it.scoppelletti.spaceship.app.AppExt
import it.scoppelletti.spaceship.i18n.I18NProvider
import it.scoppelletti.spaceship.i18n.MessageSpec
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `ViewModel` of an [AlertDialogFragment] view.
 *
 * @since 1.0.0
 *
 * @property message Message.
 */
public class AlertDialogModel(
        private val i18nProvider: I18NProvider,
        private val handle: SavedStateHandle
): ViewModel() {

    private val _message = MutableLiveData<String>()
    public val message: LiveData<String> = _message

    init {
        _message.value = handle[AlertDialogModel.PROP_MSG]
    }

    /**
     * Loads the message.
     *
     * @param messageSpec Message specification.
     */
    public fun load(messageSpec: MessageSpec) = viewModelScope.launch {
        val msg: String?

        msg = messageSpec.buildMessage(i18nProvider)
        handle.set(AlertDialogModel.PROP_MSG, msg)

        _message.value = msg
    }

    private companion object {
        const val PROP_MSG = AppExt.PROP_MESSAGE
    }
}

/**
 * Implementation of `SavedStateViewModelProvider.Factory` interface for
 * [AlertDialogModel] class.
 *
 * @since 1.0.0
 */
public class AlertDialogModelFactory @Inject constructor(
        private val i18nProvider: I18NProvider
) : ViewModelProviderEx.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle?
    ): T {
        val delegate: ViewModelProvider.Factory

        delegate = AlertDialogModelFactory.Delegate(owner, defaultArgs,
                i18nProvider)
        return delegate.create(AlertDialogModel::class.java) as T
    }

    private class Delegate(
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle?,
            private val i18nProvider: I18NProvider
    ) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
        ): T = AlertDialogModel(i18nProvider, handle) as T
    }
}


