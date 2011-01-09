package org.integratedmodelling.modelling.loader;

import java.io.File;
import java.util.Properties;

import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IResourceLoader;

public class ModelResourceLoader implements IResourceLoader {
	
	@Override
	public void load(Properties properties, File dir)
			throws ThinklabException {
		
		File fmod = new File(dir + File.separator + "models");
		if (fmod.exists())
			ModelFactory.get().loadModelFiles(fmod);
		
		fmod = new File(dir + File.separator + "contexts");
		if (fmod.exists())
			ModelFactory.get().loadModelFiles(new File(dir + File.separator + "contexts"));
		
		fmod = new File(dir + File.separator + "agents");
		if (fmod.exists())
			ModelFactory.get().loadModelFiles(new File(dir + File.separator + "agents"));
		
		fmod = new File(dir + File.separator + "storylines");
		if (fmod.exists())
			ModelFactory.get().loadStorylines(new File(dir + File.separator + "storylines"));
	}
}
