package org.integratedmodelling.thinklab.rest.resources;

import java.io.File;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.project.ThinklabProject;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.utils.FolderZiper;
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
