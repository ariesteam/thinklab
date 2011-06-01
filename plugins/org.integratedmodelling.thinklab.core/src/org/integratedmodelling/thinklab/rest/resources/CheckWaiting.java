package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Pass taskid, see if it has completed, if so return result.
 * @author ferdinando.villa
 *
 */
public class CheckWaiting extends DefaultRESTHandler {

	@Get
	public Representation checkTask() {

		try {
			// don't change the ordering in the if 
			String cmd = getArgument("taskid");
			if ((getScheduler().started(cmd) && !getScheduler().finished(cmd)) || getScheduler().enqueued(cmd)) {
				keepWaiting(cmd);
			} else if (getScheduler().finished(cmd)) {
				return getScheduler().getResult(cmd);
			} else {
				throw new ThinklabInternalErrorException("task " + cmd + " unknown");
			}
			
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}

}
