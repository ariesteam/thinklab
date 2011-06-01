package org.integratedmodelling.modelling.loader;

import java.io.File;
import java.util.Properties;

import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IResourceLoader;

public class ModelResourceLoader implements IResourceLoader {
	
	@Override
	public void load(Properties properties, File dir)
			throws ThinklabException {
		
		File fmod = new File(dir + File.separator + "models");
		if (fmod.exists()) {
			ModelFactory.get().loadModelFiles(fmod);
		}
		fmod = new File(dir + File.separator + "contexts");
		if (fmod.exists()) {
			ModelFactory.get().loadModelFiles(fmod);
		}
		fmod = new File(dir + File.separator + "agents");
		if (fmod.exists()) {
			ModelFactory.get().loadModelFiles(fmod);
		}
		fmod = new File(dir + File.separator + "storylines");
		if (fmod.exists()) {
			StorylineFactory.addSourceDirectory(fmod);
		}
	}

	@Override
	public void unload(Properties properties, File loadDir) {
		// TODO Auto-generated method stub
		
	}
}
