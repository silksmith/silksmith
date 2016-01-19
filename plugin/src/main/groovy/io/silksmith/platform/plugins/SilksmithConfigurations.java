/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.silksmith.platform.plugins;

import com.google.common.collect.ImmutableSet;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.internal.file.collections.LazilyInitializedFileCollection;
import org.gradle.api.internal.file.collections.SimpleFileCollection;

import java.io.File;


public class SilksmithConfigurations {
    
    public static final String COMPILE_CONFIGURATION = "silk";
    
    public static final String TEST_COMPILE_CONFIGURATION = "silkTest";
    public static final String DEV_COMPILE_CONFIGURATION = "silkDev";

    private final ConfigurationContainer configurations;
    private final DependencyHandler dependencyHandler;

    public SilksmithConfigurations(ConfigurationContainer configurations, DependencyHandler dependencyHandler) {
        this.configurations = configurations;
        this.dependencyHandler = dependencyHandler;
        

        Configuration silkCompile = configurations.create(COMPILE_CONFIGURATION);
        

        Configuration silkDev = configurations.create(DEV_COMPILE_CONFIGURATION);
        silkDev.extendsFrom(silkCompile);

        Configuration silkTestCompile = configurations.create(TEST_COMPILE_CONFIGURATION);
        
        silkTestCompile.extendsFrom(silkDev);

        configurations.maybeCreate(Dependency.DEFAULT_CONFIGURATION).extendsFrom(silkCompile);
    }

    

    public SilkConfiguration getSilk() {
        return new SilkConfiguration(COMPILE_CONFIGURATION);
    }

    public SilkConfiguration getSilkDev() {
        return new SilkConfiguration(DEV_COMPILE_CONFIGURATION);
    }

    public SilkConfiguration getSilkTest() {
        return new SilkConfiguration(TEST_COMPILE_CONFIGURATION);
    }

    
    class SilkConfiguration {
        private final String name;

        SilkConfiguration(String name) {
            this.name = name;
        }

        private Configuration getConfiguration() {
            return configurations.getByName(name);
        }

        FileCollection getAllArtifacts() {
            return getConfiguration();
        }

        FileCollection getChangingArtifacts() {
            return new FilterByProjectComponentTypeFileCollection(getConfiguration(), true);
        }

        FileCollection getNonChangingArtifacts() {
            return new FilterByProjectComponentTypeFileCollection(getConfiguration(), false);
        }

        void addDependency(Object notation) {
            dependencyHandler.add(name, notation);
        }

        void addArtifact(PublishArtifact artifact) {
            configurations.getByName(name).getArtifacts().add(artifact);
        }
    }

    private static class FilterByProjectComponentTypeFileCollection extends LazilyInitializedFileCollection {
        private final Configuration configuration;
        private final boolean matchProjectComponents;

        private FilterByProjectComponentTypeFileCollection(Configuration configuration, boolean matchProjectComponents) {
            this.configuration = configuration;
            this.matchProjectComponents = matchProjectComponents;
        }

        @Override
        public FileCollectionInternal createDelegate() {
            ImmutableSet.Builder<File> files = ImmutableSet.builder();
            for (ResolvedArtifact artifact : configuration.getResolvedConfiguration().getResolvedArtifacts()) {
                if ((artifact.getId().getComponentIdentifier() instanceof ProjectComponentIdentifier) == matchProjectComponents) {
                    files.add(artifact.getFile());
                }
            }
            return new SimpleFileCollection(files.build());
        }
    }
}
