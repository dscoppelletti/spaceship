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

/**
 * Name of the POM elements that defines a dependency.
 */
final class MavenXml {
    static final String NODE_ARTIFACTID = "artifactId";
    static final String NODE_CLASSIFIER = "classifier";
    static final String NODE_GROUPID = "groupId";
    static final String NODE_DEPENDENCIES = "dependencies";
    static final String NODE_DEPENDENCY = "dependency";
    static final String NODE_DEPENDENCYMANAGENENT = "dependencyManagement";
    static final String NODE_SCOPE = "scope";
    static final String NODE_TYPE = "type";
    static final String NODE_VERSION = "version";

    /**
     * Private constructor for static class.
     */
    private MavenXml() {
    }
}
