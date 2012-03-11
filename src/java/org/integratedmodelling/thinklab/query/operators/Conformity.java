package org.integratedmodelling.thinklab.query.operators;

import org.integratedmodelling.thinklab.query.Query;

/**
 * Conforms is a semantic operator that will select an object that is conformant to
 * what it's been created with. As a default, conformance means that all object properties
 * target conformant objects, but no data properties are checked. Equality checks
 * the data properties and Identity checks both.
 * 
 * Restrict the result to select more fields for conformance. For example, a man with a 
 * wife and two children conforms to another man with a wife and two children. A 
 * Conforms(the_man).restrict(AGE_PROPERTY, 50) constraint will only make them conform 
 * if they're both 50.
 * 
 * @author Ferd
 *
 */
public class Conformity extends Query {

	public Conformity(Object object) {
		
	}
}
