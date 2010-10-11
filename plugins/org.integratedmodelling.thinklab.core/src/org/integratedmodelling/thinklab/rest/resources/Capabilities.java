package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class Capabilities extends DefaultRESTHandler {

	@Get
	public Representation getCapabilities() {
		
		JSONObject oret = new JSONObject();
		
		/*
		 * TODO
		 */
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);

		return ret;
	}
	
}
