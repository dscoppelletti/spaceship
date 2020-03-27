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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Nonnull;
import it.scoppelletti.spaceship.gradle.model.Developer;
import it.scoppelletti.spaceship.gradle.model.License;
import it.scoppelletti.spaceship.gradle.model.SpaceshipExtension;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.gradle.api.publish.maven.MavenPomLicense;
import org.gradle.api.publish.maven.MavenPomScm;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.tooling.BuildException;
import org.jetbrains.dokka.gradle.DokkaTask;

/**
 * Tools for implementing plug-ins.
 *
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class PlatformTools {
    private static final String CACHE_DEFAULT = "default";
    private static final String CLASSIFIER_JAVADOC = "javadoc";
    private static final String CLASSIFIER_SOURCES = "sources";
    private static final String FORMAT_HTML = "html";
    private static final String PLATFORM_JVM = "JVM";
    private static final String PROTOCOL_SCM = "scm:git:";
    private final Project myProject;
    private final String myPackaging;
    private final TaskNames myTaskNames;
    private final BasePluginConvention myConvention;
    private final SpaceshipExtension mySpaceshipExt;
    private final PublishingExtension myPublishExt;

    /**
     * Constructor.
     *
     * @param project   Project.
     * @param taskNames Provides name and description of the tasks.
     */
    protected PlatformTools(@Nonnull Project project,
            @Nonnull String packaging,
            @Nonnull TaskNames taskNames) {
        myProject = Objects.requireNonNull(project,
                "Argument project is null.");

        if (StringUtils.isBlank(packaging)) {
            throw new NullPointerException("Argument packaging is null.");
        }

        myPackaging = packaging;
        myTaskNames = Objects.requireNonNull(taskNames,
                "Argument taskNames is null.");

        myConvention = myProject.getConvention().getPlugin(
                BasePluginConvention.class);
        mySpaceshipExt = Objects.requireNonNull(
                myProject.getExtensions().findByType(SpaceshipExtension.class),
                () -> String.format("Extension %1$s not found.",
                        SpaceshipExtension.class));
        myPublishExt = Objects.requireNonNull(
                myProject.getExtensions().findByType(PublishingExtension.class),
                () -> String.format("Extension %1$s not found.",
                        PublishingExtension.class));
    }

    /**
     * Defines the task {@code Copy} for generating the {@code META-INF} files.
     *
     * @param  intoDir Path of the {@code META-INF} folder.
     * @return         The new object.
     */
    @Nonnull
    protected final Copy doGenerateMetainf(@Nonnull Path intoDir) {
        Path fromDir;
        Copy metainfTask;

        Objects.requireNonNull(intoDir, "Argument intoDir is null.");

        fromDir = myProject.getRootDir().toPath().getParent();
        metainfTask = myProject.getTasks().create(
                myTaskNames.getGenerateMetainfName(), Copy.class);
        metainfTask.setDescription(myTaskNames.getGenerateMetainfDescription());
        metainfTask.setGroup(BasePlugin.BUILD_GROUP);

        metainfTask.from(fromDir.toFile())
                .into(intoDir.toFile())
                .include("LICENSE")
                .rename("LICENSE", "LICENSE.txt");

        metainfTask.doLast(task -> writeNotice(intoDir.resolve("NOTICE.txt")));
        return metainfTask;
    }

    /**
     * Writes the file {@code NOTICE.txt}.
     *
     * @param file Path of the file.
     */
    private void writeNotice(Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.append(mySpaceshipExt.getDescription());
            writer.newLine();
            writer.append("Copyright(C) ");
            writer.append(mySpaceshipExt.getInceptionYear());
            writer.append(' ');
            writer.append(mySpaceshipExt.getDeveloper().getName());
            writer.append(", <");
            writer.append(mySpaceshipExt.getDeveloper().getUrl());
            writer.append("/>");
            writer.newLine();
        } catch (IOException ex) {
            throw new BuildException(String.format("Failed to write %1$s.",
                    file), ex);
        }
    }

    /**
     * Defines the task {@code Jar} for packaging the source code files.
     *
     * @param  source Collection of the source code folders.
     * @return        The new object.
     */
    @Nonnull
    protected final Jar doPackageSources(@Nonnull FileCollection source) {
        Jar jarTask;
        Copy metainfTask;

        Objects.requireNonNull(source, "Argument source is null.");

        metainfTask = (Copy) Objects.requireNonNull(
                myProject.getTasks().findByName(
                        myTaskNames.getGenerateMetainfName()),
                () -> String.format("Task %1$s not found.",
                        myTaskNames.getGenerateMetainfName()));

        jarTask = myProject.getTasks().create(
                myTaskNames.getPackageSourcesName(), Jar.class);
        jarTask.setDescription(myTaskNames.getPackageSourcesDescription());
        jarTask.setGroup(PublishingPlugin.PUBLISH_TASK_GROUP);

        //noinspection deprecation
        jarTask.setClassifier(PlatformTools.CLASSIFIER_SOURCES);

        jarTask.from(source);

        //noinspection UnstableApiUsage
        jarTask.metaInf(spec -> spec.from(metainfTask.getDestinationDir()));

        return jarTask;
    }

    /**
     * Defines the task {@code DokkaTask} for generating the KDoc documentation.
     *
     * @return The new object.
     */
    @Nonnull
    protected final DokkaTask doGenerateKDoc() {
        Path outDir, readmeFile;
        DokkaTask kdocTask;

        outDir = myProject.getBuildDir().toPath()
                .resolve("docs")
                .resolve("kdoc");
        readmeFile = myProject.getRootDir().toPath()
                .resolve("README.md");

        kdocTask = myProject.getTasks().create(
                myTaskNames.getGenerateKDocName(), DokkaTask.class);
        kdocTask.setDescription(myTaskNames.getGenerateKDocDescription());
        kdocTask.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);

        // http://github.com/Kotlin/dokka, README.md
        kdocTask.setOutputFormat(PlatformTools.FORMAT_HTML);
        kdocTask.setOutputDirectory(outDir.toString());
        kdocTask.setCacheRoot(PlatformTools.CACHE_DEFAULT);

        kdocTask.configuration(config -> {
            config.setModuleName(myConvention.getArchivesBaseName());
            config.setIncludeNonPublic(false);
            config.setSkipDeprecated(true);
            config.setReportUndocumented(false);
            config.setSkipEmptyPackages(true);
            config.setTargets(Collections.singletonList(
                    PlatformTools.PLATFORM_JVM));
            config.setPlatform(PlatformTools.PLATFORM_JVM);
//            config.setClasspath(...);
//            config.setSourceRoots(...);
            config.setIncludes(
                    Collections.singletonList(readmeFile.toString()));
            config.setJdkVersion(8);
            config.setNoStdlibLink(true);
            config.setNoJdkLink(true);
        });

        return kdocTask;
    }

    /**
     * Defines the task {@code Jar} for packaging the KDoc documentation.
     */
    public final void packageKDoc() {
        DokkaTask kdocTask;
        Jar jarTask;

        kdocTask = (DokkaTask) Objects.requireNonNull(
                myProject.getTasks().findByName(
                        myTaskNames.getGenerateKDocName()),
                () -> String.format("Task %1$s not found.",
                        myTaskNames.getGenerateKDocName()));

        jarTask = myProject.getTasks().create(myTaskNames.getPackageKDocName(),
                Jar.class);
        jarTask.setDescription(myTaskNames.getPackageKDocDescription());
        jarTask.setGroup(PublishingPlugin.PUBLISH_TASK_GROUP);

        //noinspection deprecation
        jarTask.setClassifier(PlatformTools.CLASSIFIER_JAVADOC);

        jarTask.from(kdocTask.getOutputDirectoryAsFile());
        jarTask.dependsOn(kdocTask);
    }

    /**
     * Defines the Maven publication.
     *
     * @return The new object.
     */
    @Nonnull
    protected final MavenPublication doPublish() {
        Task kdocTask;
        MavenPublication publ;
        BasePluginConvention convention;

        publ = myPublishExt.getPublications().create(
                myTaskNames.getPublishName(), MavenPublication.class);

        publ.artifact(Objects.requireNonNull(
                myProject.getTasks().findByName(
                        myTaskNames.getPackageSourcesName()), () ->
                        String.format("Task %1$s not found.",
                                myTaskNames.getPackageSourcesName())));

        kdocTask = myProject.getTasks().findByName(
                myTaskNames.getPackageKDocName());
        if (kdocTask != null) {
            publ.artifact(kdocTask);
        }

        publ.setArtifactId(myConvention.getArchivesBaseName());
        publ.pom(this::configurePom);

        return publ;
    }

    /**
     * Configures the POM.
     *
     * @param pom POM.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void configurePom(@Nonnull MavenPom pom) {
        pom.getDescription().set(mySpaceshipExt.getDescription());
        pom.setPackaging(myPackaging);
        pom.getUrl().set(mySpaceshipExt.getUrl());
        pom.getInceptionYear().set(mySpaceshipExt.getInceptionYear());

        pom.developers(developers -> developers.developer(
                dev -> configureDeveloper(dev, mySpaceshipExt.getDeveloper())));

        if (mySpaceshipExt.getLicense() != null) {
            pom.licenses(spec -> spec.license(lic -> configureLicense(lic,
                    mySpaceshipExt.getLicense())));
        }

        pom.scm(this::configureScm);
    }

    /**
     * Configures the POM element {@code developer}.
     *
     * @param pom       POM.
     * @param developer Developer.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void configureDeveloper(@Nonnull MavenPomDeveloper pom,
            Developer developer) {
        pom.getName().set(developer.getName());
        pom.getEmail().set(developer.getEmail());
        pom.getUrl().set(developer.getUrl());
    }

    /**
     * Configures the POM element {@code license}.
     *
     * @param pom     POM.
     * @param license License.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void configureLicense(@Nonnull MavenPomLicense pom,
            @Nonnull License license) {
        pom.getName().set(license.getName());
        pom.getUrl().set(license.getUrl());
    }

    /**
     * Configures the POM element {@code scm}.
     *
     * @param pom POM.
     */
    @SuppressWarnings("UnstableApiUsage")
    private void configureScm(@Nonnull MavenPomScm pom) {
        String scm;

        scm = PlatformTools.PROTOCOL_SCM.concat(mySpaceshipExt.getScmUrl());
        pom.getConnection().set(scm);
        pom.getDeveloperConnection().set(scm);
        pom.getUrl().set(mySpaceshipExt.getScmUrl());
    }
}
