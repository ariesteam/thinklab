package org.integratedmodelling.modelling.ploaders;

import java.io.File;
import java.util.Collection;

import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ProjectLoader;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;

@ProjectLoader(folder="annotations")
public class AnnotationLoader implements IProjectLoader {

	Collection<String> _namespaces;

	@Override
	public void load(File directory) throws ThinklabException {
		_namespaces = ModelFactory.get().loadModelFiles(directory);
	}

	@Override
	public void unload(File directory) throws ThinklabException {
		for (String ns : _namespaces) {
			ModelFactory.get().releaseNamespace(ns);
		}
	}

}
