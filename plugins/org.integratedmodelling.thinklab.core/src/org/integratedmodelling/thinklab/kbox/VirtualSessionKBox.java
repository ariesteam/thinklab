/**
 * VirtualSessionKBox.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.kbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.utils.instancelist.InstanceList;

/**
 * A "virtual" kbox that only gives access to a subset of objects contained in a session. Used to simplify 
 * organization when objects are loaded from a source: a kbox with the same ID as the source filename is
 * created (overwritten if the loading is done more than once). The kbox can be exported or copied
 * just like any other.
 * 
 * @author Ferdinando
 *
 */
public class VirtualSessionKBox implements IKBox {

	Properties properties = new Properties();
	ArrayList<IInstance> instances = new ArrayList<IInstance>();
	ISession session;
	
	public VirtualSessionKBox(ISession session) {
		this.session = session;
		this.properties.putAll(session.getSessionProperties());
	}
	
	public IQuery parseQuery(String toEval) throws ThinklabException {

		Polylist l = Polylist.parse(toEval);
		return new Constraint(l);
	}
	
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	public Polylist getObjectAsListFromID(String id,
			HashMap<String, String> refTable) throws ThinklabException {
		
		IInstance inst = session.requireObject(id);
		return inst.asList(null);
	}

	public IInstance getObjectFromID(String id, ISession session)
			throws ThinklabException {
		return session.requireObject(id);
	}

	public IInstance getObjectFromID(String id, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {

		return session.requireObject(id);
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata, ISession s) throws ThinklabException {

		IInstance ninst = session.createObject(list);
		instances.add(ninst);
		return ninst.getLocalName();
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session)
			throws ThinklabException {
		
		instances.add(object);
		return object.getLocalName();
	}

	public String storeObject(Polylist list, String iid, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> refTable) throws ThinklabException {

		/*
		 * FIXME not really needed, should not even be called; make sure 
		 * it isn't called improperly
		 */
		String id = new InstanceList(list).getLocalName();
		String ret = id;
		
		if (!refTable.containsKey(id)) {
			IInstance ninst = session.createObject(list);
			instances.add(ninst);
			ret = ninst.getLocalName();
			refTable.put(id, ret);
		}
		
		return ret;
		
	}
	public String storeObject(IInstance object, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> references) throws ThinklabException {

		String oid = object.getLocalName();
		instances.add(object);
		return oid;
	}

	public IQueryResult query(IQuery q) throws ThinklabException {		
		return query(q, null, 0, -1);
	}

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		return query(q, null, offset, maxResults);
	}

	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		
		ArrayList<IInstance> rret = new ArrayList<IInstance>();
		
		for (IInstance i : instances) {
			
			boolean add = false;
			
			if (q == null || q.isEmpty()) {
				add = true;
			} else if (q instanceof Constraint) {
				add = ((Constraint)q).match(i);
			} else {
				throw new ThinklabValidationException("virtual kbox does not support query " + q);
			}
			
			if (add)
				rret.add(i);
		}
		
	    SimpleQueryResult ret = new SimpleQueryResult(offset, rret.size());
	    
	    if (maxResults == -1)
	    	maxResults = rret.size();
	    
	    for (int i = offset; i < maxResults; i++)
	    	ret.add(rret.get(i));
		
		return ret;
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
		return instances.size();
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
