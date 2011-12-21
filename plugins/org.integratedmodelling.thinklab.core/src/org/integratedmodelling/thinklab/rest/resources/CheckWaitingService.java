/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.rest.resources;

import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
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
