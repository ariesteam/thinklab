/**
 * InstanceShapefileLoader.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
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
			ret.add(session.createObject(list, null));
		}
		
		return ret;
	}


}
