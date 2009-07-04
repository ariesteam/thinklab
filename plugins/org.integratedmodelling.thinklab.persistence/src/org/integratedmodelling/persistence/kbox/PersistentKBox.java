/**
 * PersistentKBox.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabPersistencePlugin.
 * 
 * ThinklabPersistencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabPersistencePlugin is distributed in the hope that it will be useful,
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
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.persistence.kbox;

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
 * 
 * @author Ioannis N. Athanasiadis
 * @since Feb 5, 2007
 * @version 0.2
 */
public class PersistentKBox implements IKBox {
	
	private Properties properties = new Properties();
	protected ClassLoader PersistentClassLoader = Thread.currentThread().getContextClassLoader();

	public IInstance getObjectFromID(String id, ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public IInstance getObjectFromID(String id, ISession session, HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(String protocol, String datasourceURI, Properties properties) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session) throws ThinklabException {

		PInstance p = new PInstance(object.getDirectType(),((PSession)session).kBox);
		
		return null;
	}

	public String storeObject(IInstance object, String id, Map<String, IValue> metadata, ISession session, HashMap<String, String> references) throws ThinklabException {
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

	public IQuery parseQuery(String toEval) throws ThinklabException {
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
