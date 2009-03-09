/**
 * RuleKBox.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabRulePlugin.
 * 
 * ThinklabRulePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabRulePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.rules.kbox;

import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKBoxCapabilities;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Polylist;

/**
 * Specialized kbox that holds rules. It should be seen as a wrapper for another kbox - in fact,
 * we create the other kbox first and pass it to RuleKBox as an argument. RuleKBox will support the
 * additional functions and redirect control to the underlying implementation.
 * 
 * FIXME we must support this sort of wrapping at the kbox: protocol level and make this a IKBoxWrapper
 * or something.
 * 
 * @author Ferdinando Villa
 *
 */
public class RuleKBox implements IKBox {

	public IQuery parseQuery(String toEval) throws ThinklabException {

		Polylist l = null;
		try {
			l = Polylist.parse(toEval);
		} catch (MalformedListException e) {
			throw new ThinklabValidationException(e);
		}
		return new Constraint(l);
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

	public String storeObject(IInstance object, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(IInstance object, ISession session,
			HashMap<String, String> references) throws ThinklabException {
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

	public IQueryResult query(IQuery q, Polylist resultSchema, int offset,
			int maxResults) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public String storeObject(Polylist list, ISession session) throws ThinklabException {
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

	public String storeObject(Polylist list, ISession session,
			HashMap<String, String> refTable) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public Polylist getMetadataSchema() throws ThinklabException {
		return KBoxManager.get().parseSchema(null);
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

}
