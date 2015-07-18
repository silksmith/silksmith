package io.silksmith.source.internal;

import io.silksmith.source.WebSourceSet;
import io.silksmith.source.WebSourceSetContainer;

import org.gradle.internal.reflect.Instantiator;

import org.gradle.api.Project;
import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.api.internal.file.FileResolver;

public class DefaultWebSourceSetContainer extends
		AbstractNamedDomainObjectContainer<WebSourceSet> implements
		WebSourceSetContainer {

	private final Project project;
	private final Instantiator instantiator;
	private final FileResolver fileResolver;

	public DefaultWebSourceSetContainer(Project project,
			Instantiator instantiator, FileResolver fileResolver) {
		super(WebSourceSet.class, instantiator);
		this.project = project;
		this.instantiator = instantiator;
		this.fileResolver = fileResolver;
	}

	@Override
	protected WebSourceSet doCreate(String name) {
		return instantiator.newInstance(DefaultWebSourceSet.class, name,
				project, instantiator, fileResolver);
	}

}
