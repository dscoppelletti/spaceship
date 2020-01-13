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
import it.scoppelletti.spaceship.gradle.model.SpaceshipExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Plug-in for Spaceship projects.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SpaceshipPlugin implements Plugin<Project> {

    /**
     * Property containing the URL of the development Maven repository.
     */
    public static final String PROP_DEVREPOURL =
            "it.scoppelletti.tools.devRepoUrl";

    /**
     * Name of the development Maven repository.
     */
    public static final String REPO_DEV = "dev";

    @Override
    public void apply(@Nonnull Project project) {
        project.getExtensions().create(SpaceshipExtension.NAME,
                SpaceshipExtension.class, project);
    }
}
