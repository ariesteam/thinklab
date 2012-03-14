package org.integratedmodelling.thinklab.query.operators;

import org.integratedmodelling.thinklab.query.Query;

/**
 * Equality is a semantic operator that will select an object that is equal to
 * what it's been created with. As a default, equality means that all data 
 * properties are to equal fields. Object properties are not checked. 
 * 
 * @author Ferd
 * @see Conformity, Identity
 */
public class Equality extends Query {

	public Equality(Object object) {
		
	}
}
