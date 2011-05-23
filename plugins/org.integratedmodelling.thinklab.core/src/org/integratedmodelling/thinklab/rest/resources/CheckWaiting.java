package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class CheckWaiting extends DefaultRESTHandler {

	@Get
	public Representation getResource() {

		try {
			
			String cmd = getArgument("resource");
		
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}

}
