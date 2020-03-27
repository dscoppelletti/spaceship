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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;
import com.android.builder.model.SourceProvider;
import groovy.util.Node;
import it.scoppelletti.spaceship.gradle.PlatformTools;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.XmlProvider;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.dokka.gradle.DokkaTask;

/**
 * Tools.
 */
final class AndroidTools extends PlatformTools {
    private static final String CONFIG_API = "ApiElements";
    private static final String CONFIG_BOM = "bom";
    private static final String CONFIG_RUNTIME = "RuntimeElements";
    private static final String DIR_TEST = "test";
    private static final String PACKAGING = "aar";
    private static final String SCOPE_COMPILE = "compile";
    private static final String SCOPE_IMPORT = "import";
    private static final String SCOPE_RUNTIME = "runtime";
    private static final String TYPE_POM = "pom";
    private static final Logger myLogger = Logging.getLogger(
            AndroidTools.class);
    private final Project myProject;
    private final LibraryVariant myVariant;
    private final AndroidTaskNames myTaskNames;

    /**
     * Constructor.
     *
     * @param project   Project.
     * @param variant   Variant.
     * @param taskNames Provides name and description of the tasks.
     */
    AndroidTools(@Nonnull Project project, @Nonnull LibraryVariant variant,
            @Nonnull AndroidTaskNames taskNames) {
        super(project, AndroidTools.PACKAGING, taskNames);

        myProject = project;
        myVariant = Objects.requireNonNull(variant,
                "Argument variant is null.");
        myTaskNames = taskNames;
    }

    /**
     * Defines the task {@code Copy} for generating the {@code META-INF} files.
     */
    void generateMetainf() {
        Path intoDir;
        Task resTask;
        Copy metainfTask;

        intoDir = myProject.getBuildDir().toPath()
                .resolve("intermediates")
                .resolve("java_res")
                .resolve(myVariant.getName())
                .resolve("out")
                .resolve("META-INF");

        metainfTask = doGenerateMetainf(intoDir);

        resTask = Objects.requireNonNull(myProject.getTasks().findByName(
                myTaskNames.getGenerateResourcesName()), () ->
                String.format("Task %1$s not found.",
                        myTaskNames.getGenerateResourcesName()));
        resTask.dependsOn(metainfTask);
    }

    /**
     * Defines the task {@code Jar} for packaging the source code files.
     */
    void packageSources() {
        Jar jarTask;
        FileCollection source;

        source = myProject.files();
        for (SourceProvider sourceSet : myVariant.getSourceSets()) {
            for (File file: sourceSet.getJavaDirectories()) {
                if (!file.getPath().contains(AndroidTools.DIR_TEST)) {
                    source = source.plus(myProject.files(file));
                }
            }
        }

        jarTask = doPackageSources(source);

        jarTask.dependsOn(myTaskNames.getGenerateSourcesName());
    }

    /**
     * Defines the task {@code DokkaTask} for generating the KDoc documentation.
     */
    void generateKDoc() {
        DokkaTask kdocTask;

        kdocTask = doGenerateKDoc();

        kdocTask.configuration(config -> {
            config.setAndroidVariants(
                    Collections.singletonList(myVariant.getName()));
            config.setNoAndroidSdkLink(true);
        });

        kdocTask.dependsOn(myTaskNames.getPackageLibraryName());
    }

    /**
     * Defines the Maven publication.
     */
    void publish() {
        String ver, verSuffix;
        ProductFlavor flavor;
        MavenPublication publ;

        publ = doPublish();
        publ.artifact(Objects.requireNonNull(
                myProject.getTasks().findByName(
                        myTaskNames.getPackageLibraryName()), () ->
                        String.format("Task %1$s not found.",
                                myTaskNames.getPackageLibraryName())));

        flavor = myVariant.getMergedFlavor();
        ver = flavor.getVersionName();
        verSuffix = flavor.getVersionNameSuffix();
        if (StringUtils.isNotBlank(verSuffix)) {
            ver = ver.concat(verSuffix);
        }

        publ.setVersion(ver);
        publ.pom(this::configurePom);
        publ.pom(pom -> pom.withXml(this::configureDependencies));
    }

    /**
     * Configures the POM.
     *
     * @param pom POM.
     */
    private void configurePom(@Nonnull MavenPom pom) {
        pom.setPackaging("aar");
    }

    /**
     * Configures the POM element {@code dependencies}.
     *
     * @param xml Provides XML access to the POM.
     */
    private void configureDependencies(@Nonnull XmlProvider xml) {
        // http://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph
        // The configurations "apiElements" and "runtimeElements" are the ones
        // used when a component compiles, or runs against the library, but the
        // same dependency can be included in both of the configurations.
        // Then I collect all the dependencies in the configuration
        // "apiElements", and I assign to them the scope "compile".
        // Next I collect only the dependencies in the configuration
        // "runtimeElements" which are not collected yet, and I assign to them
        // the scope "runtime".
        Node depManagementNode, depsNode, root;

        root = xml.asNode();
        depManagementNode = root.appendNode(MavenXml.NODE_DEPENDENCYMANAGENENT)
                .appendNode(MavenXml.NODE_DEPENDENCIES);
        depsNode = root.appendNode(MavenXml.NODE_DEPENDENCIES);

        listDeps().forEach(dep -> {
            Node depNode, parentNode;

            if (dep.getScope().equals(AndroidTools.SCOPE_IMPORT)) {
                parentNode = depManagementNode;
            } else {
                parentNode = depsNode;
            }

            depNode = parentNode.appendNode(MavenXml.NODE_DEPENDENCY);
            depNode.appendNode(MavenXml.NODE_GROUPID, dep.getGroupId());
            depNode.appendNode(MavenXml.NODE_ARTIFACTID, dep.getArtifactId());

            if (StringUtils.isNotBlank(dep.getVersion())) {
                depNode.appendNode(MavenXml.NODE_VERSION, dep.getVersion());
            }

            if (StringUtils.isNotBlank(dep.getClassifier())) {
                depNode.appendNode(MavenXml.NODE_CLASSIFIER,
                        dep.getClassifier());
            }

            if (StringUtils.isNotBlank(dep.getType())) {
                depNode.appendNode(MavenXml.NODE_TYPE, dep.getType());
            }

            depNode.appendNode(MavenXml.NODE_SCOPE, dep.getScope());
        });
    }

    /**
     * Lists the configurations for dependecies.
     *
     * @return Collection.
     */
    private @Nonnull List<DependencyConfig> listConfig() {
        String varName = myVariant.getName();
        ArrayList<DependencyConfig> list;

        list = new ArrayList<>(2);
        list.add(new DependencyConfig(AndroidTools.CONFIG_BOM,
                AndroidTools.TYPE_POM, AndroidTools.SCOPE_IMPORT));
        list.add(new DependencyConfig(varName.concat(AndroidTools.CONFIG_API),
                null, AndroidTools.SCOPE_COMPILE));
        list.add(new DependencyConfig(varName.concat(
                AndroidTools.CONFIG_RUNTIME), null,
                AndroidTools.SCOPE_RUNTIME));

        return list;
    }

    /**
     * Lists the dependencies.
     *
     * @return Collection.
     */
    private @Nonnull List<DependencySpec> listDeps() {
        Set<DependencySpec> deps;

        deps = new LinkedHashSet<>();
        listConfig().forEach(config -> addDeps(deps, config));

        return new ArrayList<>(deps);
    }

    /**
     * Collects the dependencies for a configuration.
     *
     * @param deps      Collection.
     * @param depConfig Configuration.
     */
    private void addDeps(Set<DependencySpec> deps,
            DependencyConfig depConfig) {
        Configuration config;

        myLogger.debug("Adding dependencies for {}.", depConfig);
        config = myProject.getConfigurations().getByName(depConfig.getName());
        config.getAllDependencies().withType(ExternalDependency.class)
                .forEach(dep -> {
                    DependencySpec depSpec;

                    depSpec = new DependencySpec(dep, depConfig.getType(),
                            depConfig.getScope());
                    if (deps.add(depSpec)) {
                        myLogger.debug("Added dependency {}.", depSpec);
                    } else {
                        myLogger.debug("Depedency {} not added.", depSpec);
                    }
                });
    }
}
