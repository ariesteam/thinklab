package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

@RESTResourceHandler(id="observe",
		 arguments="concept,context,scenario",
		 description="list observations for the given concept in the passed context")
public class ObserveService extends DefaultRESTHandler {

	@Get
	public Representation runService() throws ThinklabException {
		
		return wrap();
	}
}
