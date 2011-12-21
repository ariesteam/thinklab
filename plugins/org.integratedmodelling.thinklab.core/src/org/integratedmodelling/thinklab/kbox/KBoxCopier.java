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

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.extensions.KnowledgeImporter;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.instancelist.InstanceList;

/**
 * A simple utility class to assist import/export of objects and transfer of
 * knowledge from a kbox to another.
 * 
 * Only the importKnowledge() function is reentrant with respect to the instance
 * set in each kbox: all others need to be called in the same sequence of
 * operations, as they save the list of objects exported and imported between
 * calls, and will only insert references for them. To use correctly, create a
 * new KBoxCopier at the start of any coordinated sequence of imports from one
 * kbox to another.
 * 
 * @author Ferdinando
 * 
 */
public class KBoxCopier implements KnowledgeImporter {

	private IKBox sourceKB;
	private IKBox targetKB;

	/**
	 * Used to make sure we don't store objects twice. Can be overridden to
	 * implement more sophisticated filtering; just make sure you call super
	 * first and you honor its result value.
	 * 
	 * @param object
	 * @param refs2 
	 * @return true if the object should be stored
	 */
	protected boolean filterObject(Polylist object) {
		return true;
	}

	private IKBox resolveURL(String sourceURL, org.integratedmodelling.thinklab.interfaces.applications.ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return 
			session == null ? 
					KBoxManager.get().requireGlobalKBox(sourceURL) :
					session.requireKBox(sourceURL);	
	}

	/**
	 * 
	 */
	public void transferKnowledge(String sourceURL, String targetURL, ISession session)
			throws ThinklabException {

		sourceKB = resolveURL(sourceURL, session);
		targetKB = resolveURL(targetURL, session);

		IQueryResult res = sourceKB.query(null);
		HashMap<String, String> refl = new HashMap<String, String>();
		HashMap<String, String> refs = new HashMap<String, String>();

		ISession s = new Session();

		for (int i = 0; i < res.getTotalResultCount(); i++) {
			
			Polylist l = res.getResultAsList(i, refl);
			InstanceList il = new InstanceList(l);
			
			System.out.println(Polylist.prettyPrint(l));

			String id = il.getLocalName();
			
			if (!refs.containsKey(id) && filterObject(l)) {
				targetKB.storeObject(l, null, null, s, refs);
			}
		}
	}

	/**
	 * Use with caution, nobody wants a 5000 element list. Uses the existing ref
	 * table, so it will not export things that have been exported before.
	 */
	public Polylist exportKnowledge(String sourceURL, ISession session) throws ThinklabException {

		ArrayList<Polylist> ret = new ArrayList<Polylist>();
		HashMap<String, String> refs = new HashMap<String, String>();

		sourceKB = resolveURL(sourceURL, session);

		IQueryResult res = sourceKB.query(null);

		for (int i = 0; i < res.getTotalResultCount(); i++) {
			Polylist l = res.getResultAsList(i, refs);
			if (l != null && !l.isEmpty() && !refs.containsKey(new InstanceList(l).getLocalName()))
				ret.add(l);
		}

		return Polylist.PolylistFromArray(ret.toArray());
	}

	/**
	 * Input must be a polylist of lists, each specifying an object.
	 */
	public void importKnowledge(String targetURL, Polylist knowledge)
			throws ThinklabException {

		HashMap<String, String> refs = new HashMap<String, String>();

		targetKB = resolveURL(targetURL, null);

		Object[] os = knowledge.array();

		ISession session = new Session();
		
		for (int i = 0; i < os.length; i++) {
			targetKB.storeObject((Polylist) os[i], null, null, session, refs);
		}

	}

}
