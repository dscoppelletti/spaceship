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

package it.scoppelletti.spaceship.gradle.java;

import java.util.Objects;
import javax.annotation.Nonnull;
import it.scoppelletti.spaceship.gradle.TaskNames;
import org.gradle.api.Project;

/**
 * Provides name and description of the tasks.
 */
final class JavaTaskNames implements TaskNames {

    /**
     * Name of the assemble task.
     */
    static final String ASSEMBLE = "assemble";

    private final Project myProject;

    /**
     * Constructor.
     *
     * @param project Project.
     */
    JavaTaskNames(@Nonnull Project project) {
        myProject = Objects.requireNonNull(project,
                "Argument project is null.");
    }

    @Nonnull
    @Override
    public String getPublishName() {
        return StringExt.toCamelCase(myProject.getName());
    }
}
