package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Version;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class Status extends DefaultRESTHandler {

	@Get
	public Representation getThinklabStatus() {
		
		JSONObject oret = new JSONObject();
		
		try {

			oret.put("thinklab.version", Version.VERSION);
			oret.put("thinklab.branch", Version.BRANCH);
			oret.put("thinklab.status", Version.STATUS);
			oret.put("up.since", KnowledgeManager.get().activeSince().toString());

			/*
			 * TODO add anything interesting: plugins loaded, kboxes online, 
			 * memory used/left, number of processors, etc.
			 */
			

		} catch (JSONException e) {
			// come on, it's a map.
		}
		
		JsonRepresentation ret = new JsonRepresentation(oret);
	    ret.setCharacterSet(CharacterSet.UTF_8);

		return ret;
	}
	
}
