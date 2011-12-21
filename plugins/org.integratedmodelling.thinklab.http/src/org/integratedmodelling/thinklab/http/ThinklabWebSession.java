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
package org.integratedmodelling.thinklab.http;

import javax.servlet.http.HttpSession;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.http.application.ThinklabWebApplication;
import org.integratedmodelling.thinklab.owlapi.Session;

/**
 * 
 * 
 * @author Ferdinando
 *
 */
public class ThinklabWebSession extends Session {

	public ThinklabWebSession() throws ThinklabException {
		super();
	}

	@Override
	public String toString() {
		return "[" + httpSession.getId() + " owner: " + getUserModel() + "]";
	}
	
	HttpSession httpSession = null;
	private ThinklabWebApplication application = null;
	
	public void initialize(HttpSession session) {
		httpSession = session;
	}
	
	HttpSession getHttpSession() {
		return httpSession;
	}

	public ThinklabWebApplication getApplication() {
		return application;
	}
	
	public void setApplication(ThinklabWebApplication app) {
		application  = app;
	}

	public void setModel(ThinklabWebModel model) throws ThinklabException {
		
		setUserModel(model);
		
		if (model != null) {
			model.initialize(this);
		}
	}

}
