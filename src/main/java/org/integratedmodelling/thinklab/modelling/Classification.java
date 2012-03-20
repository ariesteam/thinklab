package org.integratedmodelling.thinklab.modelling;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.parsing.IClassifyingObserverDefinition;

public class Classification extends Observer implements IClassifyingObserverDefinition {

	IConcept _conceptSpace;
	
	@Override
	public IConcept getConceptSpace() {
		return _conceptSpace;
	}

	@Override
	public void setConceptSpace(IConcept concept) {
		_conceptSpace = concept;
	}

}
