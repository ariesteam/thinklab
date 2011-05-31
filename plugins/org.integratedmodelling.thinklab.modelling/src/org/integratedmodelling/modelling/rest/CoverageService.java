package org.integratedmodelling.modelling.rest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

@RESTResourceHandler(id="coverage",
		 arguments="model,storyline,context,scenario",
		 options="format,output",
		 description="check the spatial coverage of a given model, storyline or concept")
public class CoverageService extends DefaultRESTHandler {

	@Get
	public Representation runService() throws ThinklabException {
		
		return wrap();
	}
}
