package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class Restart extends DefaultRESTHandler {

	@Get
	public Representation shutdown() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
				
		int seconds = 5;
		String hook = getArgument("hook");
		
		/*
		 * TODO hook parameters
		 */
		
		if (getArgument("time") != null) {
			seconds = Integer.parseInt(getArgument("time"));
		}
		
		Thinklab.get().shutdown(hook, seconds);
		info((hook == null ? "shutdown" : hook) + " scheduled in " + seconds + " seconds"); 
		
		return wrap();
	}
	
}
