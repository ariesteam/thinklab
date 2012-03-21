package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.lang.parsing.IConceptDefinition;

@Concept(NS.CONCEPT_DEFINITION)
public class ConceptObject extends ModelObject<ConceptObject> implements IConceptDefinition {

	@Override
	public ConceptObject demote() {
		return this;
	}

}
