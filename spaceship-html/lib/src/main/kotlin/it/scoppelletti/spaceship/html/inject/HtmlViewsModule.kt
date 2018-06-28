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
 * limit
 */

package it.scoppelletti.spaceship.html.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.html.app.HtmlViewerActivity

/**
 * Defines the views exported by this library.
 *
 * @since 1.0.0
 */
@Module
public abstract class HtmlViewsModule {

    @ContributesAndroidInjector(modules = [])
    public abstract fun contributeHtmlViewerActivity(): HtmlViewerActivity
}