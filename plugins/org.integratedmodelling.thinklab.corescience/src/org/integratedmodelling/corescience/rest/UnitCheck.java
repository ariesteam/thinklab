package org.integratedmodelling.corescience.rest;

import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

@RESTResourceHandler(id="/ucheck", description="")
public class UnitCheck extends DefaultRESTHandler {

	@Get
	public Representation execute() {
		
		JSONObject ret = new JSONObject();

		try {
			ret.put("test","ok");
		} catch (JSONException e) {
			// bah
		}
		
		return wrap(ret);
	}
	
}
