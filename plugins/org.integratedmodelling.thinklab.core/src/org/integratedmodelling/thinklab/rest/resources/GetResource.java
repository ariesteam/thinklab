package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class GetResource extends DefaultRESTHandler {

	@Get
	public Representation getCommandStatus() {

		try {
			
			String cmd = getArgument("enqueued");
		
			if (getScheduler().finished(cmd)) {
				setResult(getScheduler().getResult(cmd));
			} else if (getScheduler().started(cmd) || getScheduler().enqueued(cmd)) {
				keepWaiting(cmd);
			} else {
				throw new ThinklabInternalErrorException(
						"rest: status: task ID " + cmd + " unknown");
			}
			
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}

}
