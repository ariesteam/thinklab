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
			
			// TODO accept dynamic state specs
			
		} else if (keyword.equals(":derivative")) {
			
			// TODO accept dynamic derivative specs
			
		} else super.applyClause(keyword, argument);
	}
	
}
