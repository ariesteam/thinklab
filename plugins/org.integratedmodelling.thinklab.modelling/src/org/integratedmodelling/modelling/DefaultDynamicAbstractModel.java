package org.integratedmodelling.modelling;


import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

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

	@Override
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
		lang = ((DefaultDynamicAbstractModel)model).lang;
		dynSpecs = ((DefaultDynamicAbstractModel)model).dynSpecs;
	}

	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getConfigurableClone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
