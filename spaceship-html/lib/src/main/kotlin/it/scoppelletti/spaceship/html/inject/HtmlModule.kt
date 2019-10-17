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

@file:Suppress("RedundantVisibilityModifier", "unused")

package it.scoppelletti.spaceship.html.inject

import android.text.Html
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import it.scoppelletti.spaceship.html.ApplicationLabelTagHandler
import it.scoppelletti.spaceship.html.ApplicationVersionTagHandler
import it.scoppelletti.spaceship.html.ContentHandlerTagHandler
import it.scoppelletti.spaceship.html.HtmlExt
import it.scoppelletti.spaceship.html.HtmlTagHandler
import it.scoppelletti.spaceship.html.ResourceTagHandler
import it.scoppelletti.spaceship.html.lifecycle.HtmlViewerViewModel
import it.scoppelletti.spaceship.inject.ContextModule
import it.scoppelletti.spaceship.inject.UIModule
import it.scoppelletti.spaceship.inject.ViewModelKey
import javax.inject.Named

/**
 * Defines the dependencies provided by this library.
 *
 * @since 1.0.0
 */
@Module(includes = [ ContextModule::class, UIModule::class ])
public abstract class HtmlModule {

    @Binds
    @IntoMap
    @StringKey(ApplicationLabelTagHandler.TAG)
    public abstract fun bindApplicationLabelHtmlTagHandler(
            handler: ApplicationLabelTagHandler
    ): HtmlTagHandler

    @Binds
    @IntoMap
    @StringKey(ApplicationVersionTagHandler.TAG)
    public abstract fun bindApplicationVersionHtmlTagHandler(
            handler: ApplicationVersionTagHandler
    ): HtmlTagHandler

    @Binds
    @IntoMap
    @StringKey(ContentHandlerTagHandler.TAG)
    public abstract fun bindContentHandlerTagHandler(
            handler: ContentHandlerTagHandler
    ): HtmlTagHandler

    @Binds
    @IntoMap
    @StringKey(ResourceTagHandler.TAG)
    public abstract fun bindResourceTagHandler(
            handler: ResourceTagHandler
    ): HtmlTagHandler

    @Binds
    @Named(HtmlExt.DEP_TAGHANDLER)
    public abstract fun bindHtmlTagHandler(
            handler: InjectHtmlTagHandler
    ): Html.TagHandler

    @Binds
    @IntoMap
    @ViewModelKey(HtmlViewerViewModel::class)
    public abstract fun bindHtmlViewerViewModel(
            viewModel: HtmlViewerViewModel
    ): ViewModel
}
