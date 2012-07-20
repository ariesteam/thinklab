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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.modelling.datasets.FileDataset;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTUserModel;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Execute a language statement and optionally produce a visualization and/or dataset
 * for the results.
 * 
 * @author ferdinando.villa
 *
 */
public class ExecuteStatementService extends DefaultRESTHandler {
	
	/*
	 * TODO localize to user ID if authenticated
	 */
	private static final String USER_NAMESPACE_PREFIX = "rest.user.";
	
	@Get
	public Representation service() {
		
		JSONObject oret = new JSONObject();
		
		try {

			/*
			 * if vdir = <directory> is passed, we are asked to produce a File
			 * visualization in the given local directory. This happens when
			 * thinklab is being run as an embedded local server.
			 */
			String vdir = getArgument("visdir"); 
			
			/*
			 * if visualize = true is passed, we are asked to produce a NetCDF output
			 * and make it available for download. If vdir is also passed, we produce
			 * the file in the given directory instead, with the name "data.nc". 
			 */
			boolean visualize = Boolean.parseBoolean(getArgument("visualize", "false"));

			String ns = USER_NAMESPACE_PREFIX + getSession().getID();
			RESTUserModel um = (RESTUserModel)(getSession().getUserModel());
			
			String statement = getArgument("statement");
			InputStream is = new ByteArrayInputStream(statement.getBytes());
			um.getModelGenerator().parseInNamespace(is, ns, um.getResolver());
			is.close();
			
			IModelObject o = um.getResolver().getLastProcessedObject(); 

			/*
			 * turn the context into enough JSON to satisfy the client.
			 */
			oret = contextToJSON(
					((ModelManager.Resolver)um.getResolver()).getCurrentContext(), 
					visualize);
			
			
		} catch (Exception e) {
			fail(e);
		}
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);

		return ret;
	}

	private JSONObject contextToJSON(IContext context, boolean isLocal) throws ThinklabException {
		
		/*
		 * create a filedataset from the context
		 */
		FileDataset fds = new FileDataset();
		fds.setContext(context);
		
		/*
		 * if not local, zip it and send it over
		 */
		File fdir = Thinklab.get().getTempArea(getSession().getWorkspace());
		if (!isLocal) {
			fdir = new File(fdir + File.separator + "ctx.zip");
		} 
		
		fds.persist(fdir.toString());
		
		return null;
	}
	
}
