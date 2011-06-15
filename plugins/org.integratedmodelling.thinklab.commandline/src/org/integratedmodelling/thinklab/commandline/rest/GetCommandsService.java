package org.integratedmodelling.thinklab.commandline.rest;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTManager;
import org.integratedmodelling.thinklab.rest.RESTManager.RestCommand;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Authenticate creates a session for the calling service and returns a descriptor 
 * containing its URN, its privileges and other parameters. Sessions may be persistent
 * or ephemeral.
 * 
 * If the request already contains a session urn, simply verify its validity and 
 * return session status.
 * 
 * @author ferdinando.villa
 * 
 */
@RESTResourceHandler(id="getCommands", description="get command descriptors",  arguments="")
public class GetCommandsService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		Object[] res = new Object[RESTManager.get().getCommandDescriptors().size()];
		
		int i = 0;
		for (RestCommand rc : RESTManager.get().getCommandDescriptors()) {
			res[i++] = rc.asArray();
		}
		
		setResult(res);
		
		return wrap();
	}
	
}
