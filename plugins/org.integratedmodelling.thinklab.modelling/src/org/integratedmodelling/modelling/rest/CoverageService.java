package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

@RESTResourceHandler(id="kbox",
		 arguments="arg0,arg1,arg2",
		 description="manage kboxes and load annotations in them")
public class CoverageService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {
		
		return wrap();
	}
}
