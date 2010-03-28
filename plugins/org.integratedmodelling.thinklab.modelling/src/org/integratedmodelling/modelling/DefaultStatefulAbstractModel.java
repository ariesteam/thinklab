package org.integratedmodelling.modelling;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

public abstract class DefaultStatefulAbstractModel extends DefaultAbstractModel {

	protected Object state = null;

	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		// System.out.println(this + "processing clause " + keyword + " -> " + argument);
		
		if (keyword.equals(":value")) {
			state = validateState(state);			
		} else super.applyClause(keyword, argument);
	}
		
	
	protected abstract Object validateState(Object state) throws ThinklabValidationException;
		
	/*
	 * Copy the relevant fields when a clone is created before configuration
	 */
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
		state = model.state;		
	}

}
