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

import java.util.HashMap;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.lang.IPrototype;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.api.runtime.IServer;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.thinklab.rest.RESTManager.RestCommand;
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
			oret.put(IServer.VERSION_STRING, Thinklab.get().getVersion());
			oret.put(IServer.BOOT_TIME_MS, Thinklab.get().getBootTime());
			oret.put(IServer.TOTAL_MEMORY_MB, runtime.totalMemory()/1048576);
			oret.put(IServer.FREE_MEMORY_MB, runtime.freeMemory()/1048576);
			oret.put(IServer.AVAILABLE_PROCESSORS, runtime.availableProcessors());
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
			
//			for (PluginDescriptor pd :
//				 Thinklab.get().getManager().getRegistry().getPluginDescriptors()) {
//				
//				HashMap<String, String> map = new HashMap<String, String>();
//
//				if (!ProjectFactory.get().isProject(pd)) {
//					map.put("id", pd.getId());
//					map.put("version", pd.getVersion().getMajor() + "." + pd.getVersion().getMinor());
//					map.put("active", 
//						Thinklab.get().getManager().isPluginActivated(pd) ?
//								"true" :
//								"false");
//				}
//				
//				oret.append("plugins", map);
//			}

			for (IProject p : Thinklab.get().getProjects()) {
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				/*
				 * add version, deploy time, error status etc
				 */
				map.put("id", p.getId());
				
				oret.append("projects", map);
			}
			
			/*
			 * add prototypes for all functions
			 */
			for (IPrototype p : ((ModelManager)(Thinklab.get().getModelManager())).getFunctionPrototypes()) {

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", p.getId());
				map.put("mandatory", p.getMandatoryArgumentNames());
				oret.append("functions", map);				
			}
			
			/*
			 * add prototypes for all REST commandss
			 */
			int i = 0;
			for (RestCommand rc : RESTManager.get().getCommandDescriptors()) {
				oret.append("commands", rc.asArray());
			}
			
			/*
			 * list all ontologies (local namespaces)
			 * 
			 * return namespace, URI and last modification date as a long
			 */
			for (IOntology o : Thinklab.get().getKnowledgeRepository().getOntologies()) {
				oret.append("ontologies", new String[] {o.getConceptSpace(), o.getURI(), Long.toString(o.getLastModificationDate())});
			}

			for (IProject p : Thinklab.get().getProjects()) {
				
				for (INamespace n : p.getNamespaces()) {
					HashMap<String, String> map = new HashMap<String, String>();
				
					/*
					 * TODO put in number of errors, project
					 */
					map.put("id", n.getId());
					map.put("last-modified", Long.toString(n.getTimeStamp()));
					
					oret.append("namespaces", map);
				}
			}

			// getScheduler().
			
			oret.put("status", IServer.OK);


		} catch (JSONException e) {
			// come on, it's a map.
		}
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);
	    
	    return ret;
	}
	
}
