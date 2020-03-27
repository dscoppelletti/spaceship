/*
 * Copyright (C) 2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.android;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Configuration for dependencies.
 */
@ToString
@RequiredArgsConstructor
final class DependencyConfig {

    /**
     * Gets the configuration name.
     */
    @Getter
    @Nonnull
    private final String myName;

    /**
     * Gets the dependencies type.
     */
    @Getter
    @Nullable
    private final String myType;

    /**
     * Gets the dependencies scope.
     */
    @Getter
    @Nonnull
    private final String myScope;
}
