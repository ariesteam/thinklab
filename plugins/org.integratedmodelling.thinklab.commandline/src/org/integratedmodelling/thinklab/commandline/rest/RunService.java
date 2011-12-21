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
package org.integratedmodelling.thinklab.commandline.rest;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.application.Application;
import org.integratedmodelling.thinklab.interfaces.annotations.RESTResourceHandler;
import org.integratedmodelling.thinklab.rest.DefaultRESTHandler;
import org.integratedmodelling.thinklab.rest.RESTTask;
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
@RESTResourceHandler(id="run", description="run application",  arguments="application")
public class RunService extends DefaultRESTHandler {

	class AppThread extends Thread implements RESTTask {

		String app;
		IValue result = null;
		boolean _done = false;
		
		AppThread(String app) {
			this.app = app;
		}
		
		@Override
		public void run() {
			try {
				result = Application.run(app);
			} catch (ThinklabException e) {
				fail(e);
			} finally {
				_done = true;
			}
		}

		@Override
		public Representation getResult() {
			if (this.result != null)
				setResult(result.toString());
			return wrap();
		}

		@Override
		public boolean isFinished() {
			return _done;
		}
	}
	
	@Get
	public Representation service() throws ThinklabException {

		if (!checkPrivileges("user:Administrator"))
			return wrap();
		
		try {
			String app = this.getArgument("application");
			return enqueue(new AppThread(app));
		} catch (Exception e) {
			fail(e);
		}
		
		return wrap();
	}
	
}
