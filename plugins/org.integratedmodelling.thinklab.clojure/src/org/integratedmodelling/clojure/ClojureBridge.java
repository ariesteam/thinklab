package org.integratedmodelling.clojure;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.DynamicClassLoader;
import clojure.lang.IPersistentMap;
import clojure.lang.ISeq;
import clojure.lang.RT;

/**
 * Functions to support integrating thinklab with clojure, mostly called within thinklab.clj.
 * @author Ferdinando
 *
 */
public class ClojureBridge {
	
	public static Polylist list2p(ISeq list) {
	
		return null;
		
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
	
	public static void chainClassloader(ClassLoader cl) {
		RT.ROOT_CLASSLOADER = new MultiClassLoader(RT.ROOT_CLASSLOADER, cl);
	}
	
}
