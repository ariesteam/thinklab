/**
 * ShapefileKBox.java
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.exceptions.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;

/**
 * A simple kbox that gives access to a shapefile that is semantically annotated with a kbox file.
 * @author Ferdinando Villa
 *
 */
public class ShapefileKBox extends InstanceShapefileHandler implements IKBox {

	String uri = null;
	
	public IQuery parseQuery(String toEval) throws ThinklabException {
		return null;
	}
	
	public ShapefileKBox(String uri, URL url, Properties properties) throws ThinklabException {
		super(url, properties);
		this.uri = uri;
	}

	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {

		super.process(id);
		
		if (observations.size() < 1) {
			throw new ThinklabResourceNotFoundException(
					"resource " + id + " not found in kbox " + super.getLayerName());
		} else if (observations.size() > 1) {
			throw new ThinklabValidationException(
					"resource " + id + " matches multiple objects in kbox " + super.getLayerName());
		}
		
		return session.createObject(observations.get(0));
	}

	public IInstance getObjectFromID(String id, ISession session, HashMap<String, String> refTable) 
		throws ThinklabException {
		// shapefiles are non-relational, so no need for reference handling.
		// FIXME actually depends on what objects we create from them, but we should
		// not find the secondary ones anyway.
		return getObjectFromID(id, session);
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session) throws ThinklabException {
		throw new ThinklabStorageException("shapefile kbox is read-only");
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session, HashMap<String, String> references) throws ThinklabException {
		throw new ThinklabStorageException("shapefile kbox is read-only");
	}

	@Override
	public void notifyInstance(Polylist list) {
		// nothing to do, we're not going to call process()
	}

	public Capabilities getCapabilities() {
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
		
		if (q == null || q.isEmpty()) {
			// TODO implement offset/max
			//ret = this.getFeatureIDs(resultSchema);
		} else {
			throw new ThinklabUnimplementedFeatureException(
					getLayerName() + 
					": shapefile kbox cannot be searched. Please import to SQL.");
		}
		
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
		return uri;
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
