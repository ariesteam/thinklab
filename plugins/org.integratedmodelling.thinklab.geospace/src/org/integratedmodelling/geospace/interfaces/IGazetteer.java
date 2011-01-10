package org.integratedmodelling.geospace.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;

/**
 * A gazetteer is a queriable so it can be serched with whatever query its method parseQuery will
 * return. The QueryResult returned from a query must have a "shape" field (SHAPE_FIELD in 
 * constants) which returns the ShapeValue corresponding to the result. All other fields are
 * implementation-dependent, but as a rule we should have "id" and "name" always, plus 
 * "label" and "description" optionally.
 * 
 * The gazetteer can also be searched using resolve(), which will normally return one result
 * searched by unique ID, although the implementation does not enforce that.
 * 
 * @author Ferdinando
 *
 */
public interface IGazetteer extends IQueriable {

	public static final String SHAPE_FIELD = "shape";
	public static final String PRIORITY_PROPERTY = "gazetteer.priority";
	
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
	
	/**
	 * Return true if the server can import new locations.
	 * @return
	 */
	public abstract boolean isReadOnly();
	
	/**
	 * Import locations from a url. Won't be called if isReadOnly returns true.
	 * @param properties TODO
	 */
	public abstract void importLocations(String url, Properties properties) throws ThinklabException;

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

	/**
	 * Gazetteers can specify a priority (0 = highest) so that they are consulted sooner
	 * or later when a name is looked up. A good value for a database-backed, multiple-value
	 * database should be 128 or so - 0 should be reserved for local, user-defined gazetteers
	 * that have few unambiguous results.
	 * 
	 * @return
	 */
	public abstract int getPriority();

	/**
	 * After calling this one, the gazetteer must be empty and ready for new
	 * content.
	 * @throws ThinklabException 
	 */
	public abstract void resetToEmpty() throws ThinklabException;
	
}
