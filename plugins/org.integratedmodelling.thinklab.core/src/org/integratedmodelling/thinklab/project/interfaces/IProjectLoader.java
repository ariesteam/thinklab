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
package org.integratedmodelling.thinklab.project.interfaces;

import java.io.File;

import org.integratedmodelling.exceptions.ThinklabException;

/**
 * Each resource loader installed will be passed the parsed content of the
 * THINKLAB-INF/thinklab.properties file (empty if there is no such file) as well as 
 * the plugin load directory, to do what it needs to do with. Resource loaders are called after 
 * every standard resource (such as ontologies) has been installed, but just before doStart() is invoked.
 * 
 * @author ferdinando.villa
 *
 */
public interface IProjectLoader {

	/**
	 * Handle resources in the given directory
	 * 
	 * @param directory
	 * @throws ThinklabException
	 */
	void load(File directory) throws ThinklabException;

	/**
	 * Cleanup resources coming from given directory
	 * 
	 * @param directory
	 * @throws ThinklabException
	 */
	void unload(File directory) throws ThinklabException;

}
