package org.integratedmodelling.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Utility to reduce the ugliness of casting generic collections in Java. If you 
 * have say a Collection<A> (ca) that you know is a Collection<B extends A> and you need
 * a Collection<B>, do the following:
 * 
 * Collection<B> cb = new Cast<A,B>.cast(ca);
 * 
 * and type safety be damned. This will not generate any warning and will avoid 
 * any silly copy. Works for generic collections, arraylists and hashsets - add
 * more if needed. Needs something else for maps.
 * 
 * @author ferdinando.villa
 *
 * @param <B>
 * @param <T>
 */
public class Cast<B, T extends B> {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<T> cast(Collection<B> b) {
		return (Collection<T>)(Collection)b;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<T> cast(ArrayList<B> b) {
		return (ArrayList<T>)(ArrayList)b;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashSet<T> cast(HashSet<B> b) {
		return (HashSet<T>)(HashSet)b;
	}
}