package org.integratedmodelling.thinklab.commandline.rest;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.utils.MiscUtilities;
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
@RESTResourceHandler(id="log", description="retrieve logged information",  arguments="lines")
public class PloadService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
		
		try {
		
			String logfile = System.getenv("THINKLAB_LOG_FILE");
			
			if (logfile != null) {
				int lines 	= Integer.parseInt(this.getArgument("lines"));
				String rr = MiscUtilities.tail(logfile, lines);
				setResult(rr);
			}
		
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
