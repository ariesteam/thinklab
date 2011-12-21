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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;


public class DefaultThinklabWebModel extends ThinklabWebModel {

	Properties properties = null;
	
	@Override
	public void initialize(ThinklabWebSession session) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void persist(String authenticatedUser) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void restore(String authenticatedUser) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public PrintStream getOutputStream() {
		return null;
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

}
