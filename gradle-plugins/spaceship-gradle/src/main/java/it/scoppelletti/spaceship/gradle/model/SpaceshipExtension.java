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

package it.scoppelletti.spaceship.gradle.model;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;

/**
 * Extension object for the plug-in {@code SpaceshipPlugin}.
 *
 * @see   it.scoppelletti.spaceship.gradle.SpaceshipPlugin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class SpaceshipExtension {

    /**
     * Name of this extension object.
     */
    public static final String NAME = "spaceship";

    private final Project myProject;

    /**
     * Developer.
     */
    @Getter
    private final Developer myDeveloper = new Developer();

    /**
     * License.
     */
    @Getter
    @Setter
    private License myLicense = new License();

    /**
     * URL of the project.
     */
    @Getter
    @Setter
    private String myUrl = "http://github.com/dscoppelletti/spaceship";

    /**
     * URL of the source control management.
     */
    @Getter
    @Setter
    private String myScmUrl = "git@github.com:dscoppelletti/spaceship.git";

    /**
     * Inception year.
     */
    @Getter
    @Setter
    private String myInceptionYear;

    /**
     * Constructor.
     *
     * @param project Project.
     */
    public SpaceshipExtension(@Nonnull Project project) {
        myProject = Objects.requireNonNull(project,
                "Argument projects is null.");
    }

    /**
     * Gets the project description.
     *
     * @return Value.
     */
    public String getDescription() {
        if (StringUtils.isNotBlank(myProject.getDescription())) {
            return myProject.getDescription();
        }

        return myProject.getName();
    }
}
