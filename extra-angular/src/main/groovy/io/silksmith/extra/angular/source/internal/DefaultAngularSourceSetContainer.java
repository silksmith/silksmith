package io.silksmith.extra.angular.source.internal;

import org.gradle.api.Project;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.reflect.Instantiator;

import io.silksmith.extra.angular.source.AngularSourceSet;
import io.silksmith.extra.angular.source.AngularSourceSetContainer;


public class DefaultAngularSourceSetContainer extends
		AbstractNamedDomainObjectContainer<AngularSourceSet> implements
		AngularSourceSetContainer {

	private final Project project;
	private final Instantiator instantiator;
	private final FileResolver fileResolver;

	public DefaultAngularSourceSetContainer(Project project,
			Instantiator instantiator, FileResolver fileResolver) {
		super(AngularSourceSet.class, instantiator);
		this.project = project;
		this.instantiator = instantiator;
		this.fileResolver = fileResolver;
	}

	@Override
	protected AngularSourceSet doCreate(String name) {
		return instantiator.newInstance(DefaultAngularSourceSet.class, name,
				project, instantiator, fileResolver);
	}
}
