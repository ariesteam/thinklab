package org.integratedmodelling.modelling;


import org.integratedmodelling.thinklab.exception.ThinklabException;

import clojure.lang.ISeq;

/**
 * Just adds handling of state and derivative clauses followed by executable code.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class DefaultDynamicAbstractModel extends DefaultStatefulAbstractModel {

	
	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		if (keyword.equals(":state") && (argument instanceof ISeq)) {
			
			System.out.println("got state " + argument);
			
		} else if (keyword.equals(":derivative")) {
			
			// TODO accept dynamic derivative specs
			
		} else if (keyword.equals(":probability")) {
			
			// TODO accept bayesian node form
			
		} else super.applyClause(keyword, argument);
	}
	
}
