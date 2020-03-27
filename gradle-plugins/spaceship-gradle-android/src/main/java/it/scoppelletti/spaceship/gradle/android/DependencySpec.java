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

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.artifacts.DependencyArtifact;
import org.gradle.api.artifacts.ExternalDependency;

/**
 * Dependency specification.
 */
@ToString
@EqualsAndHashCode
final class DependencySpec {

    /**
     * Gets the group ID.
     */
    @Getter
    @Nonnull
    private final String myGroupId;

    /**
     * Gets the artifact ID.
     */
    @Getter
    @Nonnull
    private final String myArtifactId;

    /**
     * Gets the version.
     */
    @Getter
    @Nullable
    private final String myVersion;

    /**
     * Gets the classifier.
     */
    @Getter
    @Nullable
    private final String myClassifier;

    /**
     * Gets the type.
     */
    @Getter
    @Nullable
    @EqualsAndHashCode.Exclude
    private final String myType;

    /**
     * Gets the scope.
     */
    @Getter
    @Nonnull
    @EqualsAndHashCode.Exclude
    private final String myScope;

    /**
     * Constructor.
     *
     * @param source Dependency.
     * @param scope  Scope.
     */
    public DependencySpec(@Nonnull ExternalDependency source,
            @Nullable String type, @Nonnull String scope) {
        Objects.requireNonNull(source, "Argument source is null.");
        if (StringUtils.isBlank(scope)) {
            throw new NullPointerException("Argument scope is null.");
        }

        myGroupId = Objects.requireNonNull(source.getGroup(),
            "Argument source.group is null.");
        myArtifactId = source.getName();
        myVersion = source.getVersion();
        myClassifier = DependencySpec.initClassifier(source);
        myType = type;
        myScope = scope;
    }

    /**
     * Gets the classifier of a dependency.
     *
     * @param  source Dependency.
     * @return        Classifier.
     */
    @Nullable
    private static String initClassifier(ExternalDependency source) {
        for (DependencyArtifact artifact : source.getArtifacts()) {
            // If the build script specifies a classifier, the dependency
            // includes an artifact with that classifier.
            return artifact.getClassifier();
        }

        return null;
    }
}
