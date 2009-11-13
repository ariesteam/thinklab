package org.integratedmodelling.geospace.interfaces;

import java.util.Collection;
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
	 * If the gazetteer manages a collection of known localities, return the names. This one should
	 * just be a no-op if the list of known localities cannot be pre-determined, such as in most web
	 * gazetteers.
	 *  
	 * @param container the collection to add names to. If null, should return a new collection.
	 * @return
	 */
	public abstract Collection<String> getKnownNames(Collection<String> container);
	
}
