package org.integratedmodelling.thinklab.rest;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ThinklabRESTResource extends ServerResource {

	@Get
	public String represent() {
		return "Dio pongo";
	}
}
