/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
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
