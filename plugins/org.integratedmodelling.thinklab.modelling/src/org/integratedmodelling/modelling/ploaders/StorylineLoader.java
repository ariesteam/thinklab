package org.integratedmodelling.modelling.ploaders;

import java.io.File;

import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ProjectLoader;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;

@ProjectLoader(folder="storylines")
public class StorylineLoader implements IProjectLoader {

	@Override
	public void load(File directory) throws ThinklabException {
		StorylineFactory.addSourceDirectory(directory);
	}

	@Override
	public void unload(File directory) throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
