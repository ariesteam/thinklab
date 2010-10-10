package org.integratedmodelling.thinklab.rest;

import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.restlet.resource.ServerResource;

/**
 * REST resource handlers must be derived from this one, so that Thinklab knows how to work with
 * the resource. They should adopt all the appropriate rest.interface interfaces to tell the system
 * what formats they're capable of handling.
 * 
 * @author Ferdinando
 *
 */
public abstract class DefaultRESTHandler extends ServerResource {

	/**
	 * This one performs all the work and must save its results so that the retrieval functions (depending on
	 * the rest.interface adopted) can return them in the requested format.
	 * 
	 * @param context
	 * @param parameters
	 */
	abstract protected void execute(List<String> context, HashMap<String,String> parameters) 
		throws ThinklabException;
	
}
