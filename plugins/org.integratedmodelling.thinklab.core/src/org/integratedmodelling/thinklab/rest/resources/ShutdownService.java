package org.integratedmodelling.thinklab.rest.resources;

import java.util.HashMap;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Manages shutdown of the server, allowing to insert "hooks" for the top-level 
 * shell script to trigger restart, upgrade and the like.
 * 
 * @author ferdinando.villa
 *
 */
public class ShutdownService extends DefaultRESTHandler {

	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
				
		int seconds = 5;
		String hook = getArgument("hook");
		HashMap<String,String> parms = new HashMap<String, String>();
		
		/*
		 * hook parameters
		 */
		for (int i = 1; ; i++) {
			if (getArgument("hookarg" + i) == null)
				break;
			parms.put("ARG"+i, getArgument("hookarg"+i));
		}
		if (getArgument("time") != null) {
			seconds = Integer.parseInt(getArgument("time"));
		}
		
		Thinklab.get().shutdown(hook, seconds, parms);
		info((hook == null ? "shutdown" : hook) + " scheduled in " + seconds + " seconds"); 
		
		return wrap();
	}
	
}
