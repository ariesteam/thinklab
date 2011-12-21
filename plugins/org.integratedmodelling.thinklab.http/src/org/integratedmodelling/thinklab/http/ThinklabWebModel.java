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

import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.IUserModel;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Helper class to build persistent MVC models for thinkcap. Any ThinkcapModel works with Portlets and portals in the view
 * package to help build clean MVC applications.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class ThinklabWebModel implements IUserModel {

	ISession tlSession = null;
	Properties properties = null;
	
	public abstract void initialize(ThinklabWebSession session) throws ThinklabException;

	public abstract void restore(String authenticatedUser) throws ThinklabException;	

	public abstract void persist(String authenticatedUser) throws ThinklabException;

	@Override
	public void initialize(ISession session) {
		tlSession = session;
	}	
	
	public ISession getSession() {
		return tlSession;
	}

	@Override
	public void setProperties(Properties uprop) {
		properties = uprop;
	}

	@Override
	public Properties getProperties() {

		if (properties == null)
			properties = new Properties();
		
		return properties;
	}
	
	@Override
	public IInstance getUserInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
