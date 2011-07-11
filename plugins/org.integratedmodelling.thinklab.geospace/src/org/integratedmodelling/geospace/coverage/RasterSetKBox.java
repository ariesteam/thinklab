/**
 * RasterSetKBox.java
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
package org.integratedmodelling.geospace.coverage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;

/**
 * A KBox that wraps one or more rasters, mapping them to different observations or 
 * different context dimensions for the same observation. 
 * 
 * @author Ferdinando Villa
 *
 */
public class RasterSetKBox implements IKBox {

	String uri = null;
	Properties properties = new Properties();
	
	public IQuery parseQuery(String toEval) throws ThinklabException {
		return null;
	}
	
	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance getObjectFromID(String id, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(String protocol, String datasourceURI,
			Properties properties) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> references) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IKBoxCapabilities getKBoxCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata, ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IQueryResult query(IQuery q) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Polylist getObjectAsListFromID(String id, HashMap<String, String> refTable)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public long getObjectCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void resetToEmpty() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, IConcept> getMetadataSchema() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}


}
