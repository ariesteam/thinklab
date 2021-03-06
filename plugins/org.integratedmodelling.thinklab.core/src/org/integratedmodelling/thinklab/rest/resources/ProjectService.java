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
package org.integratedmodelling.thinklab.rest.resources;

import java.io.File;

import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.project.ThinklabProject;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.Pair;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Manages thinklab projects at the server side (deploy, undeploy, update) using 
 * archive files sent by the client.
 * 
 * @author ferdinando.villa
 *
 */
public class ProjectService extends DefaultRESTHandler {

	@Get
	public Representation service() {

		try {
			
			if (!checkPrivileges("user:Administrator"))
				return wrap();
			
			String cmd = getArgument("cmd");
			String pluginId = getArgument("plugin");
			
			if (cmd.equals("deploy")) {

				File archive = this.getFileForHandle(getArgument("handle"), true);
				ThinklabProject.deploy(archive, pluginId, true);
				
			} else if (cmd.equals("undeploy")) {

				ThinklabProject.undeploy(pluginId);

			} else if (cmd.equals("pack")) {
				
				/*
				 * make an archive from the project and return the handle
				 */
				ThinklabProject tp = ThinklabProject.getProject(pluginId);
				if (tp == null)
					throw new ThinklabResourceNotFoundException("project " + pluginId + " does not exist");

				Pair<File, String> fname = this.getFileName("project.zip", getSession());
				FolderZiper.zipFolder(tp.getPath().toString(), fname.getFirst().toString());
				put("handle", fname.getSecond());
			}
			
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}



}
