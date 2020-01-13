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

package it.scoppelletti.spaceship.gradle;

import javax.annotation.Nonnull;

/**
 * Provides name and description of the tasks.
 *
 * @since 1.0.0
 */
public interface TaskNames {

    /**
     * Gets the name of the task for generating the {@code META-INF} files.
     *
     * @return Value.
     */
    @Nonnull
    default String getGenerateMetainfName() {
        return "generateMetainf";
    }

    /**
     * Gets the description of the task for generating the {@code META-INF}
     * files.
     *
     * @return Value.
     */
    @Nonnull
    default String getGenerateMetainfDescription() {
        return "Generate META-INF.";
    }

    /**
     * Gets the name of the task for packaging the source code files.
     *
     * @return Value.
     */
    @Nonnull
    default String getPackageSourcesName() {
        return "packageSources";
    }

    /**
     * Gets the description of the task for packaging the source code files.
     *
     * @return Value.
     */
    @Nonnull
    default String getPackageSourcesDescription() {
        return "Package sources.";
    }

    /**
     * Gets the name of the task for generating the KDoc documentation.
     *
     * @return Value
     */
    @Nonnull
    default String getGenerateKDocName() {
        return "generateKDoc";
    }

    /**
     * Gets the description of the task for generating the KDoc documentation.
     *
     * @return Value.
     */
    @Nonnull
    default String getGenerateKDocDescription() {
        return "Generate KDoc.";
    }

    /**
     * Gets the name of the task for packaging the KDoc documentation.
     *
     * @return Value
     */
    @Nonnull
    default String getPackageKDocName() {
        return "packageKDoc";
    }

    /**
     * Gets the description of the task for packaging the KDoc documentation.
     *
     * @return Value.
     */
    @Nonnull
    default String getPackageKDocDescription() {
        return "Package KDoc.";
    }

    /**
     * Gets the name of the publication.
     *
     * @return Value.
     */
    @Nonnull
    String getPublishName();
}
