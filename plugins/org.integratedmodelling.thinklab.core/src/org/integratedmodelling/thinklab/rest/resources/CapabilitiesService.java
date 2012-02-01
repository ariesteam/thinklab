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

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Version;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
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
			oret.put("status", DefaultRESTHandler.DONE);

			/*
			 * TODO - add:
			 * list of project
			 * list of loaded plugins
			 * list of unloaded plugins
			 * list of known namespaces
			 * public configuration info
			 * running kboxes, type of backup store, content and status
			 * running sessions, users, load and n. of commands executed
			 */
			

		} catch (JSONException e) {
			// come on, it's a map.
		}
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);
	    
	    return ret;
	}
	
}
