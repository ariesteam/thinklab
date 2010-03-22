package org.integratedmodelling.geospace.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public interface IGazetteer {

	/**
	 * Lookup a name in the gazetteer.
	 * 
	 * @param name a string to lookup
	 * @param container the collection to add shapes to. If null, should return a new collection.
	 * @param options properties that can influence the behavior. Must accept a null for defaults.
	 * @return a possibly empty collection of shapes. If container was not null, the result must
	 * 		   contain the shapes in it.
	 */
	public abstract Collection<ShapeValue> resolve(String name, Collection<ShapeValue> container, Properties options)
		throws ThinklabException;
	
	/**
	 * Lookup a name in the gazetteer using a textual search. This should be google-like in
	 * behavior. If the gazetteer does not implement this behavior, don't throw an exception,
	 * simply return an empty collection (or the passed container if not null).
	 * 
	 * @param name a string to lookup
	 * @param container the collection to add shapes to. If null, should return a new collection.
	 * @param options properties that can influence the behavior. Must accept a null for defaults.
	 * @return a possibly empty collection of shapes. If container was not null, the result must
	 * 		   contain the shapes in it.
	 */
	public abstract Collection<ShapeValue> findLocations(String name, Collection<ShapeValue> container)
		throws ThinklabException;
	
	
	/**
	 * If the gazetteer manages a collection of known localities, return the names. This one should
	 * just be a no-op if the list of known localities cannot be pre-determined, such as in most web
	 * gazetteers.
	 *  
	 * @param container the collection to add names to. If null, should return a new collection.
	 * @return
	 */
	public abstract Collection<String> getKnownNames(Collection<String> container);
	
	/**
	 * Return true if the server can import new locations.
	 * @return
	 */
	public abstract boolean isReadOnly();
	
	/**
	 * Import locations from a url. Won't be called if isReadOnly returns true.
	 */
	public abstract void importLocations(String url) throws ThinklabException;

	/**
	 * Add locations directly. Won't be called if isReadOnly returns true.
	 */
	public abstract void addLocation(String id, ShapeValue shape, Map<String,Object> metadata) 
		throws ThinklabException;

	/**
	 * Gazetteers must have an empty constructor and must be fully initialized
	 * using this function. If they don't they can not be declared in plugin.xml.
	 * 
	 * @param properties
	 * @throws ThinklabException 
	 */
	public abstract void initialize(Properties properties) throws ThinklabException;

}
