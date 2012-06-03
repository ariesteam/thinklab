package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IClassifyingObserverDefinition;

@Concept(NS.CLASSIFYING_OBSERVER)
public class Classification extends Observer<Classification> implements IClassifyingObserverDefinition {

	IConcept _conceptSpace;
	
	@Override
	public IConcept getConceptSpace() {
		return _conceptSpace;
	}

	@Override
	public void setConceptSpace(IConcept concept) {
		_conceptSpace = concept;
	}

	@Override
	public Classification demote() {
		return this;
	}

	@Override
	public IState createState(ISemanticObject<?> observable, IContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
