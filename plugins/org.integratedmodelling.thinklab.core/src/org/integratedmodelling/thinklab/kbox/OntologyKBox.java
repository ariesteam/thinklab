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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IOntology;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.instancelist.InstanceList;

/**
 * A Kbox based on an ontology. Quick, dirty, and horribly inefficient, particularly for searching of course. But easy and ready to
 * use without DB connections or anything, so valuable anyway.
 *  
 * @author villa
 */
public class OntologyKBox implements IKBox {

    IOntology storage;
    boolean  modified = false;
    Properties properties = new Properties();
    
    public OntologyKBox(String kboxURI) throws ThinklabException {
		initialize("owl", kboxURI, null);
	}

    public IInstance getObjectFromID(String id, ISession session) throws ThinklabException {
        return storage.getInstance(id);
    }

    public IInstance getObjectFromID(String id, ISession session, HashMap<String, String> refTable) throws ThinklabException {
    	return storage.getInstance(id);
	}
	
	public void initialize(String protocol, String datasourceURI, Properties properties) throws ThinklabException {

		storage = 				
			KnowledgeManager.get().getKnowledgeRepository().createTemporaryOntology(NameGenerator.newName("okb"));

		URL url;
		try {
			url = new URL(datasourceURI);
		} catch (MalformedURLException e) {
			throw new ThinklabIOException("ontology kbox: source URL " +  
					datasourceURI +
					" is invalid");
		}
		
		storage.read(url);
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session) throws ThinklabException {
		return storage.createInstance(object).getLocalName();
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session, HashMap<String, String> references) throws ThinklabException {
		return storage.createInstance(object).getLocalName();
	}

	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		return query(q, null, offset, maxResults);
	}

	public IQueryResult query(IQuery q, String[] metadata, int offset, int maxResults) 
		throws ThinklabException {

		ArrayList<IInstance> rret = new ArrayList<IInstance>();

	    for (IInstance i : storage.getInstances())
	    	if ((q == null || q.isEmpty()) || (q instanceof Constraint && ((Constraint)q).match(i)))
	    	   rret.add(i);
	    
	    SimpleQueryResult ret = new SimpleQueryResult(offset, rret.size());
	    
	    if (maxResults == -1)
	    	maxResults = rret.size();
	    
	    for (int i = offset; i < maxResults; i++)
	    	ret.add(rret.get(i));
	    
	    return ret;
	}

	public IKBoxCapabilities getKBoxCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	public IQueryResult query(IQuery q) throws ThinklabException {
		return query(q, 0, -1);
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata, ISession session) throws ThinklabException {
		return storage.createInstance(list).getLocalName();
	}

	public Polylist getObjectAsListFromID(String id, HashMap<String, String> refTable)
			throws ThinklabException {
		
       IInstance ii = storage.getInstance(id);
       return ii.toList(id, refTable);
	}

	public String storeObject(Polylist list, String id, Map<String, IValue> metadata,
			ISession session, HashMap<String, String> refTable) throws ThinklabException {

		InstanceList il = new InstanceList(list);
		String ret = il.getLocalName();
		
		if (!refTable.containsKey(il.getLocalName())) {
			
			/* TODO this will create all sub-instances, too, which is wrong. We must add the
			 * ref handling into ontology.
			 */				
			ret = storage.createInstance(list).getLocalName();
			refTable.put(il.getLocalName(), ret);
		}
		return ret;
		
	}

	public IQuery parseQuery(String toEval) throws ThinklabException {

		Polylist l = null;
		try {
			l = Polylist.parse(toEval);
		} catch (MalformedListException e) {
			throw new ThinklabValidationException(e);
		}
		return new Constraint(l);
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return storage.getURI();
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public long getObjectCount() {
		try {
			return storage.getInstances().size();
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
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
