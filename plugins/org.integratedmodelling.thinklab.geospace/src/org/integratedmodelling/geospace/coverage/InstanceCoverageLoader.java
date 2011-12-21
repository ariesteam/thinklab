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
package org.integratedmodelling.geospace.coverage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

public class InstanceCoverageLoader extends CoverageHandler {

	ArrayList<IInstance> instances = null;
	
	public InstanceCoverageLoader(URL url, Properties properties)
			throws ThinklabException {
		super(url, properties);
	}

	public Collection<IInstance> loadObservations(ISession session) throws ThinklabException {

		instances = new ArrayList<IInstance>();
		
		process();
		
		for (Polylist list : olist) {
			System.out.println(Polylist.prettyPrint(list));
			instances.add(session.createObject(list));
		}
		
		return instances;
	}

}
