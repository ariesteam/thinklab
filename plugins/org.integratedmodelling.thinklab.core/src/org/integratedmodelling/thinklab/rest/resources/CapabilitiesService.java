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

import java.util.Date;
import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.Version;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.project.ProjectFactory;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.java.plugin.registry.PluginDescriptor;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Return extensive server capabilities information. Does nothing at the moment.
 * 
 * @author ferdinando.villa
 *
 */
public class CapabilitiesService extends DefaultRESTHandler {

	@Get
	public Representation service() {
		
		JSONObject oret = new JSONObject();
		Runtime runtime = Runtime.getRuntime();
		try {

			boolean isAdmin = false;
			try {
				isAdmin = checkPrivileges("user:Administrator");
			} catch (ThinklabException e) {
			}
			
			/*
			 * same stuff as Ping
			 */
			oret.put("thinklab.version", Version.VERSION);
			oret.put("thinklab.branch", Version.BRANCH);
			oret.put("thinklab.status", Version.STATUS);
			oret.put("thinklab.inst", System.getenv("THINKLAB_INST"));
			oret.put("thinklab.home", System.getenv("THINKLAB_HOME"));
			oret.put("boot.time", KnowledgeManager.get().activeSince()
					.getTime());
			oret.put("current.time", new Date().getTime());
			oret.put("memory.total", runtime.totalMemory());
			oret.put("memory.max", runtime.maxMemory());
			oret.put("memory.free", runtime.freeMemory());
			oret.put("processors", runtime.availableProcessors());
			oret.put("admin",  isAdmin? "true" : "false");

			/*
			 * TODO - add:
			 * whether user has admin privileges
			 * list of projects
			 * list of plugins with version and load status
			 * list of known namespaces with project and error status
			 * list of current tasks with status
			 * public configuration info
			 * running sessions, users, load and n. of commands executed (if admin)
			 */
			
			for (PluginDescriptor pd :
				 Thinklab.get().getManager().getRegistry().getPluginDescriptors()) {
				
				HashMap<String, String> map = new HashMap<String, String>();

				if (!ProjectFactory.get().isProject(pd)) {
					map.put("id", pd.getId());
					map.put("version", pd.getVersion().getMajor() + "." + pd.getVersion().getMinor());
					map.put("active", 
						Thinklab.get().getManager().isPluginActivated(pd) ?
								"true" :
								"false");
				}
				
				oret.append("plugins", map);
			}

			for (IProject p : ProjectFactory.get().getProjects()) {
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				/*
				 * add version, deploy time, error status etc
				 */
				map.put("id", p.getId());
				map.put("id", p.getId());
				map.put("id", p.getId());
				
				oret.append("projects", map);
			}
			
			/*
			 * list all ontologies (local namespaces)
			 * 
			 * return namespace, URI and last modification date as a long
			 */
			for (IOntology o : KnowledgeManager.get().getKnowledgeRepository().retrieveAllOntologies()) {
				oret.append("ontologies", new String[] {o.getConceptSpace(), o.getURI(), Long.toString(o.getLastModificationDate())});
			}
//			
//			for (IProject p : ProjectFactory.get().getProjects()) {				
//	
//				HashMap<String, String> map = new HashMap<String, String>();
//				
//				/*
//				 * TODO add stuff - version, deploy time
//				 */
//				map.put("id", p.getId());
//	
//				oret.append("projects", p.getId());
//			}

			for (IProject p : ProjectFactory.get().getProjects()) {
				
				for (INamespace n : p.getNamespaces()) {
					HashMap<String, String> map = new HashMap<String, String>();
				
					/*
					 * TODO put in number of errors, project
					 */
					map.put("id", n.getNamespace());
					map.put("last-modified", Long.toString(n.getLastModification()));
				

					oret.append("namespaces", p.getId());
				}
			}

			// getScheduler().
			
			oret.put("status", DefaultRESTHandler.DONE);


		} catch (JSONException e) {
			// come on, it's a map.
		}
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);
	    
	    return ret;
	}
	
}
