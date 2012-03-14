package org.integratedmodelling.thinklab.query.operators;

import org.integratedmodelling.thinklab.query.Query;

/**
 * Identity selects objects that have identical data properties AND object
 * properties. That means two men with the same age, name, address, wife and
 * children. Conformity checks only the object properties, Equality checks
 * only the data properties.
 *  
 * @author Ferd
 *
 */
public class Identity extends Query {

	public Identity(Object object) {
		
	}
}
