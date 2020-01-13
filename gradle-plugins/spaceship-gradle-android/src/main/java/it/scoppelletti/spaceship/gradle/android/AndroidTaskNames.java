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

package it.scoppelletti.spaceship.gradle.android;

import java.util.Objects;
import javax.annotation.Nonnull;
import com.android.build.gradle.api.LibraryVariant;
import it.scoppelletti.spaceship.gradle.TaskNames;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Provides name and description of the tasks.
 */
final class AndroidTaskNames implements TaskNames {

    /**
     * Name of the assemble task.
     */
    static final String ASSEMBLE = "assembleRelease";

    private final LibraryVariant myVariant;

    /**
     * Name of the task for generating the source code files.
     */
    @Getter
    private final String myGenerateSourcesName;

    /**
     * Name of the task for generating the resource files.
     */
    @Getter
    private final String myGenerateResourcesName;

    private final String myGenerateMetainf;
    private final String myPackageSources;
    private final String myGenerateKDoc;
    private final String myPackageKDoc;

    /**
     * Name of the task for packaging the library.
     */
    @Getter
    private final String myPackageLibraryName;

    /**
     * Constructor.
     *
     * @param variant Variant.
     */
    AndroidTaskNames(@Nonnull LibraryVariant variant) {
        String varName;

        myVariant = Objects.requireNonNull(variant,
                "Argument variant is null.");

        varName = StringUtils.capitalize(variant.getName());
        myGenerateSourcesName = "generate".concat(varName).concat("Sources");
        myGenerateResourcesName = "generate".concat(varName).concat("Resources");
        myGenerateMetainf = "generate".concat(varName).concat("Metainf");
        myPackageSources = "package".concat(varName).concat("Sources");
        myGenerateKDoc = "generate".concat(varName).concat("KDoc");
        myPackageKDoc = "package".concat(varName).concat("KDoc");
        myPackageLibraryName = "bundle".concat(varName).concat("Aar");
    }

    @Nonnull
    @Override
    public String getGenerateMetainfName() {
        return myGenerateMetainf;
    }

    @Nonnull
    @Override
    public String getGenerateMetainfDescription() {
        return String.format("Generate META-INF for %1$s.",
                myVariant.getName());
    }

    @Nonnull
    @Override
    public String getPackageSourcesName() {
        return myPackageSources;
    }

    @Nonnull
    @Override
    public String getPackageSourcesDescription() {
        return String.format("Package sources for %1$s.", myVariant.getName());
    }

    @Nonnull
    @Override
    public String getGenerateKDocName() {
        return myGenerateKDoc;
    }

    @Nonnull
    @Override
    public String getGenerateKDocDescription() {
        return String.format("Generate KDoc for %1$s.", myVariant.getName());
    }

    @Nonnull
    @Override
    public String getPackageKDocName() {
        return myPackageKDoc;
    }

    @Nonnull
    @Override
    public String getPackageKDocDescription() {
        return String.format("Package KDoc for %1$s.", myVariant.getName());
    }

    @Nonnull
    @Override
    public String getPublishName() {
        return myVariant.getName();
    }
}
