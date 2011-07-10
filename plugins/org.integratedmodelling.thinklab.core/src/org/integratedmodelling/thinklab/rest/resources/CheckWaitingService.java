package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

/**
 * Pass taskid, see if it has completed, if so return result and status, otherwise
 * return taskid and status = WAIT. Used to implement background processing on 
 * the client side.
 * 
 * @author ferdinando.villa
 *
 */
public class CheckWaitingService extends DefaultRESTHandler {

	@Get
	public Representation service() {

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
