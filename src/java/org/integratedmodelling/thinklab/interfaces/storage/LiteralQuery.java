package org.integratedmodelling.thinklab.interfaces.storage;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/**
 * Specific subclasses of Query capable of handling literals should be able to
 * return a concept and the necessary arguments so that an external object can
 * rewrite the query for a host infrastructure and perform it over another 
 * object or collection of objects. The concept identifies the type of comparison
 * requested (e.g. Equals or Intersects), and the objects, if any, define what to 
 * compare it to. These are passed to a KboxTypeAdapter to rewrite queries on
 * specific host infrastructures.
 * 
 * @author Ferd
 *
 */
public interface LiteralQuery {

	public abstract Pair<IConcept, Object[]> getQueryParameters();
}
