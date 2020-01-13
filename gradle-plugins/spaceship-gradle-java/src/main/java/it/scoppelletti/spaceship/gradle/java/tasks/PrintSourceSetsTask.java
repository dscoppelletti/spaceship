/*
 * Copyright (C) 2019-2020 Dario Scoppelletti, <http://www.scoppelletti.it/>.
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

package it.scoppelletti.spaceship.gradle.java.tasks;

import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

/**
 * Prints all the source sets.
 *
 * @see <a href="http://docs.gradle.org/current/userguide/building_java_projects.html#sec:java_source_sets"
 *      target="_top">Declaring your source files via source sets</a>
 * @since 1.0.0
 */
public class PrintSourceSetsTask extends DefaultTask {

    /**
     * Sole constructor.
     */
    public PrintSourceSetsTask() {
        setDescription("Print all the source sets.");
    }

    /**
     * Executes the task.
     */
    @TaskAction
    public void run() {
        Project project = getProject();
        JavaPluginConvention convention;

        convention = project.getConvention().getPlugin(
                JavaPluginConvention.class);
        for (SourceSet sources : convention.getSourceSets()) {
            System.out.printf("SourceSet(%1$s) {%n", sources.getName());
            for (File file : sources.getAllJava().getSourceDirectories()) {
                System.out.printf("\t%1$s%n", file);
            }
            System.out.println("}");
        }
    }
}
