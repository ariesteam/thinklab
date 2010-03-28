package org.integratedmodelling.modelling;


import org.integratedmodelling.thinklab.exception.ThinklabException;

import clojure.lang.IFn;

/**
 * Just adds handling of state and derivative clauses followed by executable code.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class DefaultDynamicAbstractModel extends DefaultStatefulAbstractModel {

	protected Object dynSpecs = null;
	public enum language {
		CLOJURE,
		MVEL
	};
	protected language lang = null;
	
	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		if (keyword.equals(":state") && (argument instanceof IFn)) {
			this.dynSpecs = argument;
			lang = language.CLOJURE;
		} else if (keyword.equals(":state") && (argument instanceof String)) {
			this.dynSpecs = argument;
			lang = language.MVEL;
		} else if (keyword.equals(":derivative")) {
			
			// TODO accept dynamic derivative specs
			
		} else if (keyword.equals(":probability")) {
			
			// TODO accept bayesian node form
			
		} else super.applyClause(keyword, argument);
	}
	
	@Override
	public boolean isResolved() {
		return super.isResolved() || this.dynSpecs != null;
	}
	
}
