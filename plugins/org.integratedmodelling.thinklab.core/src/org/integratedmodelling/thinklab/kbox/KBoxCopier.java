/**
 * KBoxCopier.java
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

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.owlapi.Session;
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
public class KBoxCopier  {

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
	protected boolean filterObject(IList object) {
		return true;
	}

//	private IKBox resolveURL(String sourceURL, ISession session) throws ThinklabException {
//		// TODO Auto-generated method stub
//		return 
//			session == null ? 
//					KBoxManager.get().requireGlobalKBox(sourceURL) :
//					session.requireKBox(sourceURL);	
//	}

	/**
	 * 
	 */
	public void transferKnowledge(String sourceURL, String targetURL, ISession session)
			throws ThinklabException {

//		sourceKB = resolveURL(sourceURL, session);
//		targetKB = resolveURL(targetURL, session);
//
//		IQueryResult res = sourceKB.query(null);
//		HashMap<String, String> refl = new HashMap<String, String>();
//		HashMap<String, String> refs = new HashMap<String, String>();
//
//		ISession s = new Session();
//
//		for (int i = 0; i < res.getTotalResultCount(); i++) {
//			
//			IList l = res.getResultAsList(i, refl);
//			InstanceList il = new InstanceList(l);
//			
//			System.out.println(PolyList.prettyPrint(l));
//
//			String id = il.getLocalName();
//			
//			if (!refs.containsKey(id) && filterObject(l)) {
//				targetKB.storeObject(l, null, null, s, refs);
//			}
//		}
	}

	/**
	 * Use with caution, nobody wants a 5000 element list. Uses the existing ref
	 * table, so it will not export things that have been exported before.
	 */
	public IList exportKnowledge(String sourceURL, ISession session) throws ThinklabException {

		ArrayList<IList> ret = new ArrayList<IList>();
		HashMap<String, String> refs = new HashMap<String, String>();

//		sourceKB = resolveURL(sourceURL, session);
//
//		IQueryResult res = sourceKB.query(null);
//
//		for (int i = 0; i < res.getTotalResultCount(); i++) {
//			IList l = res.getResultAsList(i, refs);
//			if (l != null && !l.isEmpty() && !refs.containsKey(new InstanceList(l).getLocalName()))
//				ret.add(l);
//		}

		return PolyList.fromArray(ret.toArray());
	}

	/**
	 * Input must be a IList of lists, each specifying an object.
	 */
	public void importKnowledge(String targetURL, IList knowledge)
			throws ThinklabException {

//		HashMap<String, String> refs = new HashMap<String, String>();
//
//		targetKB = resolveURL(targetURL, null);
//
//		Object[] os = knowledge.array();
//
//		ISession session = new Session();
//		
//		for (int i = 0; i < os.length; i++) {
//			targetKB.storeObject((IList) os[i], null, null, session, refs);
//		}

	}

}
