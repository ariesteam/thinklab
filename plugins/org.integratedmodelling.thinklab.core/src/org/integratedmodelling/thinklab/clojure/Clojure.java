package org.integratedmodelling.thinklab.clojure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IRelationship;
import org.integratedmodelling.thinklab.api.knowledge.IValue;

import clojure.lang.IPersistentMap;
import clojure.lang.ISeq;
import clojure.lang.RT;

/**
 * Functions to support integrating thinklab with clojure, mostly called within thinklab.clj.
 * @author Ferdinando
 *
 */
public class Clojure {
	

	private static Polylist list2pInternal(ISeq list, Polylist plist) {
	
		if (plist == null)
			plist = new Polylist();
		
		plist = 
			plist.appendElement(list.first() instanceof ISeq ? 
					list2pInternal(((ISeq)list.first()), null) : 
					list.first());
		
		if (list.next() != null)
			plist = list2pInternal(list.next(), plist);
		
		return plist;
		
	}
	
	public static Polylist list2p(ISeq list) {
		return list2pInternal(list, null);
	}

	public static ISeq p2list(Polylist list) throws ThinklabException {

		Object[] arr = new Object[list.length()];
		
		Object[] al = list.array();
		
		for (int i = 0; i < al.length; i++) {
			if (al[i] instanceof Polylist)
				arr[i] = p2list((Polylist)al[i]);
			else
				arr[i] = al[i];
		}
		
		try {
			return RT.arrayToList(arr);
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
	}
	
	/**
	 * Promote a clojure-returned object to its likely semantic counterpart 
	 */
	public static IValue promote(Object object) {
		return null;
	}
	
	/**
	 * Strip semantics from an object if it has any, and return its likely counterpart for
	 * use in Clojure.
	 * 
	 * @param object
	 * @return
	 */
	public static Object demote(Object object) {
		return (object instanceof IValue) ? ((IValue)object).demote() : object;
	}

	/**
	 * Much simpler than using Clojure directly
	 * 
	 * @param instance
	 * @param demote
	 * @return
	 * @throws ThinklabException
	 */
	public static IPersistentMap getRelationships(IInstance instance, boolean demote) 
		throws ThinklabException {
		
		Collection<IRelationship> c = instance.getRelationships();
		Hashtable<String, ISeq> lists = new Hashtable<String, ISeq>();
		
		for (IRelationship r : c) {
			
			String k = r.getProperty().toString();
			Object v = demote ? demote(r.getValue()) : r.getValue();
			
			if (lists.contains(k)) {
				lists.put(k, lists.get(k).cons(v));
			} else {
				lists.put(k, RT.list(v));
			}
		}

		Object[] o = new Object[lists.size() * 2];
		int i = 0;
		
		for (Map.Entry<String, ISeq> entry : lists.entrySet()) {
			o[i++] = entry.getKey();
			o[i++] = entry.getValue();
		}
		
		return RT.map(o);
	}
	
	
	public static Collection<Object> getPropertyValues(IInstance instance, String property) throws ThinklabException {
		
		ArrayList<Object> ret = new ArrayList<Object>();
		
		for (IRelationship r : instance.getRelationships(property)) {
			ret.add(demote(r.getValue()));
		}
		
		return ret;
		
	}
}
