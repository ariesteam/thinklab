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
package org.integratedmodelling.geospace.feature;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

public class InstanceShapefileLoader extends InstanceShapefileHandler {

	public InstanceShapefileLoader(URL url) throws ThinklabException {
		super(url);
	}

	public InstanceShapefileLoader(URL url, Properties properties) throws ThinklabException {
		super(url, properties);
	}

	@Override
	public void notifyInstance(Polylist list) throws ThinklabException {
		
		if (observations == null) {
			observations = new ArrayList<Polylist>();
		}
		
		observations.add(list);
	}

	public Collection<IInstance> loadObservations(ISession session) throws ThinklabException {
		
		// extract stuff from shapefile
		process();

		ArrayList<IInstance> ret = new ArrayList<IInstance>();
		
		for (Polylist list : observations) {		
			ret.add(session.createObject(list));
		}
		
		return ret;
	}


}
