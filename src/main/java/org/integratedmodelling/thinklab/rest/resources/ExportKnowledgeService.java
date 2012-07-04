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

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.Thinklab;
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
public class ExportKnowledgeService extends DefaultRESTHandler {

	@Get
	public Representation service() {

		try {			
			if (!checkPrivileges("user:Administrator"))
				return wrap();
			
			Pair<File, String> fname = this.getFileName("knowledge.zip", getSession());
			File tf = Thinklab.get().getScratchArea("_k");
			Thinklab.get().getKnowledgeManager().extractCoreOntologies(tf);
			FolderZiper.zipFolder(tf.toString(), fname.getFirst().toString());
			put("handle", fname.getSecond());

		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}



}
