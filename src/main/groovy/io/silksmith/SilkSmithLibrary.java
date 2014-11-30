package io.silksmith;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.Usage;

public class SilkSmithLibrary implements SoftwareComponentInternal {
	private final Usage runtimeUsage = new RuntimeUsage();
	private final LinkedHashSet<PublishArtifact> artifacts = new LinkedHashSet<PublishArtifact>();
	private final DependencySet runtimeDependencies;

	public SilkSmithLibrary(PublishArtifact webcraftArtifact,
			DependencySet runtimeDependencies) {
		artifacts.add(webcraftArtifact);
		this.runtimeDependencies = runtimeDependencies;
	}

	public String getName() {
		return "silk";
	}

	public Set<Usage> getUsages() {
		return Collections.singleton(runtimeUsage);
	}

	private class RuntimeUsage implements Usage {
		public String getName() {
			return "runtime";
		}

		public Set<PublishArtifact> getArtifacts() {
			return artifacts;
		}

		public Set<ModuleDependency> getDependencies() {
			return runtimeDependencies.withType(ModuleDependency.class);
		}
	}
}