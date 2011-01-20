package org.integratedmodelling.modelling.model;

import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

import umontreal.iro.lecuyer.probdist.Distribution;

public abstract class DefaultStatefulAbstractModel extends DefaultAbstractModel {

	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		if (keyword.equals(":value")) {
			if (argument instanceof Distribution) {
				distribution = new DistributionValue((Distribution)argument);
			} else {
				state = validateState(argument);			
			}
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

	public Object getState() {
		return this.state;
	}
	
	@Override
	public boolean isStateful() {
		return true;
	}
}
