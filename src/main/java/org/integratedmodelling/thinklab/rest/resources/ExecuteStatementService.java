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
import java.io.InputStream;

import org.integratedmodelling.thinklab.api.modelling.IModelObject;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTUserModel;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Returns server status information. Bound to the root service URL and used as an
 * handshaking command ("ping") to ensure that server is alive and well.
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
			String ns = USER_NAMESPACE_PREFIX + getSession().getID();
			RESTUserModel um = (RESTUserModel)(getSession().getUserModel());
			
			
			InputStream is = new ByteArrayInputStream(getArgument("statement").getBytes());
			um.getModelGenerator().parseInNamespace(is, ns, um.getResolver());
			is.close();
			IModelObject o = um.getResolver().getLastProcessedObject(); 

			/*
			 * turn the context into enough JSON to satisfy the client.
			 */
			if (o instanceof IObservation) {
				um.mergeContext(((IObservation) o).getContext());
			}
			
			oret = um.contextToJSON(oret);
			
		} catch (Exception e) {
			fail(e);
		}
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);

		return ret;
	}
	
}
