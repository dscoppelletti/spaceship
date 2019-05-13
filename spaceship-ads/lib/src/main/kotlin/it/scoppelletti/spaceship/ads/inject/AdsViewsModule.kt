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

package it.scoppelletti.spaceship.ads.inject

import dagger.Module
import dagger.android.ContributesAndroidInjector
import it.scoppelletti.spaceship.ads.app.ConsentAgeFragment
import it.scoppelletti.spaceship.ads.app.ConsentPromptFragment
import it.scoppelletti.spaceship.ads.app.ConsentPrivacyFragment
import it.scoppelletti.spaceship.ads.app.ConsentReminderFragment
import it.scoppelletti.spaceship.ads.app.ConsentUnderageFragment

/**
 * Defines the views exported by this library.
 *
 * @since 1.0.0
 */
@Module
public abstract class AdsViewsModule {

    @ContributesAndroidInjector
    public abstract fun contributeConsentAgeFragment(): ConsentAgeFragment

    @ContributesAndroidInjector
    public abstract fun contributeConsentUnderageFragment(
    ): ConsentUnderageFragment

    @ContributesAndroidInjector
    public abstract fun contributeConsentPromptFragment(): ConsentPromptFragment

    @ContributesAndroidInjector
    public abstract fun contributeConsentPrivacyFragment(
    ): ConsentPrivacyFragment

    @ContributesAndroidInjector
    public abstract fun contributeConsentReminderFragment(
    ): ConsentReminderFragment
}