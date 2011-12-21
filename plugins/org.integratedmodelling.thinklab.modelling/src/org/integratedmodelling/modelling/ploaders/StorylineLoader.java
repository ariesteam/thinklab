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
