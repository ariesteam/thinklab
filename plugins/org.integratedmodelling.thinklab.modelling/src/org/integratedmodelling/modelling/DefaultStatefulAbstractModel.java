package org.integratedmodelling.modelling;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.Polylist;

public abstract class DefaultStatefulAbstractModel extends DefaultAbstractModel {

	protected Object state = null;

	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		System.out.println(this + "processing clause " + keyword + " -> " + argument);
		
		if (keyword.equals(":state")) {

			/**
			 * TODO this only validates ATOMIC states; this one should recognize computed 
			 * states, vectors, sets as well, and call validateState repeatedly as necessary.
			 */
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
