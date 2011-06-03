package org.integratedmodelling.modelling.ploaders;

import java.io.File;
import java.util.ArrayList;

import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ProjectLoader;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;

@ProjectLoader(folder="storylines")
public class StorylineLoader implements IProjectLoader {

	ArrayList<File> _dirs = new ArrayList<File>();
	
	@Override
	public void load(File directory) throws ThinklabException {
		StorylineFactory.addSourceDirectory(directory);
		_dirs.add(directory);
	}

	@Override
	public void unload(File directory) throws ThinklabException {
		for (File dir : _dirs) {
			StorylineFactory.removeSourceDirectory(dir);
		}
	}

}
