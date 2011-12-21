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
package org.integratedmodelling.thinklab.kbox;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.utils.Polylist;

/**
 * A KBoxWrapper is used as base class to implement KBoxes that need a more sophisticated
 * API. They implement IKbox and redirect all functions to the underlying kbox. The reason
 * it exists at this level is that a kbox can specify the wrapper it wants to be, using the
 * thinklab.kbox.wrapper parameter in its .kbox file.
 * 
 * The class must have an empty constructor. If initialization is needed the initialize()
 * function should be overridden, making sure that super.initialize() is called first thing.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class KBoxWrapper implements IKBox {

	protected IKBox kbox;
	
	public IKBoxCapabilities getKBoxCapabilities() {
		return kbox.getKBoxCapabilities();
	}

	public Polylist getObjectAsListFromID(String id,
			HashMap<String, String> refTable) throws ThinklabException {
		return kbox.getObjectAsListFromID(id, refTable);
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> refTable) throws ThinklabException {
		return kbox.storeObject(list, id, null, session, refTable);
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		return kbox.storeObject(list, id, null, session);
	}

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		return kbox.query(q, offset, maxResults);
	}

	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		return kbox.query(q, metadata, offset, maxResults);
	}

	public IQueryResult query(IQuery q) throws ThinklabException {
		return kbox.query(q);
	}

	public void initialize(IKBox kbox) {
		this.kbox = kbox;
	}
	
	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {
		return kbox.getObjectFromID(id, session);
	}
	
	public IInstance getObjectFromID(String id, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {
		return kbox.getObjectFromID(id, session, refTable);
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		return kbox.storeObject(object, id, null, session);
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> references) throws ThinklabException {
		return kbox.storeObject(object, id, null, session, references);
	}
	
	@Override
	public Map<String, IConcept> getMetadataSchema() throws ThinklabException {
		return kbox.getMetadataSchema();
	}

	@Override
	public long getObjectCount() {
		return kbox.getObjectCount();
	}

	@Override
	public Properties getProperties() {
		return kbox.getProperties();
	}

	@Override
	public String getUri() {
		return kbox.getUri();
	}

	@Override
	public void resetToEmpty() throws ThinklabException {
		kbox.resetToEmpty();
	}

	@Override
	public IQuery parseQuery(String toEval) throws ThinklabException {
		return kbox.parseQuery(toEval);
	}

}
