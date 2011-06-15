package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

@RESTResourceHandler(id="storyline",
		 arguments="command,storyline,context,scenario",
		 options="output,visualize,dump",
		 description="control the storyline subsystem")
public class StorylineService extends DefaultRESTHandler {

	@Get
	public Representation runService() throws ThinklabException {
		
		return wrap();
	}
}
