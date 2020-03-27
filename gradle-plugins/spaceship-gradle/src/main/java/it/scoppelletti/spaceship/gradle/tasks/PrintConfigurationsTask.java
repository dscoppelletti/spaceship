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

package it.scoppelletti.spaceship.gradle.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.tasks.TaskAction;

/**
 * Prints the names of all configuration.
 *
 * @see <a href="http://docs.gradle.org/current/userguide/dependency_management_for_java_projects.html#sec:configurations_java_tutorial"
 *      target="_top">Using dependency configurations</a>
 * @since 1.0.0
 */
public class PrintConfigurationsTask extends DefaultTask {

    /**
     * Sole constructor.
     */
    public PrintConfigurationsTask() {
        setDescription("Print the names of all configurations.");
    }

    /**
     * Executes the task.
     */
    @TaskAction
    public void run() {
        Project project = getProject();

        for (Configuration config : project.getConfigurations()) {
            System.out.printf("Configuration(%1$s) {%n", config.getName());
            for (ExternalDependency dep : config.getAllDependencies()
                    .withType(ExternalDependency.class)) {
                //noinspection UnstableApiUsage
                System.out.printf("\tDependency(groupId=%1$s,artifactId=%2$s," +
                                "version=%3$s,reason=%4$s," +
                                "targetConfiguration=%5$s) {%n",
                        dep.getGroup(), dep.getName(),
                        dep.getVersion(), dep.getReason(),
                        dep.getTargetConfiguration());

                //noinspection UnstableApiUsage
                System.out.printf("\t\tVersionConstraint(branch=%1$s," +
                                "preferred=%2$s,required=%3$s,strict=%4$s)%n",
                    dep.getVersionConstraint().getBranch(),
                    dep.getVersionConstraint().getPreferredVersion(),
                    dep.getVersionConstraint().getRequiredVersion(),
                    dep.getVersionConstraint().getStrictVersion());

                for (DependencyArtifact artifact : dep.getArtifacts()) {
                    System.out.printf("\t\tArtifact(name=%1$s,type=%2$s," +
                                    "extension=%3$s,classifier=%4$s)%n",
                            artifact.getName(), artifact.getType(),
                            artifact.getExtension(), artifact.getClassifier());
                }

                System.out.printf("\t}%n");
            }

            System.out.println("}");
        }
    }
}

