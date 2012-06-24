package org.integratedmodelling.thinklab.modelling.lang;

import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.modelling.parsing.IConceptDefinition;

@Concept(NS.CONCEPT_DEFINITION)
public class ConceptObject extends ModelObject<ConceptObject> implements IConceptDefinition {

	@Override
	public ConceptObject demote() {
		return this;
	}

	@Override
	public String getName() {
		
		/*
		 * namespace == null only happens in error, but let it through so
		 * we don't get a null pointer exception, and we report the error 
		 * anyway.
		 */
		String ns = "UNDEFINED";
		if (getNamespace() != null)
			ns = getNamespace().getId();
		return ns + ":" + _id;
	}
}
