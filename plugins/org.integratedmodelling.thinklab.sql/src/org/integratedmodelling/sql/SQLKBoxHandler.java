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
package org.integratedmodelling.sql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.extensions.KBoxHandler;

public class SQLKBoxHandler implements KBoxHandler {


	public IKBox createKBox(String originalURI, String protocol, String dataUri, Properties properties) throws ThinklabException {
		
		if (protocol.equals("pg") || protocol.equals("hsqldb") || protocol.equals("mysql"))
			return new SQLKBox(originalURI, protocol, dataUri, properties);

		return null;
	}

	public IKBox createKBoxFromURL(URI url) throws ThinklabStorageException {
		throw new ThinklabStorageException("sql kboxes must be created with the kbox protocol");
	}
	

}
